package com.randomcoder.user;

import junit.framework.TestCase;

public class UserNotFoundExceptionTest extends TestCase
{
	public void testUserNotFoundException()
	{
		try
		{
			throw new UserNotFoundException();
		}
		catch (UserNotFoundException e)
		{
			assertNull(e.getMessage());
		}
	}

	public void testUserNotFoundExceptionString()
	{
		try
		{
			throw new UserNotFoundException("test-message");
		}
		catch (UserNotFoundException e)
		{
			assertEquals("test-message", e.getMessage());
		}
	}
}