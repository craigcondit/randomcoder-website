package org.randomcoder.download;

import static org.junit.Assert.*;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class PackageListExceptionTest
{
	@Test
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

	@Test
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

	@Test
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

	@Test
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