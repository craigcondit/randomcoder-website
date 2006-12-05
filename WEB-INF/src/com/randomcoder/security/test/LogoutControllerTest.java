package com.randomcoder.security.test;

import static org.junit.Assert.*;

import javax.servlet.http.*;

import org.junit.*;
import org.springframework.mock.web.*;
import org.springframework.web.servlet.ModelAndView;

import com.randomcoder.security.LogoutController;

public class LogoutControllerTest
{
	private LogoutControllerMock controller;
	
	@Before public void setUp() throws Exception
	{
		controller = new LogoutControllerMock();
		controller.setViewName("success");
	}

	@After public void tearDown() throws Exception
	{
		controller = null;
	}

	@Test public void testHandleRequestInternal()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		HttpSession session = request.getSession(true);
		assertNotNull("Null session", session);
		
		session = request.getSession(false);
		assertNotNull("Null session", session);
		
		ModelAndView mav = controller.handleRequestInternal(request, response);
		
		session = request.getSession(false);
		assertNull("Non-null session", session);
		
		assertNotNull("Null MAV", mav);
		assertEquals("Wrong view name", "success", mav.getViewName());		
	}

	protected class LogoutControllerMock extends LogoutController
	{
		
		@Override
		protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
		{
			return super.handleRequestInternal(request, response);
		}
		
	}
}
