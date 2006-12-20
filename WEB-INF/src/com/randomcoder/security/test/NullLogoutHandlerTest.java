package com.randomcoder.security.test;

import static org.junit.Assert.*;

import javax.servlet.http.*;

import org.acegisecurity.*;
import org.acegisecurity.ui.logout.LogoutHandler;
import org.junit.*;
import org.springframework.mock.web.*;

import com.randomcoder.security.NullLogoutHandler;

public class NullLogoutHandlerTest
{
	private NullLogoutHandler handler = null;
	private LogoutHandlerMock mock = null;
	private MockHttpServletRequest request = null;
	private MockHttpServletResponse response = null;
	
	@Before
	public void setUp() throws Exception
	{
		mock = new LogoutHandlerMock();
		handler = new NullLogoutHandler();
		handler.setUsername("anonymousUser");
		handler.setLogoutHandler(mock);
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@After
	public void tearDown() throws Exception
	{
		handler = null;
		mock = null;
		request = null;
		response = null;
	}

	@Test
	public void testLogoutNormal()
	{
		handler.logout(request, response, new AuthenticationMock());
		Authentication auth = mock.getAuthentication();
		assertNotNull(auth);
		assertEquals(AuthenticationMock.class, auth.getClass());
		
		assertEquals("temp", auth.getName());
		assertEquals("temp", auth.getPrincipal());
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
		

	@Test
	public void testLogoutNull()
	{
		handler.logout(request, response, null);
		Authentication auth = mock.getAuthentication();
		assertNotNull(auth);
		assertEquals("anonymousUser", auth.getName());
	}
	
	@SuppressWarnings("unused")
	private static class LogoutHandlerMock implements LogoutHandler
	{		
		private Authentication authentication = null;
		
		public LogoutHandlerMock() {}
		
		public void logout(HttpServletRequest request, HttpServletResponse response, Authentication auth)
		{
			authentication = auth;
		}
		
		public Authentication getAuthentication()
		{
			return authentication;
		}		
	}
	
	@SuppressWarnings("unused")
	private static class AuthenticationMock implements Authentication
	{
		private static final long serialVersionUID = 3105620828874678824L;

		public AuthenticationMock() {}
		
		public GrantedAuthority[] getAuthorities()
		{
			return new GrantedAuthority[] {};
		}

		public Object getCredentials()
		{
			return null;
		}

		public Object getDetails()
		{
			return null;
		}

		public Object getPrincipal()
		{
			return "temp";
		}

		public boolean isAuthenticated()
		{
			return false;
		}

		public void setAuthenticated(boolean authenticated) throws IllegalArgumentException
		{
		}

		public String getName()
		{
			return "temp";
		}
		
	}
}
