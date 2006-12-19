package com.randomcoder.xml.security.test;


import static org.junit.Assert.*;

import org.junit.Test;

import com.randomcoder.xml.security.XmlSecurityConfigurationException;

public class XmlSecurityConfigurationExceptionTest
{

	@Test
	public void testXmlSecurityConfigurationExceptionMessage()
	{
		try
		{
			throw new XmlSecurityConfigurationException("test-message");
		}
		catch (XmlSecurityConfigurationException e)
		{
			assertEquals("test-message", e.getMessage());
		}
	}

	@Test
	public void testXmlSecurityConfigurationExceptionMessageCause()
	{
		try
		{
			try
			{
				throw new Exception("cause");
			}
			catch (Exception e)
			{
				throw new XmlSecurityConfigurationException("test-message", e);
			}
		}
		catch (XmlSecurityConfigurationException e)
		{
			assertEquals("test-message", e.getMessage());
			assertNotNull(e.getCause());
		}
	}

	
}
