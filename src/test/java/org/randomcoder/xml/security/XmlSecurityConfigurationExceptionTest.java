package org.randomcoder.xml.security;

import junit.framework.TestCase;

public class XmlSecurityConfigurationExceptionTest extends TestCase
{
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
