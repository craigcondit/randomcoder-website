package org.randomcoder.security;

import junit.framework.TestCase;

@SuppressWarnings("javadoc")
public class UnauthorizedExceptionTest extends TestCase
{
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
