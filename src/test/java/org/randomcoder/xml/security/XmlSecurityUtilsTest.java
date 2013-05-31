package org.randomcoder.xml.security;

import static org.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_TEST;

import java.security.*;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.*;

import org.randomcoder.saml.SamlUtils;
import org.randomcoder.test.TestObjectFactory;

@SuppressWarnings("javadoc")
public class XmlSecurityUtilsTest extends TestCase
{
	private Document encryptedData;
	private String encodedClientPublicKey;
	private PrivateKey serverPrivateKey;
	
	@Override
	public void setUp() throws Exception
	{		
		encryptedData = TestObjectFactory.getXmlDocument(RESOURCE_SAML_ASSERTION_TEST);
		encodedClientPublicKey = TestObjectFactory.getEncodedClientPublicKey();
		serverPrivateKey = TestObjectFactory.getPrivateKey();
	}

	@Override
	public void tearDown() throws Exception
	{
		encryptedData = null;
		encodedClientPublicKey = null;
		serverPrivateKey = null;
	}

	public void testFindFirstEncryptedData() throws Exception
	{
		// found in encrypted
		Element el = XmlSecurityUtils.findFirstEncryptedData(encryptedData);
		assertNotNull(el);
		assertEquals("EncryptedData", el.getLocalName());
		XmlSecurityUtils.decrypt(encryptedData, el, serverPrivateKey);
		
		// not found in decrypted
		el = XmlSecurityUtils.findFirstEncryptedData(encryptedData);
		assertNull(el);
	}

	public void testFindFirstSignature() throws Exception
	{
		// not found in encrypted
		Element el = XmlSecurityUtils.findFirstSignature(encryptedData);
		assertNull(el);		
		el = XmlSecurityUtils.findFirstEncryptedData(encryptedData);
		assertNotNull(el);
		assertEquals("EncryptedData", el.getLocalName());
		XmlSecurityUtils.decrypt(encryptedData, el, serverPrivateKey);
		
		// found in decrypted
		el = XmlSecurityUtils.findFirstSignature(encryptedData);
		assertNotNull(el);
	}

	public void testDecrypt() throws Exception
	{
		assertNull(XmlSecurityUtils.findFirstSignature(encryptedData));
		Element ed = XmlSecurityUtils.findFirstEncryptedData(encryptedData);
		XmlSecurityUtils.decrypt(encryptedData, ed, serverPrivateKey);
		assertNotNull(XmlSecurityUtils.findFirstSignature(encryptedData));
	}
	
	public void testDecryptNull() throws Exception
	{
		try
		{
			XmlSecurityUtils.decrypt(null, null, null);
			fail("XmlSecurityException expected");
		}
		catch (XmlSecurityException e)
		{
			// pass
		}
	}

	public void testVerifySignature() throws Exception
	{
		Element ed = XmlSecurityUtils.findFirstEncryptedData(encryptedData);
		XmlSecurityUtils.decrypt(encryptedData, ed, serverPrivateKey);
		
		Element sig = XmlSecurityUtils.findFirstSignature(encryptedData);
		assertNotNull(sig);
		
		Element assertion = SamlUtils.findFirstSamlAssertion(encryptedData);
		assertNotNull(assertion);
		
		assertion.setIdAttribute("AssertionID", true);
		
		PublicKey publicKey = XmlSecurityUtils.verifySignature(sig);
		assertNotNull(publicKey);
		
		String encoded = new String(Base64.encodeBase64(publicKey.getEncoded()), "UTF-8");
		assertEquals(encoded, encodedClientPublicKey);
	}

	public void testVerifySignatureNull() throws Exception
	{
		try
		{
			XmlSecurityUtils.verifySignature(null);
			fail("XmlSecurityException expected");
		}
		catch (XmlSecurityException e)
		{
			// pass
		}
	}

