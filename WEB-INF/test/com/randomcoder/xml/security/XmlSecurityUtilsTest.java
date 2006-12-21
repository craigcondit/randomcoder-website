package com.randomcoder.xml.security;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.security.*;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.junit.*;
import org.springframework.core.io.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import com.randomcoder.crypto.*;
import com.randomcoder.saml.SamlUtils;
import com.randomcoder.xml.XmlUtils;

public class XmlSecurityUtilsTest
{
	private static final String RES_ENCRYPTED = "/xmlsec/saml-encrypted.xml";
	private static final String RES_XMLSEC_PROPS = "/xmlsec/xmlsec.properties";
	
	private Document encryptedData;
	private String clientPublicKey;
	private PrivateKey serverPrivateKey;
	
	@Before
	public void setUp() throws Exception
	{		
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream(RES_XMLSEC_PROPS));

		KeystoreCertificateFactoryBean keystoreFactory = new KeystoreCertificateFactoryBean();
		
		Resource keystoreLocation = new ClassPathResource(properties.getProperty("keystore.resource"));
		
		keystoreFactory.setKeystoreLocation(keystoreLocation);
		keystoreFactory.setKeystoreType(properties.getProperty("keystore.type"));
		keystoreFactory.setKeystorePassword(properties.getProperty("keystore.password"));
		keystoreFactory.setCertificateAlias(properties.getProperty("certificate.alias"));
		keystoreFactory.setCertificatePassword(properties.getProperty("certificate.password"));
		keystoreFactory.afterPropertiesSet();
		CertificateContext certContext = (CertificateContext) keystoreFactory.getObject();
		
		encryptedData = XmlUtils.parseXml(new InputSource(getClass().getResourceAsStream(RES_ENCRYPTED)));		
		clientPublicKey = properties.getProperty("client.publickey.encoded");
		serverPrivateKey = certContext.getPrivateKey();		
	}

	@After
	public void tearDown() throws Exception
	{
		encryptedData = null;
		clientPublicKey = null;
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
		assertEquals(encoded, clientPublicKey);
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
