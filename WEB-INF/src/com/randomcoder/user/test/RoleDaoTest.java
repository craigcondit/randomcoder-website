package com.randomcoder.user.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

import com.randomcoder.test.AbstractDaoTestCase;
import com.randomcoder.user.*;

public class RoleDaoTest extends AbstractDaoTestCase
{
	private RoleDao roleDao;
	
	@Before public void setUp() throws Exception
	{
		cleanDatabase();
		roleDao = (RoleDao) createDao(Role.class, RoleDao.class);
		bindSession();
	}
	
	@Test public void testFindByName() throws Exception
	{
		Role role = roleDao.findByName("ROLE_POST_ARTICLES");
		assertNotNull("Role not found", role);
		assertEquals("Wrong name", "ROLE_POST_ARTICLES", role.getName());		
	}
	
	@Test public void testListAll() throws Exception
	{
		List<Role> roles = roleDao.listAll();
		
		assertEquals("Wrong size", 6, roles.size());
		
		// sort is by description, so test first and last
		assertEquals("Wrong name at start", "ROLE_DEVELOPMENT_DWR", roles.get(0).getName());
		assertEquals("Wrong name at end", "ROLE_MANAGE_ARTICLES", roles.get(5).getName());
	}
	
	@Test public void testRead() throws Exception
	{
		Role manageUsers = roleDao.read(1L);
		assertNotNull("Null ROLE_MANAGE_USERS", manageUsers);
		assertEquals("Wrong name", "ROLE_MANAGE_USERS", manageUsers.getName());
		
		Role articlePost = roleDao.read(2L);
		assertNotNull("Null ROLE_POST_ARTICLES", articlePost);
		assertEquals("Wrong name", "ROLE_POST_ARTICLES", articlePost.getName());
	}
	
	@After public void tearDown() throws Exception
	{
		unbindSession();
		roleDao = null;
	}
	
}
