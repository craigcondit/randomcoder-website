package com.randomcoder.user;

import junit.framework.TestCase;

public class RoleNotFoundExceptionTest extends TestCase
{
	public void testRoleNotFoundException()
	{
		try
		{
			throw new RoleNotFoundException();
		}
		catch (RoleNotFoundException e)
		{
			assertNull(e.getMessage());
		}
	}

	public void testRoleNotFoundExceptionString()
	{
		try
		{
			throw new RoleNotFoundException("test-message");
		}
		catch (RoleNotFoundException e)
		{
			assertEquals("test-message", e.getMessage());
		}
	}
}
