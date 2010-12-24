package org.randomcoder.xml.security;

import junit.framework.TestCase;

public class XmlSecurityExceptionTest extends TestCase
{
	public void testXmlSecurityExceptionMessage()
	{
		try
		{
			throw new XmlSecurityException("test-message");
		}
		catch (XmlSecurityException e)
		{
			assertEquals("test-message", e.getMessage());
		}
	}

	public void testXmlSecurityExceptionMessageCause()
	{
		try
		{
			try
			{
				throw new Exception("cause");
			}
			catch (Exception e)
			{
				throw new XmlSecurityException("test-message", e);
			}
		}
		catch (XmlSecurityException e)
		{
			assertEquals("test-message", e.getMessage());
			assertNotNull(e.getCause());
		}
	}
}
