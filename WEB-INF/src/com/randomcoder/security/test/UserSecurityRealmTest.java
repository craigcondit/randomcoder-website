package com.randomcoder.security.test;

import static org.junit.Assert.*;

import java.security.Principal;
import java.util.*;

import org.junit.*;
import org.springframework.mock.web.MockHttpServletRequest;

import com.randomcoder.security.UserSecurityRealm;
import com.randomcoder.user.*;
import com.randomcoder.user.test.*;

public class UserSecurityRealmTest
{
	private UserSecurityRealm realm;
	private UserDaoMock userDao;
	private RoleDaoMock roleDao;
	
	@Before public void setUp() throws Exception
	{
		roleDao = new RoleDaoMock();
		userDao = new UserDaoMock();
		
		Role role = new Role();
		role.setName("test-role");
		role.setDescription("Test Role");		
		roleDao.mockCreate(role);
		
		User user = new User();
		user.setUserName("test-user");
		user.setPassword(User.hashPassword("Password1"));
		user.setEmailAddress("test@example.com");
		user.setEnabled(true);		
		List<Role> roles = new ArrayList<Role>();
		roles.add(role);		
		user.setRoles(roles);
		userDao.create(user);
		
		realm = new UserSecurityRealm();
		realm.setUserDao(userDao);
	}

	@After public void tearDown() throws Exception
	{
		realm = null;
		userDao = null;
		roleDao = null;
	}

	@Test
	public void testValidatePassword()
	{
		Principal principal;
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		
		principal = realm.validatePassword("bad-user", "Password1", request);
		assertNull("Found non-existent user", principal);
		
		principal = realm.validatePassword("test-user", "BadPassword", request);
		assertNull("Found user with bad password", principal);
		
		principal = realm.validatePassword("test-user", "Password1", request);
		assertNotNull("Didn't find user", principal);
		
		assertEquals("Wrong username", "test-user", principal.getName());
	}

	@Test public void testIsUserInRole()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		Principal principal = realm.validatePassword("test-user", "Password1", request);
		
		assertTrue("Didn't find role", realm.isUserInRole(principal, "test-role"));
		assertFalse("Found wrong role", realm.isUserInRole(principal, "bad-role"));
	}

	@Test public void testValidatePasswordNullPassword()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		assertNull(realm.validatePassword("test-user", null, request));
	}
	
	@Test public void testValidatePasswordNullDbPassword()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		userDao.findByUserName("test-user").setPassword(null);
		assertNull(realm.validatePassword("test-user", "Password1", request));		
	}
	
	@Test public void testIsUserInRoleNonUserPrincipal()
	{
		Principal principal = new PrincipalMock("test-user"); 
		assertFalse(realm.isUserInRole(principal, "test-role"));		
	}
	
	/**
	 * This is not really a test, but exercises UserPrincipal.toString()
	 */
	@Test public void coverUserPrincipalToString()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		Principal principal = realm.validatePassword("test-user", "Password1", request);
		principal.toString();
	}
	
	static class PrincipalMock implements Principal
	{
		private final String name;
		
		public PrincipalMock(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return name;
		}		
	}
}
