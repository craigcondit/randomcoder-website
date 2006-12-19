package com.randomcoder.xml.security;

import static org.apache.xml.security.encryption.XMLCipher.DECRYPT_MODE;
import static org.apache.xml.security.utils.Constants.*;
import static org.apache.xml.security.utils.EncryptionConstants.*;

import java.security.*;

import org.apache.xml.security.encryption.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.signature.*;
import org.w3c.dom.*;

/**
 * XML security utilities.
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
public final class XmlSecurityUtils
{
	private XmlSecurityUtils() {}
	
	static
	{
    org.apache.xml.security.Init.init();
	}
	
	/**
	 * Finds the first <code>EncryptedData</code> element in the given document.
	 * @param doc DOM document
	 * @return Element, or null if not found
	 */
	public static Element findFirstEncryptedData(Document doc)
	{
		NodeList encryptedDataList = doc.getElementsByTagNameNS(EncryptionSpecNS, _TAG_ENCRYPTEDDATA);
		if (encryptedDataList == null) return null;
		if (encryptedDataList.getLength() == 0) return null;
		return (Element) encryptedDataList.item(0);				
	}
	
	/**
	 * Finds the first <code>Signature</code> element in the given document.
	 * @param doc DOM document
	 * @return Element, or null if not found
	 */
	public static Element findFirstSignature(Document doc)
	{
		NodeList signatureList = doc.getElementsByTagNameNS(SignatureSpecNS, _TAG_SIGNATURE);
		if (signatureList == null) return null;
		if (signatureList.getLength() == 0) return null;
		return (Element) signatureList.item(0);
	}
	
	/**
	 * Decrypts the given <code>EncryptedData</code> element.
	 * @param doc containing document
	 * @param encryptedData EncryptedData element to decrypt
	 * @param key private key to use for decryption
	 * @throws XmlSecurityException if decryption fails
	 * @throws XmlSecurityConfigurationException if decryption setup fails
	 */
	public static void decrypt(Document doc, Element encryptedData, PrivateKey key)
	throws XmlSecurityException, XmlSecurityConfigurationException
	{
		XMLCipher xmlCipher = null;
		try
		{
			xmlCipher = XMLCipher.getInstance();
			xmlCipher.init(DECRYPT_MODE, null);
		}
		catch (XMLEncryptionException e)
		{
			throw new XmlSecurityConfigurationException("Unable to initialize XMLCipher", e);
		}
		
    xmlCipher.setKEK(key);
    
    try
    {
    	xmlCipher.doFinal(doc, encryptedData);
    }
    catch (Exception e)
    {
    	// stupid api... throwing Exception???
    	throw new XmlSecurityException("Unable to decrypt xml", e);
    }
	}
	
	/**
	 * Verifies an XML signature.
	 * @param signature Signature element to verify
	 * @return PublicKey used for encryption
	 * @throws XmlSecurityException if verification fails
	 */
	public static PublicKey verifySignature(Element signature)
	throws XmlSecurityException
	{
		// create an xml signature
		XMLSignature sig = null;
		try
		{
			sig = new XMLSignature(signature, null);
		}
		catch (XMLSignatureException e)
		{
			throw new XmlSecurityException("Unable to create XML signature", e);
		}
		catch (XMLSecurityException e)
		{
			throw new XmlSecurityException("Unable to create XML signature", e);
		}
		
		// get key info
		KeyInfo keyInfo = sig.getKeyInfo();
		if (keyInfo == null)
			throw new XmlSecurityException("Null KeyInfo");
		
		// get public key
		PublicKey pk;
		try
		{
			pk = keyInfo.getPublicKey();
		}
		catch (KeyResolverException e)
		{
			throw new XmlSecurityException("Unable to find public key", e);
		}
		if (pk == null)
			throw new XmlSecurityException("Null PublicKey");
		
		try
		{
			if (!sig.checkSignatureValue(pk))
				throw new XmlSecurityException("Signature invalid");
		}
		catch (XMLSignatureException e)
		{
			throw new XmlSecurityException("Unable to check signature validity", e);
		}
			
		return pk;
	}	
}
