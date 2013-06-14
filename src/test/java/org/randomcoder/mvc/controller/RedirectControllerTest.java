package org.randomcoder.mvc.controller;

import javax.servlet.http.HttpServletResponse;

import org.easymock.*;
import org.junit.*;

@SuppressWarnings("javadoc")
public class RedirectControllerTest
{
	private RedirectController rc;
	private IMocksControl control;
	private HttpServletResponse response;
	
	@Before
	public void setUp()
	{
		rc = new RedirectController();
		control = EasyMock.createControl();
		response = control.createMock(HttpServletResponse.class);
	}

	@After
	public void tearDown()
	{
		rc = null;
		control = null;
		response = null;
	}

	@Test
	public void testHandle() throws Exception
	{
		response.sendRedirect("http://randomcoder.org/test/");
		control.replay();
		
		rc.redirect("http://randomcoder.org/test/", response);
		control.verify();
	}

	@Test
	public void testHandleError() throws Exception
	{
		response.sendError(400);
		control.replay();
		
		rc.redirect("BOGUS", response);
		control.verify();
	}	
}