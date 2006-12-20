package com.randomcoder.security.cardspace;

import java.io.*;
import java.security.PublicKey;

import javax.servlet.http.HttpServletRequest;

import org.acegisecurity.*;
import org.acegisecurity.ui.AbstractProcessingFilter;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.*;
import org.xml.sax.*;

import com.randomcoder.crypto.CertificateContext;
import com.randomcoder.saml.*;
import com.randomcoder.xml.XmlUtils;
import com.randomcoder.xml.security.*;

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
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request)
	throws AuthenticationException
	{
		String xmlToken = request.getParameter(parameter);
		if (xmlToken == null)
			throw new InvalidCredentialsException("No information card presented.");
		
		// parse into xml
		Document doc = null;
		try
		{
			doc = XmlUtils.parseXml(new InputSource(new StringReader(xmlToken)));
		}
		catch (SAXException e)
		{
			throw new InvalidCredentialsException("Unable to parse xml token", e);
		}
		catch (IOException e)
		{
			throw new AuthenticationServiceException("Unable to parse xml token", e);
		}

		// get encrypted data element
		Element encryptedData = XmlSecurityUtils.findFirstEncryptedData(doc);
		if (encryptedData == null)
			throw new InvalidCredentialsException("EncryptedData not found");
		
		// decrypt it
		try
		{
			XmlSecurityUtils.decrypt(doc, encryptedData, certificateContext.getPrivateKey());
		}
		catch (XmlSecurityConfigurationException e)
		{
			throw new AuthenticationServiceException("Unable to decrypt token", e);
		}
		catch (XmlSecurityException e)
		{
			throw new InvalidCredentialsException("Unable to decrypt token", e);
		}

		// get assertion element
		Element assertionElement = SamlUtils.findFirstSamlAssertion(doc);
		if (assertionElement == null)
			throw new InvalidCredentialsException("Assertion not found");
		
		// mark the AssertionID attribute as an ID
		if (assertionElement.hasAttributeNS(null, "AssertionID"))
			assertionElement.setIdAttributeNS(null, "AssertionID", true);
				
		// get signature element
		Element signature = XmlSecurityUtils.findFirstSignature(doc);
		if (signature == null)
			throw new InvalidCredentialsException("Signature not found");
				
		// verify signature
		PublicKey publicKey = null;
		try
		{
			publicKey = XmlSecurityUtils.verifySignature(signature);
		}
		catch (XmlSecurityException e)
		{
			throw new InvalidCredentialsException("Unable to verify signature", e);
		}
		
		SamlAssertion assertion = null;
		try
		{
			assertion = new SamlAssertion(assertionElement);
		}
		catch (SamlException e)
		{
			throw new InvalidCredentialsException("SAML token invalid", e);
		}
		
		CardSpaceCredentials credentials = new CardSpaceCredentials(assertion, publicKey);
		
		CardSpaceAuthenticationToken authToken = new CardSpaceAuthenticationToken(credentials);
		
		return getAuthenticationManager().authenticate(authToken);
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

}
