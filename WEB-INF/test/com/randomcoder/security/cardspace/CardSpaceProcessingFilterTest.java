package com.randomcoder.security.cardspace;

import static com.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_TEST;
import static org.junit.Assert.*;

import java.io.StringReader;
import java.lang.reflect.*;

import org.acegisecurity.Authentication;
import org.junit.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import com.randomcoder.test.TestObjectFactory;
import com.randomcoder.test.mock.acegisecurity.AuthenticationManagerMock;
import com.randomcoder.xml.XmlUtils;

public class CardSpaceProcessingFilterTest
{
	private CardSpaceProcessingFilter filter = null;
	private MockHttpServletRequest request = null;
	private String xmlToken = null;
	
	@Before
	public void setUp() throws Exception
	{
		filter = new CardSpaceProcessingFilter();
		filter.setParameter("testToken");
		filter.setDebug(false);
		filter.setCertificateContext(TestObjectFactory.getCertificateContext());
		filter.setAuthenticationManager(new AuthenticationManagerMock());

		request = new MockHttpServletRequest();
		
		xmlToken = TestObjectFactory.getResourceAsString(RESOURCE_SAML_ASSERTION_TEST);
	}

	@After
	public void tearDown() throws Exception
	{
		filter = null;
		request = null;
		xmlToken = null;
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

	@Test
	public void testAttemptAuthenticationSuccess()
	{
		request.setParameter("testToken", xmlToken);
		Authentication auth = filter.attemptAuthentication(request);
		assertNotNull(auth);
		assertEquals(CardSpaceAuthenticationToken.class, auth.getClass());
		
		CardSpaceAuthenticationToken token = (CardSpaceAuthenticationToken) auth;
		
		Object cred = token.getCredentials();
		assertNotNull(cred);
		assertEquals(CardSpaceCredentials.class, cred.getClass());
		
		CardSpaceCredentials credentials = (CardSpaceCredentials) cred;
		
		assertEquals("uuid:b89586f8-4aa8-4157-9db6-bb10afd471eb", credentials.getAssertionId());
	}

	@Test
	public void testAttemptAuthenticationDebug()
	{
		filter.setDebug(true);
		request.setParameter("testToken", xmlToken);
		Authentication auth = filter.attemptAuthentication(request);
		assertNotNull(auth);
		assertEquals(CardSpaceAuthenticationToken.class, auth.getClass());
	}
	
	/**
	 * Test the private findAssertion() method to allow injecting bad data.
	 */
	@Test(expected=InvalidCredentialsException.class)
	public void testFindAssertionInvalid() throws Throwable
	{
		Document doc = XmlUtils.parseXml(new InputSource(new StringReader("<NotAssertion />")));
		
		Method findAssertion = filter.getClass().getDeclaredMethod("findAssertion", Document.class);		
		findAssertion.setAccessible(true);
		
		try
		{
			findAssertion.invoke(filter, doc);
		}
		catch (InvocationTargetException e)
		{
			throw e.getCause();
		}
	}

	/**
	 * Test the private findSignature() method to allow injecting bad data.
	 */
	@Test(expected=InvalidCredentialsException.class)
	public void testFindSignatureInvalid() throws Throwable
	{
		Document doc = XmlUtils.parseXml(new InputSource(new StringReader("<NotSignature />")));
		
		Method findSignature = filter.getClass().getDeclaredMethod("findSignature", Document.class);		
		findSignature.setAccessible(true);
		
		try
		{
			findSignature.invoke(filter, doc);
		}
		catch (InvocationTargetException e)
		{
			throw e.getCause();
		}
	}

	/**
	 * Test the private verifySignature() method to allow injecting bad data.
	 */
	@Test(expected=InvalidCredentialsException.class)
	public void testVerifySignatureInvalid() throws Throwable
	{
		Document doc = XmlUtils.parseXml(new InputSource(new StringReader("<Root><Assertion /><Signature /></Root>")));
		Element assertion = (Element) doc.getElementsByTagName("Assertion").item(0);
		Element signature = (Element) doc.getElementsByTagName("Signature").item(0);
				
		Method verifySignature = filter.getClass().getDeclaredMethod("verifySignature", Element.class, Element.class);		
		verifySignature.setAccessible(true);
		
		try
		{
			verifySignature.invoke(filter, assertion, signature);
		}
		catch (InvocationTargetException e)
		{
			throw e.getCause();
		}
	}

	/**
	 * Test the private buildSamlAssertion() method to allow injecting bad data.
	 */
	@Test(expected=InvalidCredentialsException.class)
	public void testBuildSamlAssertionInvalid() throws Throwable
	{
		Document doc = XmlUtils.parseXml(new InputSource(new StringReader("<Root><Assertion /></Root>")));
		Element assertion = (Element) doc.getElementsByTagName("Assertion").item(0);
				
		Method buildSamlAssertion = filter.getClass().getDeclaredMethod("buildSamlAssertion", Element.class);		
		buildSamlAssertion.setAccessible(true);
		
		try
		{
			buildSamlAssertion.invoke(filter, assertion);
		}
		catch (InvocationTargetException e)
		{
			throw e.getCause();
		}
	}
	
	@Test
	public void testGetDefaultFilterProcessesUrl()
	{
		assertEquals("/j_acegi_cardspace_check", filter.getDefaultFilterProcessesUrl());
	}
}