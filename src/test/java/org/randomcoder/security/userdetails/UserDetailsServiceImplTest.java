package org.randomcoder.security.userdetails;

import static org.easymock.EasyMock.*;

import java.util.*;

import junit.framework.TestCase;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.userdetails.*;
import org.easymock.IMocksControl;
import org.randomcoder.bo.UserBusiness;
import org.randomcoder.test.mock.dao.*;
import org.randomcoder.user.*;
import org.randomcoder.user.User;

@SuppressWarnings("javadoc")
public class UserDetailsServiceImplTest extends TestCase
{
	private UserDetailsServiceImpl svc = null;
	private UserDaoMock userDao = null;
	private RoleDaoMock roleDao = null;
	
	private IMocksControl control;
	private UserBusiness ub;	
	
	@Override
	public void setUp() throws Exception
	{
		control = createControl();
		ub = control.createMock(UserBusiness.class);
		
		userDao = new UserDaoMock();
		roleDao = new RoleDaoMock();
		svc = new UserDetailsServiceImpl();
		svc.setUserBusiness(ub);
		svc.setDebug(false);
		{
			Role role = new Role();
			role.setName("ROLE_TEST");
			role.setDescription("Test role");	
			roleDao.mockCreate(role);
			
			List<Role> roles = new ArrayList<Role>();
			roles.add(role);
			
			User user = new User();
			user.setUserName("test");
			user.setEnabled(true);
			user.setPassword(User.hashPassword("Password1"));
			user.setEmailAddress("test@example.com");
			user.setRoles(roles);
			userDao.create(user);
		}
		
		{
			User user = new User();
			user.setUserName("test-no-password");
			user.setEnabled(true);
			user.setEmailAddress("test-no-password@example.com");
			user.setRoles(new ArrayList<Role> ());
			userDao.create(user);
		}
	}

	@Override
	public void tearDown() throws Exception
	{
		control = null;
		ub = null;
		svc = null;
		userDao = null;
		roleDao = null;
	}

	public void testLoadUserByUsername()
	{
		expect (ub.findUserByName("test")).andReturn(userDao.findByUserName("test"));
		control.replay();
		
		UserDetails details = svc.loadUserByUsername("test");
		assertNotNull(details);
		assertEquals("test", details.getUsername());
		
		assertEquals(User.hashPassword("Password1"), details.getPassword());
		
		GrantedAuthority[] authorities =  details.getAuthorities();
		assertNotNull(authorities);
		assertEquals(1, authorities.length);
		assertEquals("ROLE_TEST", authorities[0].getAuthority());
		
		assertTrue(details.isAccountNonExpired());
		assertTrue(details.isAccountNonLocked());
		assertTrue(details.isCredentialsNonExpired());
		assertTrue(details.isEnabled());
		
		control.verify();
	}

	public void testLoadUserByUsernameDebug()
	{
		svc.setDebug(true);
		testLoadUserByUsername();
	}
	
	public void testLoadUserByUsernameNotFound() throws Exception
	{
		try
		{
			svc.loadUserByUsername("bogus");
			fail("UsernameNotFoundException expected");
		}
		catch (UsernameNotFoundException e)
		{
			// pass
		}
	}

	public void testLoadUserByUsernameNoPassword() throws Exception
	{
		try
		{
			svc.loadUserByUsername("test-no-password");
			fail("UsernameNotFoundException expected");
		}
		catch (UsernameNotFoundException e)
		{
			// pass
		}
	}	
}