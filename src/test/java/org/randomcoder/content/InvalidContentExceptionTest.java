package org.randomcoder.content;

import static org.junit.Assert.*;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class InvalidContentExceptionTest
{
	@Test
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

	@Test
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
