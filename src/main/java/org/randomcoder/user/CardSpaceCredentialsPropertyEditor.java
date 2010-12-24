package org.randomcoder.user;

import java.beans.PropertyEditorSupport;
import java.io.*;
import java.security.PublicKey;
import java.util.Date;

import org.w3c.dom.*;
import org.xml.sax.*;

import org.randomcoder.crypto.CertificateContext;
import org.randomcoder.saml.*;
import org.randomcoder.security.cardspace.*;
import org.randomcoder.xml.XmlUtils;
import org.randomcoder.xml.security.*;

/**
 * Property editor for CardSpace tokens.
 * 
 * <pre>
 * Copyright (c) 2007, Craig Condit. All rights reserved.
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
public class CardSpaceCredentialsPropertyEditor extends PropertyEditorSupport
{
	private final CertificateContext certificateContext;
	
	public CardSpaceCredentialsPropertyEditor(CertificateContext certificateContext)
	{
		this.certificateContext = certificateContext;
	}

	/**
	 * This method always returns an empty string, as tokens cannot be reversed.
	 * @return empty string
	 */
	@Override
	public String getAsText()
	{
		return "";
	}

	/**
	 * Sets the value of the associated object as a text string.
	 * @param string text value
	 * @throws IllegalArgumentException if parsing fails
	 */
	@Override
	public void setAsText(String string) throws IllegalArgumentException
	{
		if (string == null || string.trim().length() == 0)
		{
			setValue(null);
			return;
		}
		
		try
		{
			// parse token
			Document doc = parseXmlToken(string);

			// decrypt token
			decryptXmlToken(doc);
					
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
			
			setValue(credentials);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("The information card you submitted was invalid.", e);
		}
	}
	
	private Document parseXmlToken(String xmlToken)
	throws IOException, SAXException
	{
		
		StringReader reader = null;
		InputSource source = null;
		
		try
		{
			reader = new StringReader(xmlToken);
			source = new InputSource(reader);
			
			return XmlUtils.parseXml(source);
		}
		finally
		{
			if (reader != null) reader.close();
		}
	}
	
	private void decryptXmlToken(Document doc)
	throws IllegalArgumentException, XmlSecurityException, XmlSecurityConfigurationException
	{
		Element encryptedData = XmlSecurityUtils.findFirstEncryptedData(doc);
		
		if (encryptedData == null)
			throw new IllegalArgumentException("No encrypted data found");
		
		XmlSecurityUtils.decrypt(doc, encryptedData, certificateContext.getPrivateKey());
	}

	private Element findAssertion(Document doc)
	throws IllegalArgumentException
	{
		Element assertion = SamlUtils.findFirstSamlAssertion(doc);
		if (assertion == null)
			throw new IllegalArgumentException("No SAML assertion found");
		
		return assertion;
	}

	private Element findSignature(Document doc)
	throws IllegalArgumentException
	{
		Element signature = XmlSecurityUtils.findFirstSignature(doc);
		if (signature == null)
			throw new IllegalArgumentException("No signature found");
		
		return signature;
	}

	private PublicKey verifySignature(Element assertion, Element signature)
	throws XmlSecurityException
	{
		// tag the AssertionID attribute as an ID type so that signature
		// validation works
		if (assertion.hasAttributeNS(null, "AssertionID"))
			assertion.setIdAttributeNS(null, "AssertionID", true);
		
		return XmlSecurityUtils.verifySignature(signature);
	}

	private SamlAssertion buildSamlAssertion(Element assertion)
	throws SamlException
	{
		return new SamlAssertion(assertion);
	}
		
}
