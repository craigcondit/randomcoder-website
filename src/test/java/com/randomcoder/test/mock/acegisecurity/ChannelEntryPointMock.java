package com.randomcoder.test.mock.acegisecurity;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.acegisecurity.securechannel.ChannelEntryPoint;

public class ChannelEntryPointMock implements ChannelEntryPoint
{
	public ChannelEntryPointMock() {}
	
	private HttpServletRequest req = null;
	
	@Override
	public void commence(ServletRequest _req, ServletResponse resp)
	throws IOException, ServletException
	{
		this.req = (HttpServletRequest) _req;
	}
	
	public HttpServletRequest getRequest()
	{
		return req;
	}
}