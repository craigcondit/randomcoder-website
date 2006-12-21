package com.randomcoder.xml.security;


import static org.junit.Assert.*;

import org.junit.Test;

public class XmlSecurityExceptionTest
{

	@Test
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

	@Test
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
