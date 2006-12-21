package com.randomcoder.crypto;

import static org.junit.Assert.*;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.junit.*;
import org.springframework.core.io.*;

public class KeystoreCertificateFactoryBeanTest
{
	private static final String RES_XMLSEC_PROPS = "/data/xml-security.properties";

	private KeystoreCertificateFactoryBean target;
	
	@Before
	public void setUp() throws Exception
	{
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream(RES_XMLSEC_PROPS));

		target = new KeystoreCertificateFactoryBean();
		
		Resource keystoreLocation = new ClassPathResource(properties.getProperty("keystore.resource"));
		
		target.setKeystoreLocation(keystoreLocation);
		target.setKeystoreType(properties.getProperty("keystore.type"));
		target.setKeystorePassword(properties.getProperty("keystore.password"));
		target.setCertificateAlias(properties.getProperty("certificate.alias"));
		target.setCertificatePassword(properties.getProperty("certificate.password"));
		target.afterPropertiesSet();		
	}

	@After
	public void tearDown() throws Exception
	{}

	@Test
	public void testGetObjectType()
	{
		assertEquals(CertificateContext.class, target.getObjectType());
	}

	@Test
	public void testIsSingleton()
	{
		assertFalse(target.isSingleton());
	}

	@Test
	public void testGetObject() throws Exception
	{
		Object obj = target.getObject();
		assertNotNull(obj);
		assertTrue(obj instanceof CertificateContext);
		CertificateContext context = (CertificateContext) obj;
		
		X509Certificate cert = context.getCertificate();
		assertNotNull(cert);
		
		PrivateKey key = context.getPrivateKey();
		assertNotNull(key);
	}

}
