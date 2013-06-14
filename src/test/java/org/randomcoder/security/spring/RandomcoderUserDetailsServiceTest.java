package org.randomcoder.security.spring;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import org.easymock.IMocksControl;
import org.junit.*;
import org.randomcoder.bo.UserBusiness;
import org.randomcoder.db.*;
import org.randomcoder.db.User;
import org.randomcoder.test.mock.dao.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.*;

@SuppressWarnings("javadoc")
public class RandomcoderUserDetailsServiceTest
{
	private RandomcoderUserDetailsService svc = null;
	private UserDaoMock userDao = null;
	private RoleDaoMock roleDao = null;
	
	private IMocksControl control;
	private UserBusiness ub;	
	
	@Before
	public void setUp()
	{
		control = createControl();
		ub = control.createMock(UserBusiness.class);
		
		userDao = new UserDaoMock();
		roleDao = new RoleDaoMock();
		svc = new RandomcoderUserDetailsService();
		svc.setUserBusiness(ub);
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

	@After
	public void tearDown()
	{
		control = null;
		ub = null;
		svc = null;
		userDao = null;
		roleDao = null;
	}

	@Test
	public void testLoadUserByUsername()
	{
		expect (ub.findUserByName("test")).andReturn(userDao.findByUserName("test"));
		control.replay();
		
		UserDetails details = svc.loadUserByUsername("test");
		assertNotNull(details);
		assertEquals("test", details.getUsername());
		
		assertEquals(User.hashPassword("Password1"), details.getPassword());
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(details.getAuthorities());
		assertNotNull(authorities);
		assertEquals(1, authorities.size());
		assertEquals("ROLE_TEST", authorities.get(0).getAuthority());
		
		assertTrue(details.isAccountNonExpired());
		assertTrue(details.isAccountNonLocked());
		assertTrue(details.isCredentialsNonExpired());
		assertTrue(details.isEnabled());
		
		control.verify();
	}

	@Test(expected = UsernameNotFoundException.class)
	public void testLoadUserByUsernameNotFound() throws Exception
	{
		svc.loadUserByUsername("bogus");
	}

	@Test(expected = UsernameNotFoundException.class)
	public void testLoadUserByUsernameNoPassword() throws Exception
	{
		svc.loadUserByUsername("test-no-password");
	}	
}