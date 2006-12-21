package com.randomcoder.xml.security;

import static com.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_TEST;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.security.*;

import org.apache.commons.codec.binary.Base64;
import org.junit.*;
import org.w3c.dom.*;

import com.randomcoder.saml.SamlUtils;
import com.randomcoder.test.TestObjectFactory;

public class XmlSecurityUtilsTest
{
	private Document encryptedData;
	private String encodedClientPublicKey;
	private PrivateKey serverPrivateKey;
	
	@Before
	public void setUp() throws Exception
	{		
		encryptedData = TestObjectFactory.getXmlDocument(RESOURCE_SAML_ASSERTION_TEST);
		encodedClientPublicKey = TestObjectFactory.getEncodedClientPublicKey();
		serverPrivateKey = TestObjectFactory.getPrivateKey();
	}

	@After
	public void tearDown() throws Exception
	{
		encryptedData = null;
		encodedClientPublicKey = null;
		serverPrivateKey = null;
	}

	@Test
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

	@Test
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

	@Test
	public void testDecrypt() throws Exception
	{
		assertNull(XmlSecurityUtils.findFirstSignature(encryptedData));
		Element ed = XmlSecurityUtils.findFirstEncryptedData(encryptedData);
		XmlSecurityUtils.decrypt(encryptedData, ed, serverPrivateKey);
		assertNotNull(XmlSecurityUtils.findFirstSignature(encryptedData));
	}
	
	@Test(expected=XmlSecurityException.class)
	public void testDecryptNull() throws Exception
	{
		XmlSecurityUtils.decrypt(null, null, null);
	}

	@Test
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

	@Test(expected=XmlSecurityException.class)
	public void testVerifySignatureNull() throws Exception
	{
		XmlSecurityUtils.verifySignature(null);
	}

	@Test(expected=XmlSecurityException.class)
	public void testVerifySignatureInvalidSignature() throws Exception
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
	}

	@Test(expected=XmlSecurityException.class)
	public void testVerifySignatureMissingKeyInfo() throws Exception
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
	}

	@Test(expected=XmlSecurityException.class)
	public void testVerifySignatureBadKeyInfo() throws Exception
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
	}

	@Test(expected=XmlSecurityException.class)
	public void testVerifySignatureBadAssertionID() throws Exception
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
	}

	@Test(expected=XmlSecurityException.class)
	public void testVerifySignatureInvalid() throws Exception
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
	}

	/**
	 * Not a test, but tickles the private constructor.
	 */
	@Test
	public void coverDefaultConstructor() throws Exception
	{
		Constructor c = XmlSecurityUtils.class.getDeclaredConstructor(new Class[] {});
		c.setAccessible(true);
		c.newInstance(new Object[] {});
	}		
}
