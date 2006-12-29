package com.randomcoder.crypto;

import static org.junit.Assert.*;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.junit.*;

import com.randomcoder.test.TestObjectFactory;

public class KeystoreCertificateFactoryBeanTest
{
	private KeystoreCertificateFactoryBean target;
	
	@Before
	public void setUp() throws Exception
	{
		target = TestObjectFactory.getKeystoreCertificateFactoryBean();
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
