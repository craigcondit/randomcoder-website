package com.randomcoder.security.cardspace;

import java.io.StringReader;
import java.security.PublicKey;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.acegisecurity.*;
import org.acegisecurity.ui.AbstractProcessingFilter;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import com.randomcoder.crypto.CertificateContext;
import com.randomcoder.saml.*;
import com.randomcoder.xml.XmlUtils;
import com.randomcoder.xml.security.XmlSecurityUtils;

/**
 * Processes a Windows CardSpace Information Card.
 * 
 * <p>
 * Login forms must present a single parameter (by default
 * <code>xmlToken</code> which contains an encrypted SAML token.
 * </p>
 * 
 * <p>
 * <b>Do not use this class directly.</b>
 * Instead configure <code>web.xml</code> to use the
 * {@link org.acegisecurity.util.FilterToBeanProxy}.</p>
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre> 
 */
public class CardSpaceProcessingFilter extends AbstractProcessingFilter
{
	private static final String DEFAULT_PARAMETER = "xmlToken";
	
	private CertificateContext certificateContext;
	private String parameter = DEFAULT_PARAMETER;
	private boolean debug = false;
	
	/**
	 * Sets the certificate context to retrieve private keys and certificates from.
	 * @param certificateContext certificate context
	 */
	@Required
	public void setCertificateContext(CertificateContext certificateContext)
	{
		this.certificateContext = certificateContext;
	}

	/**
	 * Sets the name of the parameter to read the xml token from (defaults to
	 * <code>xmlToken</code>.
	 * @param parameter paramater name.
	 */
	public void setParameter(String parameter)
	{
		this.parameter = parameter;
	}
	
	/**
	 * Turns on or off debugging output of XML tokens (defaults to off).
	 * @param debug true to enable debugging, false otherwise
	 */
	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}
	
	/**
	 * This filter by default responds to /j_acegi_cardspace_check
	 * @return the default 
	 */
	@Override
	public String getDefaultFilterProcessesUrl()
	{
		return "/j_acegi_cardspace_check";
	}

	/**
	 * Attempts authentication using CardSpace tokens.
	 * @param request HTTP servlet request
	 * @throws AuthenticationException if the token is missing or invalid
	 * @return authenticated <code>CardSpaceAuthenticationToken</code>
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request)
	throws AuthenticationException
	{
		logger.debug("attemptAuthentication()");
		
		// retrieve token
		String xmlToken = getXmlToken(request);
		
		if (debug) logger.debug("XML Token: " + xmlToken);
		
		// parse token
		Document doc = parseXmlToken(xmlToken);
		
		if (debug) XmlUtils.logXml(logger, "Encrypted Token:", doc);

		// decrypt token
		decryptXmlToken(doc);

		if (debug) XmlUtils.logXml(logger, "Decrypted Token:", doc);
		
		// find Assertion element
		Element assertion = findAssertion(doc);
				
		// find Signature element
		Element signature = findSignature(doc);
				
		// verify signature
		PublicKey publicKey = verifySignature(assertion, signature);
		
		// build a SAML assertion
		SamlAssertion samlAssertion = buildSamlAssertion(assertion);
		
		// build credentials
		CardSpaceCredentials credentials
			= new CardSpaceCredentials(samlAssertion, publicKey, new Date());
		
		// build an authentication token
		CardSpaceAuthenticationToken authToken
			= new CardSpaceAuthenticationToken(credentials);
		
		// verify the token
		return getAuthenticationManager().authenticate(authToken);
	}

	private String getXmlToken(HttpServletRequest request)
	throws InvalidCredentialsException
	{
		String xmlToken = request.getParameter(parameter);
		
		if (xmlToken == null)
			throw new InvalidCredentialsException("No information card presented.");
		
		return xmlToken;
	}

	private Document parseXmlToken(String xmlToken)
	throws InvalidCredentialsException, AuthenticationServiceException
	{
		
		StringReader reader = null;
		InputSource source = null;
		
		try
		{
			reader = new StringReader(xmlToken);
			source = new InputSource(reader);
			
			return XmlUtils.parseXml(source);
		}
		catch (Exception e)
		{
			throw new InvalidCredentialsException("Unable to parse xml token", e);
		}
		finally
		{
			if (reader != null) reader.close();
		}
	}

	private void decryptXmlToken(Document doc)
	throws InvalidCredentialsException
	{
		try
		{
			Element encryptedData = XmlSecurityUtils.findFirstEncryptedData(doc);
			
			if (encryptedData == null)
				throw new InvalidCredentialsException("EncryptedData not found");
			
			XmlSecurityUtils.decrypt(doc, encryptedData, certificateContext.getPrivateKey());
		}
		catch (Exception e)
		{
			throw new InvalidCredentialsException("Unable to decrypt token", e);
		}
	}

	private Element findAssertion(Document doc)
	throws InvalidCredentialsException
	{
		Element assertion = SamlUtils.findFirstSamlAssertion(doc);
		if (assertion == null)
			throw new InvalidCredentialsException("Assertion not found");
		
		return assertion;
	}

	private Element findSignature(Document doc)
	throws InvalidCredentialsException
	{
		Element signature = XmlSecurityUtils.findFirstSignature(doc);
		if (signature == null)
			throw new InvalidCredentialsException("Signature not found");
		
		return signature;
	}

	private PublicKey verifySignature(Element assertion, Element signature)
	throws InvalidCredentialsException
	{
		try
		{
			// tag the AssertionID attribute as an ID type so that signature
			// validation works
			if (assertion.hasAttributeNS(null, "AssertionID"))
				assertion.setIdAttributeNS(null, "AssertionID", true);
			
			return XmlSecurityUtils.verifySignature(signature);
		}
		catch (Exception e)
		{
			throw new InvalidCredentialsException("Unable to verify signature", e);
		}
	}

	private SamlAssertion buildSamlAssertion(Element assertion)
	throws InvalidCredentialsException
	{
		SamlAssertion samlAssertion = null;
		try
		{
			samlAssertion = new SamlAssertion(assertion);
		}
		catch (Exception e)
		{
			throw new InvalidCredentialsException("SAML token invalid", e);
		}
		return samlAssertion;
	}
	
}