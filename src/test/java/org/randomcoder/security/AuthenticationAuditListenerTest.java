package org.randomcoder.security;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.acegisecurity.event.authentication.AuthenticationSuccessEvent;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.randomcoder.test.mock.dao.UserDaoMock;
import org.randomcoder.user.*;
import org.springframework.context.ApplicationEvent;

@SuppressWarnings("javadoc")
public class AuthenticationAuditListenerTest extends TestCase
{
	private AuthenticationAuditListener aal;
	private UserDaoMock userDao;
	private UserBusinessImpl userBusiness;
	
	@Override
	protected void setUp() throws Exception
	{
		userDao = new UserDaoMock();
		userBusiness = new UserBusinessImpl();
		userBusiness.setUserDao(userDao);
		aal = new AuthenticationAuditListener();
		aal.setUserBusiness(userBusiness);
		
		User user = new User();
		user.setUserName("test");
		user.setEnabled(true);
		user.setEmailAddress("test@example.com");
		user.setLastLoginDate(null);
		user.setRoles(new ArrayList<Role>());
		userDao.create(user);
	}

	@Override
	protected void tearDown() throws Exception
	{
		aal = null;
		userBusiness = null;
		userDao = null;
	}

	public void testOnApplicationEventPassword()
	{
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("test", "test");		
		AuthenticationSuccessEvent event = new AuthenticationSuccessEvent(auth);
		
		User user = userDao.findByUserName("test");
		assertNull("Last login date specified", user.getLastLoginDate());
		aal.onApplicationEvent(event);
		user = userDao.findByUserName("test");
		assertNotNull("Last login date not specified", user.getLastLoginDate());		
	}
	
	public void testOnApplicationEventUnknown()
	{
		MockApplicationEvent event = new MockApplicationEvent(this);
		aal.onApplicationEvent(event);
	}

	private class MockApplicationEvent extends ApplicationEvent
	{
		private static final long serialVersionUID = 5531856610555293435L;

		public MockApplicationEvent(Object source)
		{
			super(source);
		}
	}
}
