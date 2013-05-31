package org.randomcoder.user;

import junit.framework.TestCase;

@SuppressWarnings("javadoc")
public class UserTest extends TestCase
{
	private User user;
	
	@Override
	protected void setUp() throws Exception
	{
		user = new User();
		user.setPassword(User.hashPassword("Password1"));
		user.setWebsite("http://localhost/");
		user.setEmailAddress("test@example.com");
		user.setUserName("test");
	}

	@Override
	protected void tearDown() throws Exception
	{
		user = null;
	}

	public void testToString()
	{
		String value = user.toString();
		assertFalse("User has password", value.contains("password"));
		assertFalse("User has email address", value.contains("emailAddress"));
		assertFalse("User has website", value.contains("website"));
		assertFalse("User does not have username", value.contains("userName='test'"));
	}
}
