package com.randomcoder.crypto;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import junit.framework.TestCase;

import com.randomcoder.test.TestObjectFactory;

public class KeystoreCertificateFactoryBeanTest extends TestCase
{
	private KeystoreCertificateFactoryBean target;
	
	@Override
	public void setUp() throws Exception
	{
		target = TestObjectFactory.getKeystoreCertificateFactoryBean();
	}

	@Override
	public void tearDown() throws Exception
	{}

	public void testGetObjectType()
	{
		assertEquals(CertificateContext.class, target.getObjectType());
	}

	public void testIsSingleton()
	{
		assertFalse(target.isSingleton());
	}

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
