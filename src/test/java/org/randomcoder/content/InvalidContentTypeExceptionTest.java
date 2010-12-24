package com.randomcoder.content;

import junit.framework.TestCase;

public class InvalidContentTypeExceptionTest extends TestCase
{
	public void testInvalidContentTypeException()
	{
		try
		{
			throw new InvalidContentTypeException("error");
		}
		catch (InvalidContentTypeException e)
		{
			assertEquals("error", e.getMessage());
		}
	}
}