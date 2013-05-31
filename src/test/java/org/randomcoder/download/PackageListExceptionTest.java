package org.randomcoder.download;

import junit.framework.TestCase;

@SuppressWarnings("javadoc")
public class PackageListExceptionTest extends TestCase
{

	public void testPackageListException()
	{
		try 
		{
			throw new PackageListException();
		}
		catch (PackageListException e)
		{
			assertNull("Message present", e.getMessage());
			assertNull("Cause present", e.getCause());
		}
	}

	public void testPackageListExceptionString()
	{
		try 
		{
			throw new PackageListException("test");
		}
		catch (PackageListException e)
		{
			assertEquals("Message wrong", "test", e.getMessage());
			assertNull("Cause present", e.getCause());
		}
	}

	public void testPackageListExceptionThrowable()
	{
		try 
		{
			throw new PackageListException(new Exception());
		}
		catch (PackageListException e)
		{
			assertNotNull("Cause not present", e.getCause());
		}
	}

	public void testPackageListExceptionStringThrowable()
	{
		try 
		{
			throw new PackageListException("test", new Exception());
		}
		catch (PackageListException e)
		{
			assertEquals("Message wrong", "test", e.getMessage());
			assertNotNull("Cause not present", e.getCause());
		}
	}

}
