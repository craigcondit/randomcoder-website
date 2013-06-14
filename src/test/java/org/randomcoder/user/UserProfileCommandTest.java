package org.randomcoder.user;

import junit.framework.TestCase;

import org.randomcoder.db.User;
import org.randomcoder.mvc.command.UserProfileCommand;

@SuppressWarnings("javadoc")
public class UserProfileCommandTest extends TestCase
{
	private UserProfileCommand command;
	
	@Override
	protected void setUp() throws Exception
	{
		command = new UserProfileCommand();
	}

	@Override
	protected void tearDown() throws Exception
	{
		command = null;
	}

	public void testGetEmailAddress()
	{
		command.setEmailAddress("test@example.com");
		assertEquals("test@example.com", command.getEmailAddress());
	}

	public void testGetWebsite()
	{
		command.setWebsite("http://test.com/");
		assertEquals("http://test.com/", command.getWebsite());
	}

	public void testProduce()
	{
		command.setEmailAddress("test@example.com");
		command.setWebsite("http://test.com/");
		
		User user = new User();
		command.produce(user);
		
		assertEquals("Wrong website", "http://test.com/", user.getWebsite());
		assertEquals("Wrong email address", "test@example.com", user.getEmailAddress());
	}
}