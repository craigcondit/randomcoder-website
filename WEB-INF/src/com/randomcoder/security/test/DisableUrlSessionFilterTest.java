package com.randomcoder.security.test;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

import org.junit.*;
import org.springframework.mock.web.*;

import com.randomcoder.security.DisableUrlSessionFilter;

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
		
		// TODO what to check?
		HttpServletResponse wrappedResponse = (HttpServletResponse) chain.getResponse();
		
		assertEquals("Wrong encodeURL", "http://localhost/", wrappedResponse.encodeURL("http://localhost/"));
		assertEquals("Wrong encodeRedirectURL", "http://localhost/", wrappedResponse.encodeRedirectURL("http://localhost/"));
	}

	static class FilterChainMock implements FilterChain
	{
		private ServletRequest request;
		private ServletResponse response;
		
		public void doFilter(ServletRequest _request, ServletResponse _response) throws IOException, ServletException
		{
			request = _request;
			response = _response;
		}
		
		public ServletRequest getRequest()
		{
			return request;
		}
		
		public ServletResponse getResponse()
		{
			return response;
		}
		
	}
}
