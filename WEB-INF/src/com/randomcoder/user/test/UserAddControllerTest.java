package com.randomcoder.user.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletRequest;

import org.junit.*;
import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.randomcoder.user.*;

public class UserAddControllerTest
{
	private UserAddControllerMock controller;
	private UserBusinessImpl userBusiness;
	private UserDaoMock userDao;

	@Before	public void setUp() throws Exception
	{
		userDao = new UserDaoMock();
		userBusiness = new UserBusinessImpl();
		userBusiness.setUserDao(userDao);
		controller = new UserAddControllerMock();
		controller.setUserBusiness(userBusiness);
		controller.setSuccessView("success");
	}

	@Test public void coverUserAddCommandToString()
	{
		new UserAddCommand().toString();
	}
	
	@Test public void testOnBindOnNewForm() throws Exception
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		
		UserAddCommand command = new UserAddCommand();
		
		controller.onBindOnNewForm(request, command);
		
		assertTrue("User not enabled", command.isEnabled());
	}

	@Test public void testOnSubmit()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		UserAddCommand command = new UserAddCommand();
		
		command.setUserName("on-submit");
		command.setEnabled(true);
		command.setPassword("Password1");
		command.setPassword2("Password1");
		command.setEmailAddress("onsubmit@example.com");
		command.setRoles(new Role[] {});
		
		BindException errors = new BindException(command, "command");
		
		ModelAndView mav = controller.onSubmit(request, response, command, errors);
		assertNotNull("Null model and view", mav);
		assertEquals("Wrong view name", "success", mav.getViewName());
		
		User user = userDao.findByUserName("on-submit");
		assertNotNull("Null user", user);
		assertEquals("Wrong username", "on-submit", user.getUserName());
	}

	@After public void tearDown() throws Exception
	{
		controller = null;
		userBusiness = null;
		userDao = null;
	}

	protected class UserAddControllerMock extends UserAddController
	{
		@Override
		protected void onBindOnNewForm(HttpServletRequest request, Object command) throws Exception
		{
			super.onBindOnNewForm(request, command);
		}
		
	}
}
