package org.randomcoder.security;

import static org.junit.Assert.*;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class UnauthorizedExceptionTest
{
	@Test
	public void testUnauthorizedException()
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