	public void testVerifySignatureInvalidSignature() throws Exception
	{
		try
		{
			Element ed = XmlSecurityUtils.findFirstEncryptedData(encryptedData);
			XmlSecurityUtils.decrypt(encryptedData, ed, serverPrivateKey);
			
			Element sig = XmlSecurityUtils.findFirstSignature(encryptedData);
			assertNotNull(sig);
	
			sig.removeChild(sig.getElementsByTagName("*").item(0));
			
			Element assertion = SamlUtils.findFirstSamlAssertion(encryptedData);
			assertNotNull(assertion);
			
			assertion.setIdAttribute("AssertionID", true);
			
			XmlSecurityUtils.verifySignature(sig);
			fail("XmlSecurityException expected");
		}
		catch (XmlSecurityException e)
		{
			// pass
		}
	}

	public void testVerifySignatureMissingKeyInfo() throws Exception
	{
		try
		{
			Element ed = XmlSecurityUtils.findFirstEncryptedData(encryptedData);
			XmlSecurityUtils.decrypt(encryptedData, ed, serverPrivateKey);
			
			Element sig = XmlSecurityUtils.findFirstSignature(encryptedData);
			assertNotNull(sig);
	
			sig.removeChild(sig.getElementsByTagName("KeyInfo").item(0));
			
			Element assertion = SamlUtils.findFirstSamlAssertion(encryptedData);
			assertNotNull(assertion);
			
			assertion.setIdAttribute("AssertionID", true);
			
			XmlSecurityUtils.verifySignature(sig);
			fail("XmlSecurityException expected");
		}
		catch (XmlSecurityException e)
		{
			// pass
		}
	}

	public void testVerifySignatureBadKeyInfo() throws Exception
	{
		try
		{
			Element ed = XmlSecurityUtils.findFirstEncryptedData(encryptedData);
			XmlSecurityUtils.decrypt(encryptedData, ed, serverPrivateKey);
			
			Element sig = XmlSecurityUtils.findFirstSignature(encryptedData);
			assertNotNull(sig);
	
			Node kv = sig.getElementsByTagName("RSAKeyValue").item(0);
			kv.getParentNode().removeChild(kv);
			
			Element assertion = SamlUtils.findFirstSamlAssertion(encryptedData);
			assertNotNull(assertion);
			
			assertion.setIdAttribute("AssertionID", true);
			
			XmlSecurityUtils.verifySignature(sig);
			fail("XmlSecurityException expected");
		}
		catch (XmlSecurityException e)
		{
			// pass
		}
	}

	public void testVerifySignatureBadAssertionID() throws Exception
	{
		try
		{
			Element ed = XmlSecurityUtils.findFirstEncryptedData(encryptedData);
			XmlSecurityUtils.decrypt(encryptedData, ed, serverPrivateKey);
			
			Element sig = XmlSecurityUtils.findFirstSignature(encryptedData);
			assertNotNull(sig);
			
			Element assertion = SamlUtils.findFirstSamlAssertion(encryptedData);
			assertNotNull(assertion);
			
			assertion.setAttribute("AssertionID", assertion.getAttribute("AssertionID") + "x");
			
			assertion.setIdAttribute("AssertionID", true);
			
			XmlSecurityUtils.verifySignature(sig);
			fail("XmlSecurityException expected");
		}
		catch (XmlSecurityException e)
		{
			// pass
		}
	}

	public void testVerifySignatureInvalid() throws Exception
	{
		try
		{
			Element ed = XmlSecurityUtils.findFirstEncryptedData(encryptedData);
			XmlSecurityUtils.decrypt(encryptedData, ed, serverPrivateKey);
			
			Element sig = XmlSecurityUtils.findFirstSignature(encryptedData);
			assertNotNull(sig);
			
			Element assertion = SamlUtils.findFirstSamlAssertion(encryptedData);
			assertNotNull(assertion);
			
			assertion.setAttribute("bogus", "true");
			
			assertion.setIdAttribute("AssertionID", true);
			
			XmlSecurityUtils.verifySignature(sig);
			fail("XmlSecurityException expected");
		}
		catch (XmlSecurityException e)
		{
			// pass
		}
	}
}
