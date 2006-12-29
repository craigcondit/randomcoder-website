package com.randomcoder.user;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.randomcoder.test.mock.dao.UserDaoMock;

public class UserBusinessImplTest extends TestCase
{
	private UserBusinessImpl userBusiness;
	private UserDaoMock userDao;
	
	@Override
	public void setUp()
	{
		userBusiness = new UserBusinessImpl();
		userDao = new UserDaoMock();
		userBusiness.setUserDao(userDao);
	}

	public void testChangePassword()
	{
		User user = new User();
		user.setUserName("test-change-password");
		user.setEnabled(true);
		user.setEmailAddress("test@example.com");
		user.setPassword(User.hashPassword("test-password"));
		
		userDao.create(user);
		
		userBusiness.changePassword("test-change-password", "test-new-password");
		
		User changed = userDao.findByUserName("test-change-password");
		assertNotNull("Null user", changed);
		assertEquals("Wrong password", User.hashPassword("test-new-password"), changed.getPassword());
	}

	public void testChangePasswordUserNotFound()
	{
		try
		{
			userBusiness.changePassword("bogus-user", "bogus-password");
			fail("UserNotFoundException expected");
		}
		catch (UserNotFoundException e)
		{
			// pass
		}
	}
	
	public void testCreateUser()
	{
		UserAddCommand cmd = new UserAddCommand();
		
		cmd.setUserName("test-create");
		cmd.setEmailAddress("test-create@example.com");
		cmd.setPassword("testCreate1");
		cmd.setPassword2("testCreate1");
		cmd.setEnabled(true);
		
		Role testRole = new Role();
		testRole.setId(1L);
		testRole.setName("test-role");
		testRole.setDescription("Test role");
		
		cmd.setRoles(new Role[] { testRole });
		
		userBusiness.createUser(cmd);
		
		User added = userDao.findByUserName("test-create");
		
		assertNotNull("Null user", added);
		assertEquals("Wrong username", "test-create", added.getUserName());
		assertEquals("Wrong email address", "test-create@example.com", added.getEmailAddress());
		assertEquals("Wrong password", User.hashPassword("testCreate1"), added.getPassword());
		assertEquals("Not enabled", true, added.isEnabled());
		assertNotNull("Null role list", added.getRoles());
		assertEquals("Wrong role count", 1, added.getRoles().size());
		assertEquals("Wrong role name", "test-role", added.getRoles().get(0).getName());
	}

	public void testUpdateUser()
	{
		User user = new User();
		user.setUserName("test-update-user");
		user.setEnabled(true);
		user.setEmailAddress("test-update@example.com");
		user.setPassword(User.hashPassword("testPassword1"));		
		user.setRoles(new ArrayList<Role> ());
		Long id = userDao.create(user);
		
		UserEditCommand cmd = new UserEditCommand();
		cmd.consume(user);
		cmd.setEmailAddress("test-update2@example.com");
		cmd.setPassword("testPassword2");
		cmd.setPassword2("testPassword2");
		userBusiness.updateUser(cmd, id);
		
		User updated = userDao.read(id);
		
		assertNotNull("Null user", updated);
		assertEquals("Wrong username", "test-update-user", updated.getUserName());
		assertEquals("Wrong email address", "test-update2@example.com", updated.getEmailAddress());
		assertEquals("Wrong password", User.hashPassword("testPassword2"), updated.getPassword());
		assertNotNull("Null role list", updated.getRoles());
		assertTrue("Not enabled", updated.isEnabled());				
		assertEquals("Wrong role count", 0, updated.getRoles().size());
	}

	public void testDeleteUser()
	{
		User user = new User();
		user.setUserName("test-delete-user");
		user.setEnabled(true);
		user.setEmailAddress("test-delete@example.com");
		user.setPassword(User.hashPassword("testPassword1"));		
		user.setRoles(new ArrayList<Role> ());
		Long id = userDao.create(user);
		
		User read = userDao.read(id);
		assertNotNull("Found pending deleted user", read);
				
		userBusiness.deleteUser(id);
		
		read = userDao.read(id);
		assertNull("Found deleted user", read);
	}

	public void testLoadUserForEditing()
	{
		User user = new User();
		user.setUserName("test-load-user");
		user.setEnabled(true);
		user.setEmailAddress("test-load@example.com");
		user.setPassword(User.hashPassword("testPassword1"));		
		user.setRoles(new ArrayList<Role> ());
		Long id = userDao.create(user);
		
		UserEditCommand cmd = new UserEditCommand();
		
		userBusiness.loadUserForEditing(cmd, id);
		
		assertEquals("Wrong username", "test-load-user", cmd.getUserName());
		assertEquals("Wrong email address", "test-load@example.com", cmd.getEmailAddress());
		assertTrue("Not enabled", cmd.isEnabled());
		assertNotNull("Null role list", cmd.getRoles());
		assertTrue("Not enabled", cmd.isEnabled());				
		assertEquals("Wrong role count", 0, cmd.getRoles().length);
	}

	public void testLoadUserForEditingUserNotFound()
	{
		try
		{
			UserEditCommand cmd = new UserEditCommand();		
			userBusiness.loadUserForEditing(cmd, (long) -1);		
			fail("UserNotFoundException expected");
		}
		catch (UserNotFoundException e)
		{
			// pass
		}
	}
	
	@Override
	public void tearDown()
	{
		userDao = null;
		userBusiness = null;
	}
}
