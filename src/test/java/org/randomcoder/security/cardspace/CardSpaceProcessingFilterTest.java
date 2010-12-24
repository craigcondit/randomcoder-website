package org.randomcoder.security.cardspace;

import static org.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_TEST;

import java.io.StringReader;
import java.lang.reflect.*;

import junit.framework.TestCase;

import org.acegisecurity.Authentication;
import org.springframework.mock.web.MockHttpServletRequest;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import org.randomcoder.test.TestObjectFactory;
import org.randomcoder.test.mock.acegisecurity.AuthenticationManagerMock;
import org.randomcoder.xml.XmlUtils;

public class CardSpaceProcessingFilterTest extends TestCase
{
	private CardSpaceProcessingFilter filter = null;
	private MockHttpServletRequest request = null;
	private String xmlToken = null;
	
	@Override
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

	@Override
	public void tearDown() throws Exception
	{
		filter = null;
		request = null;
		xmlToken = null;
	}

	public void testAttemptAuthenticationNoToken()
	{
		try
		{
			filter.attemptAuthentication(request);
			fail("InvalidCredentialsException expected");
		}
		catch (InvalidCredentialsException e)
		{
			// pass
		}
	}

	public void testAttemptAuthenticationMalformedXml()
	{
		try
		{
			request.setParameter("testToken", "This is not XML");
			filter.attemptAuthentication(request);
			fail("InvalidCredentialsException expected");
		}
		catch (InvalidCredentialsException e)
		{
			// pass
		}
	}

	public void testAttemptAuthenticationInvalidXml()
	{
		try
		{
			request.setParameter("testToken", "<not-a-cardspace-token />");
			filter.attemptAuthentication(request);
			fail("InvalidCredentialsException expected");
		}
		catch (InvalidCredentialsException e)
		{
			// pass
		}
	}

	public void testAttemptAuthenticationSuccess()
	{
		request.setParameter("testToken", xmlToken);
		Authentication auth = filter.attemptAuthentication(request);
		assertNotNull(auth);
		assertEquals(CardSpaceAuthenticationToken.class, auth.getClass());
		
		CardSpaceAuthenticationToken token = (CardSpaceAuthenticationToken) auth;
		assertNull(token.getAuthorities());
		assertNull(token.getPrincipal());
		
		Object cred = token.getCredentials();
		assertNotNull(cred);
		assertEquals(CardSpaceCredentials.class, cred.getClass());
		
		CardSpaceCredentials credentials = (CardSpaceCredentials) cred;
		
		assertEquals("uuid:b89586f8-4aa8-4157-9db6-bb10afd471eb", credentials.getAssertionId());
	}

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
	public void testFindAssertionInvalid() throws Throwable
	{
		try
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
			fail("InvalidCredentialsException expected");
		}
		catch (InvalidCredentialsException e)
		{
			// pass
		}
	}

	/**
	 * Test the private findSignature() method to allow injecting bad data.
	 */
	public void testFindSignatureInvalid() throws Throwable
	{
		try
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
			fail("InvalidCredentialsException expected");
		}
		catch (InvalidCredentialsException e)
		{
			// pass
		}
	}

	/**
	 * Test the private verifySignature() method to allow injecting bad data.
	 */
	public void testVerifySignatureInvalid() throws Throwable
	{
		try
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
			fail("InvalidCredentialsException expected");
		}
		catch (InvalidCredentialsException e)
		{
			// pass
		}
	}

	/**
	 * Test the private buildSamlAssertion() method to allow injecting bad data.
	 */
	public void testBuildSamlAssertionInvalid() throws Throwable
	{
		try
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
			fail("InvalidCredentialsException expected");
		}
		catch (InvalidCredentialsException e)
		{
			// pass
		}
	}
	
	public void testGetDefaultFilterProcessesUrl()
	{
		assertEquals("/j_acegi_cardspace_check", filter.getDefaultFilterProcessesUrl());
	}
}
