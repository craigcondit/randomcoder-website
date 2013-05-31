package org.randomcoder.security;

import junit.framework.TestCase;

import org.acegisecurity.Authentication;
import org.springframework.mock.web.*;

import org.randomcoder.test.mock.acegisecurity.*;

@SuppressWarnings("javadoc")
public class NullLogoutHandlerTest extends TestCase
{
	private NullLogoutHandler handler = null;
	private LogoutHandlerMock mock = null;
	private MockHttpServletRequest request = null;
	private MockHttpServletResponse response = null;
	
	@Override
	public void setUp() throws Exception
	{
		mock = new LogoutHandlerMock();
		handler = new NullLogoutHandler();
		handler.setUsername("anonymousUser");
		handler.setLogoutHandler(mock);
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@Override
	public void tearDown() throws Exception
	{
		handler = null;
		mock = null;
		request = null;
		response = null;
	}

	public void testLogoutNormal()
	{
		handler.logout(request, response, new AuthenticationMock());
		Authentication auth = mock.getAuthentication();
		assertNotNull(auth);
		assertEquals(AuthenticationMock.class, auth.getClass());		
	}
		
	public void testLogoutNull()
	{
		handler.logout(request, response, null);
		Authentication auth = mock.getAuthentication();
		assertNotNull(auth);
		
		assertEquals("anonymousUser", auth.getName());
		assertEquals("anonymousUser", auth.getPrincipal());
		assertFalse(auth.isAuthenticated());

		// for code coverage
		auth.getAuthorities(); 
		auth.getCredentials(); 
		auth.getDetails();
		auth.setAuthenticated(false);		
		try
		{
			auth.setAuthenticated(true);
			fail("Didn't catch exception");
		}
		catch (IllegalArgumentException e) {}
	}		
}
