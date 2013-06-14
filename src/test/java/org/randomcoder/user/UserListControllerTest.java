package org.randomcoder.user;

import java.util.*;

import junit.framework.TestCase;

import org.randomcoder.bo.UserBusinessImpl;
import org.randomcoder.db.*;
import org.randomcoder.mvc.command.UserListCommand;
import org.randomcoder.test.mock.dao.UserDaoMock;
import org.randomcoder.test.mock.user.UserListControllerMock;
import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

@SuppressWarnings("javadoc")
public class UserListControllerTest extends TestCase
{
	private UserListControllerMock controller;
	private UserDaoMock userDao;
	private UserBusinessImpl ub;
	
	@Override
	public void setUp() throws Exception
	{
		userDao = new UserDaoMock();
		
		for (int i = 0; i < 100; i++)
		{
			String userName = "user-list-";
			if (i >= 10) userName += i;
			else userName += "0" + i;
			
			User user = new User();
			user.setUserName(userName);
			user.setEmailAddress(userName + "@example.com");
			user.setEnabled(true);
			user.setRoles(new ArrayList<Role>());
			user.setPassword(User.hashPassword("Password1"));
			
			userDao.create(user);
		}
		
		ub = new UserBusinessImpl();
		ub.setUserDao(userDao);
		
		controller = new UserListControllerMock();
		controller.setDefaultPageSize(25);
		controller.setMaximumPageSize(100);
		controller.setUserBusiness(ub);
		controller.setViewName("success");
	}
	
	@Override
	public void tearDown() throws Exception
	{
		ub = null;
		userDao = null;
		controller = null;
	}
	
	public void testHandle() throws Exception
	{
		ModelAndView mav;
		BindException errors;
		List users;
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		UserListCommand command = new UserListCommand();
		
		// values out of range
		command.setStart(-1);
		command.setLimit(-1);		
		errors = new BindException(command, "command");		
		mav = controller.handle(request, response, command, errors);
		assertEquals("Wrong page start", 0, ((Number) mav.getModel().get("pageStart")).intValue());
		assertEquals("Wrong page limit", 25, ((Number) mav.getModel().get("pageLimit")).intValue());
		command.setLimit(101);
		errors = new BindException(command, "command");		
		mav = controller.handle(request, response, command, errors);
		assertEquals("Wrong page limit", 100, ((Number) mav.getModel().get("pageLimit")).intValue());
		
		// first page
		command.setStart(0);
		command.setLimit(25);
		errors = new BindException(command, "command");		
		mav = controller.handle(request, response, command, errors);
		assertNotNull("Null model and view", mav);
		assertEquals("Wrong page count", 100, ((Number) mav.getModel().get("pageCount")).intValue());
		assertEquals("Wrong page start", 0, ((Number) mav.getModel().get("pageStart")).intValue());
		assertEquals("Wrong page limit", 25, ((Number) mav.getModel().get("pageLimit")).intValue());
		users = (List) mav.getModel().get("users");
		assertEquals("Wrong list length", 25, users.size());
		User user00 = (User) users.get(0);
		assertEquals("Wrong #00 username", "user-list-00", user00.getUserName());
		User user24 = (User) users.get(24);
		assertEquals("Wrong #24 username", "user-list-24", user24.getUserName());
		
		// last page
		command.setStart(75);
		command.setLimit(25);
		errors = new BindException(command, "command");		
		mav = controller.handle(request, response, command, errors);
		assertNotNull("Null model and view", mav);
		assertEquals("Wrong page count", 100, ((Number) mav.getModel().get("pageCount")).intValue());
		assertEquals("Wrong page start", 75, ((Number) mav.getModel().get("pageStart")).intValue());
		assertEquals("Wrong page limit", 25, ((Number) mav.getModel().get("pageLimit")).intValue());
		users = (List) mav.getModel().get("users");
		assertEquals("Wrong list length", 25, users.size());
		User user75 = (User) users.get(0);
		assertEquals("Wrong #75 username", "user-list-75", user75.getUserName());
		User user99 = (User) users.get(24);
		assertEquals("Wrong #99 username", "user-list-99", user99.getUserName());

		// roll off end
		command.setStart(76);
		command.setLimit(25);
		errors = new BindException(command, "command");		
		mav = controller.handle(request, response, command, errors);
		assertNotNull("Null model and view", mav);
		assertEquals("Wrong page count", 100, ((Number) mav.getModel().get("pageCount")).intValue());
		assertEquals("Wrong page start", 76, ((Number) mav.getModel().get("pageStart")).intValue());
		assertEquals("Wrong page limit", 25, ((Number) mav.getModel().get("pageLimit")).intValue());
		users = (List) mav.getModel().get("users");
		assertEquals("Wrong list length", 24, users.size());
		User user76 = (User) users.get(0);
		assertEquals("Wrong #76 username", "user-list-76", user76.getUserName());
		user99 = (User) users.get(23);
		assertEquals("Wrong #99 username", "user-list-99", user99.getUserName());
	}
}