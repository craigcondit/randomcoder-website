package org.randomcoder.test.mock.jee;

import java.io.IOException;

import javax.servlet.*;

public class FilterChainMock implements FilterChain
{
	private ServletResponse response;
	
	@Override
	public void doFilter(ServletRequest _request, ServletResponse _response) throws IOException, ServletException
	{
		response = _response;
	}
	
	public ServletResponse getResponse()
	{
		return response;
	}	
}
