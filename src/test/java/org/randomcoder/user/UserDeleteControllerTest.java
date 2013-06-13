package org.randomcoder.user;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.randomcoder.bo.UserBusinessImpl;
import org.randomcoder.db.*;
import org.randomcoder.springmvc.IdCommand;
import org.randomcoder.test.mock.dao.UserDaoMock;
import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

@SuppressWarnings("javadoc")
public class UserDeleteControllerTest extends TestCase
{
	private UserDeleteController controller;
	private UserBusinessImpl userBusiness;
	private UserDaoMock userDao;

	@Override
	public void setUp() throws Exception
	{
		userDao = new UserDaoMock();
		userBusiness = new UserBusinessImpl();
		userBusiness.setUserDao(userDao);
		controller = new UserDeleteController();
		controller.setUserBusiness(userBusiness);
		controller.setViewName("success");
	}

	public void testHandle() throws Exception
	{
		User user = new User();
		user.setUserName("test-delete");
		user.setEmailAddress("delete@example.com");
		user.setPassword(User.hashPassword("Password1"));
		user.setRoles(new ArrayList<Role>());
		user.setEnabled(true);
		
		Long id = userDao.create(user);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		IdCommand command = new IdCommand();
		command.setId(id);
		
		BindException errors = new BindException(command, "command");
		
		ModelAndView mav = controller.handle(request, response, command, errors);
		assertNotNull("Null model and view", mav);
		assertEquals("Wrong view name", "success", mav.getViewName());
		
		User loaded = userDao.read(id);
		assertNull("User was not deleted", loaded);
	}
	
	@Override
	public void tearDown() throws Exception
	{
		controller = null;
		userBusiness = null;
		userDao = null;
	}
}