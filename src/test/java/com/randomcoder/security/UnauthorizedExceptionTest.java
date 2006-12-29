package com.randomcoder.security;

import static org.junit.Assert.*;

import org.junit.Test;

public class UnauthorizedExceptionTest
{
	@Test	public void testUnauthorizedException()
	{
		try
		{
			throw new UnauthorizedException();
		}
		catch (UnauthorizedException e)
		{
			assertNull(e.getMessage());
		}
	}

	@Test
	public void testUnauthorizedExceptionString()
	{
		try
		{
			throw new UnauthorizedException("test-message");
		}
		catch (UnauthorizedException e)
		{
			assertEquals("test-message", e.getMessage());
		}
	}
}
