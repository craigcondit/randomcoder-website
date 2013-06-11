package org.randomcoder.user;

import java.security.Principal;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.springframework.mock.web.*;
import org.springframework.validation.BindException;

import org.randomcoder.bo.UserBusinessImpl;
import org.randomcoder.test.mock.dao.*;
import org.randomcoder.test.mock.jse.PrincipalMock;
import org.randomcoder.test.mock.user.ChangePasswordControllerMock;

@SuppressWarnings("javadoc")
public class ChangePasswordControllerTest extends TestCase
{
	private ChangePasswordControllerMock controller;
	private UserBusinessImpl userBusiness;
	private UserDaoMock userDao;
	private RoleDaoMock roleDao;
	
	@Override
	public void setUp() throws Exception
	{
		userDao = new UserDaoMock();
		roleDao = new RoleDaoMock();
		userBusiness = new UserBusinessImpl();
		userBusiness.setUserDao(userDao);
		controller = new ChangePasswordControllerMock();
		controller.setUserBusiness(userBusiness);
	}

	public void testOnBind() throws Exception
	{
		Role role = createTestRole("on-bind-role", "On Bind");
		User user = createTestUser("on-bind-user", "Password1", "on-bind-user@example.com", true, role);
		
		Principal principal = new PrincipalMock(user.getUserName());
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		
		request.setUserPrincipal(principal);
		request.setRemoteUser(user.getUserName());
		request.addUserRole(role.getName());		

		ChangePasswordCommand command = new ChangePasswordCommand();
		
		controller.onBind(request, command);
		
		assertNotNull("User not populated", command.getUser());
		assertEquals("Wrong user", "on-bind-user", command.getUser().getUserName());
	}

	public void testOnSubmit() throws Exception
	{
		Role role = createTestRole("on-submit-role", "On Submit");
		User user = createTestUser("on-submit-user", "Password1", "on-submit-user@example.com", true, role);
		
		Principal principal = new PrincipalMock(user.getUserName());
		
		MockHttpServletRequest request = new MockHttpServletRequest();		
		request.setUserPrincipal(principal);
		request.setRemoteUser(user.getUserName());
		request.addUserRole(role.getName());		

		MockHttpServletResponse response = new MockHttpServletResponse();
		
		ChangePasswordCommand command = new ChangePasswordCommand();
		command.setOldPassword("Password1");
		command.setPassword("Password2");
		command.setPassword2("Password2");
		
		BindException errors = new BindException(command, "command");
		
		controller.onSubmit(request, response, command, errors);
		
		User changed = userDao.findByUserName("on-submit-user");
		assertNotNull("Null user", changed);
		assertEquals("Wrong password", User.hashPassword("Password2"), changed.getPassword());
	}

	@Override
	public void tearDown() throws Exception
	{
		controller = null;
		userBusiness = null;
		userDao = null;
	}

	private Role createTestRole(String name, String description)
	{
		Role role = new Role();
		role.setName(name);
		role.setDescription(description);
		Long id = roleDao.mockCreate(role);
		return roleDao.read(id);		
	}

	private User createTestUser(String userName, String password, String emailAddress, boolean enabled, Role... roles)
	{
		User user = new User();
		user.setUserName(userName);
		user.setEnabled(enabled);
		user.setEmailAddress(emailAddress);
		user.setPassword(User.hashPassword(password));
		user.setRoles(new ArrayList<Role>());
		for (Role role : roles) user.getRoles().add(role);
		Long id = userDao.create(user);
		return userDao.read(id);
	}
}
