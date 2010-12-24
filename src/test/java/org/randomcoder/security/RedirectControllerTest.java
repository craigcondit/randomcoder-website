package org.randomcoder.security;

import javax.servlet.http.*;

import junit.framework.TestCase;

import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class RedirectControllerTest extends TestCase
{
	private MockRedirectController controller;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private RedirectCommand command;
	private BindException errors;
	
	@Override
	protected void setUp() throws Exception
	{
		controller = new MockRedirectController();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		command = new RedirectCommand();
		errors = new BindException(new Object(), "test");
	}

	@Override
	protected void tearDown() throws Exception
	{
		controller = null;
		request = null;
		response = null;
		command = null;
		errors = null;
	}

	public void testHandle() throws Exception
	{
		command.setUrl("http://randomcoder.org/test/");		
		ModelAndView mav = controller.handle(request, response, command, errors);
		assertNull("MAV specified", mav);
		assertEquals("Wrong redirect", "http://randomcoder.org/test/", response.getRedirectedUrl());
	}

	public void testHandleError() throws Exception
	{
		command.setUrl("BOGUS");		
		ModelAndView mav = controller.handle(request, response, command, errors);
		assertNull("MAV specified", mav);
		assertEquals("Wrong status", HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
	}
	
	class MockRedirectController extends RedirectController
	{
		@Override
		public ModelAndView handle(HttpServletRequest _request, HttpServletResponse _response, Object _command, BindException _errors) throws Exception
		{
			return super.handle(_request, _response, _command, _errors);
		}
	}
}
