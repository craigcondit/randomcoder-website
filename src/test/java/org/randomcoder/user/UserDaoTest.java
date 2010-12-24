package org.randomcoder.user;

import java.util.*;

import org.randomcoder.test.AbstractDaoTestCase;

public class UserDaoTest extends AbstractDaoTestCase
{
	private UserDao userDao;
	private RoleDao roleDao;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		cleanDatabase();
		userDao = (UserDao) createDao(User.class, UserDao.class);
		roleDao = (RoleDao) createDao(Role.class, RoleDao.class);
		bindSession();
	}	
	
	public void testCountAll() throws Exception
	{		
		// database is initially empty, so record count should be 0
		begin();
		
		assertEquals("User record count wrong", 0, userDao.countAll());
		
		commit();
		
		//----------------------------
		rebindSession();
		
		// add a user		
		begin();
		
		User user = new User();
		user.setUserName("test");
		user.setPassword(User.hashPassword("test"));
		user.setEmailAddress("test@example.com");
		user.setEnabled(true);
		
		userDao.create(user);
		
		commit();
		
		//----------------------------
		rebindSession();
		
		// database should now have 1 record
		begin();
		
		assertEquals("User record count wrong", 1, userDao.countAll());
		
		commit();
	}

	public void testCreate() throws Exception
	{
		begin();
		User user = createTestUser("test-create", "test-create", "test-create@example.com", true);
		commit();
		
		assertNotNull("ID null after commit", user.getId());
		
		//----------------------------
		rebindSession();
		
		begin();
		
		User loadedUser = userDao.findByUserName("test-create");
		
		assertNotNull("User not found", loadedUser);		
		assertEquals("Wrong username", "test-create", loadedUser.getUserName());
		assertEquals("Wrong password", User.hashPassword("test-create"), loadedUser.getPassword());
		assertEquals("Not enabled", true, loadedUser.isEnabled());
		assertNotNull("Roles null", loadedUser.getRoles());
		assertEquals("Wrong role count", 1, loadedUser.getRoles().size());
		
		Role role = loadedUser.getRoles().get(0);
		assertEquals("Wrong role name", "ROLE_POST_ARTICLES", role.getName());
		
		commit();
	}
	
	public void testDelete() throws Exception
	{
		// create a test user
		begin();
		createTestUser("test-delete", "test-delete", "test-delete@example.com", true);
		commit();
		
		rebindSession();
		
		// load the user		
		begin();
		
		User loadedUser = userDao.findByUserName("test-delete");
		assertNotNull("Unable to find created user", loadedUser);
		
		// delete the user
		userDao.delete(loadedUser);
		
		commit();
		
		rebindSession();
		
		// try to load again
		
		begin();
		
		User deletedUser = userDao.findByUserName("test-delete");
		assertNull("Deleted user still exists", deletedUser);
		
		commit();		
	}

	public void testFindByUserName() throws Exception
	{
		// create a user
		begin();
		createTestUser("test-findByUserName", "test-findByUserName", "findbyusername@example.com", true);
		commit();
		
		rebindSession();
		
		// load user
		begin();
		User user = userDao.findByUserName("test-findByUserName");
		assertNotNull("Unable to find user", user);
		assertEquals("Wrong username", "test-findByUserName", user.getUserName());
		
		commit();
	}

	public void testFindByUserNameEnabled() throws Exception
	{
		// create some users
		begin();
		createTestUser("test-findByUserNameEnabled", "test-findByUserNameEnabled", "findbyusernameenabled@example.com", true);
		createTestUser("test-findByUserNameDisabled", "test-findByUserNameDisabled", "findbyusernamedisabled@example.com", false);
		commit();
		
		rebindSession();
		
		// load users
		begin();
		
		User enabled = userDao.findByUserNameEnabled("test-findByUserNameEnabled");
		assertNotNull("Unable to find enabled user", enabled);
		assertEquals("Wrong username", "test-findByUserNameEnabled", enabled.getUserName());

		User disabled = userDao.findByUserNameEnabled("test-findByUserNameDisabled");
		assertNull("Found disabled user", disabled);
		
		commit();
	}
	
	public void testListAll() throws Exception
	{		
		// create test users
		begin();
		for (int i = 0; i < 10; i++)
		{
			createTestUser("test-listAll" + i, "test-listAll", "testlistall@example.com", true);
		}		
		commit();
		
		rebindSession();
		
		// list users
		begin();
		List<User> users = userDao.listAll();
		assertNotNull("Null user list", users);
		assertEquals("Wrong length", 10, users.size());
		for (int i = 0; i < 10; i++)
		{
			assertEquals("Wrong username", "test-listAll" + i, users.get(i).getUserName());
		}
		commit();
	}

	public void testListAllInRange() throws Exception
	{		
		// create test users
		begin();
		for (int i = 0; i < 10; i++)
		{
			createTestUser("test-listAllInRange" + i, "test-listAllInRange", "testlistallinrange@example.com", true);
		}		
		commit();
		
		rebindSession();
		
		// list users
		begin();
		
		for (int i = 0; i < 10; i+=2)			
		{
			List<User> users = userDao.listAllInRange(i, 2);
			assertNotNull("Null user list", users);
			assertEquals("Wrong length", 2, users.size());
			assertEquals("Wrong username", "test-listAllInRange" + i, users.get(0).getUserName());
			assertEquals("Wrong username", "test-listAllInRange" + (i+1), users.get(1).getUserName());
		}
		
		// test off end
		List<User> users = userDao.listAllInRange(10, 2);
		assertNotNull("Null user list", users);
		assertEquals("Wrong length", 0, users.size());
		
		commit();
	}

	public void testListEnabled() throws Exception
	{
		// create test users
		begin();
		for (int i = 0; i < 10; i++)
		{
			createTestUser("test-listEnabled" + i, "test-listEnabled", "testlistenabled@example.com", (i % 2 == 0));
		}		
		commit();
		
		rebindSession();
		
		// list users
		begin();
		
		List<User> users = userDao.listEnabled();
		assertNotNull("Null user list", users);
		assertEquals("Wrong length", 5, users.size());
		for (int i = 0; i < 5; i++)			
		{
			assertEquals("Wrong username", "test-listEnabled" + (i * 2), users.get(i).getUserName());
		}
		commit();
	}
	
	public void testRead() throws Exception
	{
		begin();
		User created = createTestUser("test-read", "test-read", "testread@example.com", true);
		commit();
		
		Long id = created.getId();
		
		rebindSession();
		
		begin();
		User read = userDao.read(id);
		assertNotNull("Null user", read);		
		assertEquals("Wrong username", "test-read", read.getUserName());
		commit();
	}
	
	public void testUpdate() throws Exception
	{
		begin();
		createTestUser("test-update", "test-update", "testupdate@example.com", true);
		commit();
		
		rebindSession();

		begin();
		User read = userDao.findByUserName("test-update");
		assertNotNull("Null user", read);		
		read.setEmailAddress("testupdate2@example.com");
		userDao.update(read);
		commit();
		
		rebindSession();
		
		begin();
		User updated = userDao.findByUserName("test-update");
		assertNotNull("Null user", updated);
		assertEquals("Wrong email address", "testupdate2@example.com", updated.getEmailAddress());
		commit();
	}
		
	private User createTestUser(String userName, String password, String email, boolean enabled)
	{
		User user = new User();
		user.setUserName(userName);
		user.setPassword(User.hashPassword(password));
		user.setEnabled(enabled);
		user.setEmailAddress(email);
		
		List<Role> roles = new ArrayList<Role>();
		roles.add(roleDao.findByName("ROLE_POST_ARTICLES"));
		user.setRoles(roles);
		
		userDao.create(user);
		
		return user;
	}
	
	@Override
	public void tearDown() throws Exception
	{
		unbindSession();
		userDao = null;
		roleDao = null;
		super.tearDown();
	}
	
}
