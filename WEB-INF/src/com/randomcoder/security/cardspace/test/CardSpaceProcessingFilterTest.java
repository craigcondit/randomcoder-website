package com.randomcoder.security.cardspace.test;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.*;
import org.springframework.core.io.*;
import org.springframework.mock.web.MockHttpServletRequest;

import com.randomcoder.crypto.*;
import com.randomcoder.security.cardspace.*;

public class CardSpaceProcessingFilterTest
{
	private static final String RES_XMLSEC_PROPS = "/xmlsec/xmlsec.properties";
	
	private CardSpaceProcessingFilter filter = null;
	private MockHttpServletRequest request = null;
	
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
		
		filter = new CardSpaceProcessingFilter();
		filter.setParameter("testToken");
		filter.setDebug(false);
		filter.setCertificateContext(certContext);
		request = new MockHttpServletRequest();
	}

	@After
	public void tearDown() throws Exception
	{
		filter = null;
		request = null;
	}

	@Test(expected=InvalidCredentialsException.class)
	public void testAttemptAuthenticationNoToken()
	{
		filter.attemptAuthentication(request);
	}

	@Test(expected=InvalidCredentialsException.class)
	public void testAttemptAuthenticationMalformedXml()
	{
		request.setParameter("testToken", "This is not XML");
		filter.attemptAuthentication(request);
	}

	@Test(expected=InvalidCredentialsException.class)
	public void testAttemptAuthenticationInvalidXml()
	{
		request.setParameter("testToken", "<not-a-cardspace-token />");
		filter.attemptAuthentication(request);
	}

	@Test @Ignore
	public void testAttemptAuthenticationSuccess()
	{
		// TODO implement
		fail("Not implemented");
		filter.attemptAuthentication(request);
	}
	
	@Test
	public void testGetDefaultFilterProcessesUrl()
	{
		assertEquals("/j_acegi_cardspace_check", filter.getDefaultFilterProcessesUrl());
	}

}
