package org.randomcoder.security.cardspace;

import junit.framework.TestCase;

public class InvalidCredentialsExceptionTest extends TestCase
{
	public void testInvalidCredentialsExceptionStringThrowable()
	{
		try
		{
			throw new Exception();				
		}
		catch (Exception cause)
		{
			try
			{
				throw new InvalidCredentialsException("message", cause);
			}
			catch (InvalidCredentialsException e)
			{
				assertNotNull(e.getCause());
				assertNotNull(e.getMessage());
			}
		}
	}

	public void testInvalidCredentialsExceptionString()
	{
		try
		{
			throw new InvalidCredentialsException("message");
		}
		catch (InvalidCredentialsException e)
		{
			assertNull(e.getCause());
			assertNotNull(e.getMessage());
		}
	}
}
