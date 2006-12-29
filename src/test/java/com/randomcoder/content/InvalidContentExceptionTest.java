package com.randomcoder.content;

import junit.framework.TestCase;

public class InvalidContentExceptionTest extends TestCase
{	
	public void testInvalidContentException()
	{
		try
		{
			throw new InvalidContentException("Error", 10, 45);
		}
		catch (InvalidContentException e)
		{
			assertEquals("Error", e.getMessage());
			assertEquals(10, e.getLineNumber());
			assertEquals(45, e.getColumnNumber());
			assertEquals("Line 10, column 45: Error", e.toString());
		}
	}
	
	public void testInvalidContentExceptionNullMessage()
	{
		try
		{
			throw new InvalidContentException(null, 10, 45);
		}
		catch (InvalidContentException e)
		{
			assertNull(e.getMessage());
		}
	}
}