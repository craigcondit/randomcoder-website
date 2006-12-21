package com.randomcoder.security.cardspace.test;


import static org.junit.Assert.*;

import org.junit.Test;

import com.randomcoder.security.cardspace.Gender;

public class GenderTest
{
	@Test
	public void testValues()
	{
		Gender[] genders = Gender.values();
		assertNotNull(genders);
		assertEquals(3, genders.length);		
	}

	@Test
	public void testValueOf()
	{
		assertEquals(Gender.MALE, Gender.valueOf("MALE"));
		assertEquals(Gender.FEMALE, Gender.valueOf("FEMALE"));
		assertEquals(Gender.UNSPECIFIED, Gender.valueOf("UNSPECIFIED"));
	}

}
