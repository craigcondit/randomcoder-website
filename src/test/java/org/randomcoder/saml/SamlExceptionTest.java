package org.randomcoder.saml;

import junit.framework.TestCase;

public class SamlExceptionTest extends TestCase
{
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
