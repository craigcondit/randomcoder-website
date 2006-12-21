package com.randomcoder.saml;

import static org.junit.Assert.*;

import org.junit.Test;

public class SamlExceptionTest
{
	@Test
	public void testSamlException()
	{
		try
		{
			throw new SamlException();
		}
		catch (SamlException e)
		{
			assertNull(e.getCause());
			assertNull(e.getMessage());
		}
	}

	@Test
	public void testSamlExceptionStringThrowable()
	{
		try
		{
			throw new Exception();				
		}
		catch (Exception cause)
		{
			try
			{
				throw new SamlException("message", cause);
			}
			catch (SamlException e)
			{
				assertNotNull(e.getCause());
				assertEquals("message", e.getMessage());
			}
		}
	}

	@Test
	public void testSamlExceptionString()
	{
		try
		{
			throw new SamlException("message");
		}
		catch (SamlException e)
		{
			assertNull(e.getCause());
			assertEquals("message", e.getMessage());
		}
	}

	@Test
	public void testSamlExceptionThrowable()
	{
		try
		{
			throw new Exception();				
		}
		catch (Exception cause)
		{
			try
			{
				throw new SamlException(cause);
			}
			catch (SamlException e)
			{
				assertNotNull(e.getCause());
			}
		}
	}

}
