package org.randomcoder.user;

import java.beans.PropertyEditor;

import junit.framework.TestCase;

import org.randomcoder.bo.UserBusinessImpl;
import org.randomcoder.test.mock.dao.UserDaoMock;
import org.randomcoder.test.mock.user.AccountCreateControllerMock;
import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

@SuppressWarnings("javadoc")
public class AccountCreateControllerTest extends TestCase
{
	private AccountCreateControllerMock controller;
	private UserDaoMock userDao;
	private UserBusinessImpl userBusiness;
	
	@Override
	protected void setUp() throws Exception
	{
		userDao = new UserDaoMock();
		userBusiness = new UserBusinessImpl();
		userBusiness.setUserDao(userDao);
		controller = new AccountCreateControllerMock();
		controller.setUserBusiness(userBusiness);
		controller.setFormView("form");
		controller.setSuccessView("success");
	}

	@Override
	protected void tearDown() throws Exception
	{
		controller = null;
		userBusiness = null;
		userDao = null;
	}

	public void testInitBinder() throws Exception
	{
		PropertyEditor editor;
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		ServletRequestDataBinder binder = new ServletRequestDataBinder(new Object(), "test");
		controller.initBinder(request, binder);
	}

	public void testOnSubmitPassword() throws Exception
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		AccountCreateCommand command = new AccountCreateCommand();
		command.setUserName("test");
		command.setEmailAddress("test@example.com");
		command.setPassword("Password1");
		command.setPassword2("Password1");
		command.setWebsite("http://www.example.com/");
		
		ModelAndView mav = controller.onSubmit(request, response, command, new BindException(new Object(), "test"));
		assertNotNull("Null MAV", mav);
		assertEquals("Wrong view", "success", mav.getViewName());
		assertNotNull("Null user", userDao.findByUserName("test"));
	}	
}