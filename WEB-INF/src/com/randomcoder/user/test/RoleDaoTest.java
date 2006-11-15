package com.randomcoder.user.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

import com.randomcoder.test.AbstractDaoTest;
import com.randomcoder.user.*;

public class RoleDaoTest extends AbstractDaoTest
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
		Role role = roleDao.findByName("article-post");
		assertNotNull("Role not found", role);
		assertEquals("Wrong name", "article-post", role.getName());		
	}
	
	@Test public void testListAll() throws Exception
	{
		List<Role> roles = roleDao.listAll();
		
		assertEquals("Wrong size", 6, roles.size());
		
		// sort is by description, so test first and last
		assertEquals("Wrong name at start", "development-dwr", roles.get(0).getName());
		assertEquals("Wrong name at end", "article-admin", roles.get(5).getName());
	}
	
	@Test public void testRead() throws Exception
	{
		Role manageUsers = roleDao.read(1L);
		assertNotNull("Null manage-users", manageUsers);
		assertEquals("Wront name", "manage-users", manageUsers.getName());
		
		Role articlePost = roleDao.read(2L);
		assertNotNull("Null article-post", articlePost);
		assertEquals("Wront name", "article-post", articlePost.getName());
	}
	
	@After public void tearDown() throws Exception
	{
		unbindSession();
		roleDao = null;
	}
	
}
