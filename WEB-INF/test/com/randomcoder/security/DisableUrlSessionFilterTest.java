package com.randomcoder.security;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

import org.junit.*;
import org.springframework.mock.web.*;

import com.randomcoder.test.util.GenericProxy;

public class DisableUrlSessionFilterTest
{
	private DisableUrlSessionFilter filter;
	
	@Before public void setUp() throws Exception
	{
		filter = new DisableUrlSessionFilter();
		MockFilterConfig config = new MockFilterConfig();
		filter.init(config);
	}

	@After public void tearDown() throws Exception
	{
		filter.destroy();
		filter = null;
	}

	@SuppressWarnings("deprecation")
	@Test public void testDoFilter() throws Exception
	{
		FilterChainMock chain = new FilterChainMock();
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();		
		
		request.setRequestedSessionIdFromURL(true);
		request.setSession(new MockHttpSession());
		
		assertNotNull("Missing session", request.getSession(false));
		
		filter.doFilter(request, response, chain);
		
		assertNull("Session still active", request.getSession(false));
		
		HttpServletResponse wrappedResponse = (HttpServletResponse) chain.getResponse();
		
		assertEquals("Wrong encodeURL", "http://localhost/", wrappedResponse.encodeURL("http://localhost/"));
		assertEquals("Wrong encodeUrl", "http://localhost/", wrappedResponse.encodeUrl("http://localhost/"));
		assertEquals("Wrong encodeRedirectURL", "http://localhost/", wrappedResponse.encodeRedirectURL("http://localhost/"));
		assertEquals("Wrong encodeRedirectUrl", "http://localhost/", wrappedResponse.encodeRedirectUrl("http://localhost/"));
	}
	
	/**
	 * Not a test, but covers the case where a non-HttpServletRequest is passed
	 * to the filter. 
	 */
	@Test public void coverNonHttpServletRequest() throws Exception
	{
		FilterChainMock chain = new FilterChainMock();
		
		// proxy the request object so that it no longer implements HttpServletRequest
		ServletRequest request = (ServletRequest) GenericProxy.proxy(new MockHttpServletRequest(), ServletRequest.class);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		filter.doFilter(request, response, chain);		
	}

	protected class FilterChainMock implements FilterChain
	{
		private ServletResponse response;
		
		public void doFilter(ServletRequest _request, ServletResponse _response) throws IOException, ServletException
		{
			response = _response;
		}
		
		protected ServletResponse getResponse()
		{
			return response;
		}	
	}
}
