package com.randomcoder.security.cardspace;

import java.lang.reflect.Constructor;

import org.junit.Test;

public class CardSpaceAttributesTest
{

	/**
	 * Not a test, but tickles the private constructor.
	 */
	@Test
	public void coverDefaultConstructor() throws Exception
	{
		Constructor c = CardSpaceAttributes.class.getDeclaredConstructor(new Class[] {});
		c.setAccessible(true);
		c.newInstance(new Object[] {});
	}	
}