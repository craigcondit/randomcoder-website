package com.randomcoder.security.cardspace;

import junit.framework.TestCase;

public class CardSpaceGenderTest extends TestCase
{
	public void testValues()
	{
		CardSpaceGender[] genders = CardSpaceGender.values();
		assertNotNull(genders);
		assertEquals(3, genders.length);		
	}

	public void testValueOf()
	{
		assertEquals(CardSpaceGender.MALE, CardSpaceGender.valueOf("MALE"));
		assertEquals(CardSpaceGender.FEMALE, CardSpaceGender.valueOf("FEMALE"));
		assertEquals(CardSpaceGender.UNSPECIFIED, CardSpaceGender.valueOf("UNSPECIFIED"));
	}
}