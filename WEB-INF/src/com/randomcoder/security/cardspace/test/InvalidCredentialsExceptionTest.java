package com.randomcoder.security.cardspace.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.randomcoder.security.cardspace.InvalidCredentialsException;

public class InvalidCredentialsExceptionTest
{
	@Test
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

	@Test
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
