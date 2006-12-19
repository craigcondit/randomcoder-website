package com.randomcoder.saml.test;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.security.PrivateKey;
import java.util.Properties;

import org.junit.*;
import org.springframework.core.io.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import com.randomcoder.crypto.*;
import com.randomcoder.saml.SamlUtils;
import com.randomcoder.xml.XmlUtils;
import com.randomcoder.xml.security.XmlSecurityUtils;

public class SamlUtilsTest
{
	private static final String RES_ENCRYPTED = "/xmlsec/saml-encrypted.xml";
	private static final String RES_XMLSEC_PROPS = "/xmlsec/xmlsec.properties";
	
	private Document encryptedData;
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
		serverPrivateKey = certContext.getPrivateKey();		
	}

	@After
	public void tearDown() throws Exception
	{
		encryptedData = null;
		serverPrivateKey = null;
	}
	
	@Test
	public void testFindFirstSamlAssertion() throws Exception
	{
		// does not exist in encrypted data
		assertNull(SamlUtils.findFirstSamlAssertion(encryptedData));
		
		// decrypt token
		Element el = XmlSecurityUtils.findFirstEncryptedData(encryptedData);
		XmlSecurityUtils.decrypt(encryptedData, el, serverPrivateKey);
		
		assertNotNull(SamlUtils.findFirstSamlAssertion(encryptedData));
	}
	
	/**
	 * Not a test, but tickles the private constructor.
	 */
	@Test
	public void coverDefaultConstructor() throws Exception
	{
		Constructor c = SamlUtils.class.getDeclaredConstructor(new Class[] {});
		c.setAccessible(true);
		c.newInstance(new Object[] {});
	}		
}
