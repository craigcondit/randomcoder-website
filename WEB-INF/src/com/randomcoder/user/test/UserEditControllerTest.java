package com.randomcoder.user.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.junit.*;
import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.randomcoder.user.*;

public class UserEditControllerTest
{
	private UserEditControllerMock controller;
	private UserBusinessImpl userBusiness;
	private UserDaoMock userDao;
	private Long userId;
	private UserEditCommand command;

	@Before	public void setUp() throws Exception
	{
		userDao = new UserDaoMock();
		userBusiness = new UserBusinessImpl();
		userBusiness.setUserDao(userDao);
		controller = new UserEditControllerMock();
		controller.setUserBusiness(userBusiness);
		controller.setSuccessView("success");
		
		User user = new User();
		user.setUserName("existing-user");
		user.setPassword(User.hashPassword("Password1"));
		user.setEnabled(true);
		user.setEmailAddress("test@example.com");
		user.setRoles(new ArrayList<Role>());
		
		userId = userDao.create(user);
		
		command = new UserEditCommand();
		command.setId(userId);
	}

	@Test public void testOnBindOnNewForm() throws Exception
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		BindException errors = new BindException(command, "command");
		
		controller.onBindOnNewForm(request, command, errors);
		
		assertEquals("Wrong username", "existing-user", command.getUserName());
		assertEquals("Wrong email address", "test@example.com", command.getEmailAddress());
	}

	@Test public void testOnSubmit()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		User user = userDao.read(userId);
		command.consume(user);
		
		command.setEmailAddress("newaddress@example.com");
		command.setPassword("Password2");
		command.setPassword2("Password2");
		
		BindException errors = new BindException(command, "command");
		
		ModelAndView mav = controller.onSubmit(request, response, command, errors);
		assertNotNull("Null model and view", mav);
		assertEquals("Wrong view name", "success", mav.getViewName());
		
		User loaded = userDao.findByUserName("existing-user");
		assertNotNull("Null user", loaded);
		assertEquals("Wrong username", "existing-user", loaded.getUserName());
		assertEquals("Wrong email address", "newaddress@example.com", loaded.getEmailAddress());
		assertEquals("Wrong password", User.hashPassword("Password2"), loaded.getPassword());
	}

	@After public void tearDown() throws Exception
	{
		controller = null;
		userBusiness = null;
		userDao = null;
		userId = null;
		command = null;
	}

	protected class UserEditControllerMock extends UserEditController
	{
		@Override
		protected void onBindOnNewForm(HttpServletRequest request, Object _command, BindException errors)
		{
			super.onBindOnNewForm(request, _command, errors);
		}
		
	}
}
