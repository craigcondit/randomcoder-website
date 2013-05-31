package org.randomcoder.security;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.mock.web.*;

import org.randomcoder.test.GenericProxy;
import org.randomcoder.test.mock.jee.FilterChainMock;

@SuppressWarnings("javadoc")
public class DisableUrlSessionFilterTest extends TestCase
{
	private DisableUrlSessionFilter filter;
	
	@Override
	public void setUp() throws Exception
	{
		filter = new DisableUrlSessionFilter();
		MockFilterConfig config = new MockFilterConfig();
		filter.init(config);
	}

	@Override
	public void tearDown() throws Exception
	{
		filter.destroy();
		filter = null;
	}

	@SuppressWarnings("deprecation")
	public void testDoFilter() throws Exception
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
	
	public void testNonHttpServletRequest() throws Exception
	{
		FilterChainMock chain = new FilterChainMock();
		
		// proxy the request object so that it no longer implements HttpServletRequest
		ServletRequest request = (ServletRequest) GenericProxy.proxy(new MockHttpServletRequest(), ServletRequest.class);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		filter.doFilter(request, response, chain);		
	}
}
