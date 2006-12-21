package com.randomcoder.user;

import static org.junit.Assert.*;

import org.junit.Test;

public class RoleNotFoundExceptionTest
{
	@Test
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

	@Test
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
