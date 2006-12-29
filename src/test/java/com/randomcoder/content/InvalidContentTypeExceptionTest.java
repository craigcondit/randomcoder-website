package com.randomcoder.content;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InvalidContentTypeExceptionTest
{
	@Test
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
