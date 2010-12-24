package org.randomcoder.security;

import junit.framework.TestCase;

import org.springframework.mock.web.*;

import org.randomcoder.test.mock.acegisecurity.ChannelEntryPointMock;

public class UrlMutatingChannelEntryPointTest extends TestCase
{
	private UrlMutatingChannelEntryPoint entryPoint = null;
	private ChannelEntryPointMock channel = null;
	private MockHttpServletRequest request = null;
	private MockHttpServletResponse response = null;

	@Override
	public void setUp() throws Exception
	{
		entryPoint = new UrlMutatingChannelEntryPoint();
		channel = new ChannelEntryPointMock();
		entryPoint.setChannelEntryPoint(channel);
		entryPoint.setSuffix("/index.jsp");
		entryPoint.setReplacement("/");
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@Override
	public void tearDown() throws Exception
	{
		entryPoint = null;
		channel = null;
		request = null;
		response = null;
	}

	public void testCommenceServletPathMatch() throws Exception
	{
		request.setContextPath("");
		request.setServletPath("/test/index.jsp");
		request.setPathInfo(null);
		entryPoint.commence(request, response);
		assertEquals("/test/", channel.getRequest().getServletPath());
	}

	public void testCommenceServletPathNoMatch() throws Exception
	{
		request.setContextPath("");
		request.setServletPath("/test/save.jsp");
		request.setPathInfo(null);
		entryPoint.commence(request, response);
		assertEquals("/test/save.jsp", channel.getRequest().getServletPath());
	}

	public void testCommencePathInfoMatch() throws Exception
	{
		request.setContextPath("");
		request.setServletPath("/test");
		request.setPathInfo("/index.jsp");
		entryPoint.commence(request, response);
		assertEquals("/", channel.getRequest().getPathInfo());
	}

	public void testCommencePathInfoNoMatch() throws Exception
	{
		request.setContextPath("");
		request.setServletPath("/test");
		request.setPathInfo("/save.jsp");
		entryPoint.commence(request, response);
		assertEquals("/save.jsp", channel.getRequest().getPathInfo());
	}
}
