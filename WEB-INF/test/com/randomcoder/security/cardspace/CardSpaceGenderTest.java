package com.randomcoder.security.cardspace;


import static org.junit.Assert.*;

import org.junit.Test;

public class CardSpaceGenderTest
{
	@Test
	public void testValues()
	{
		CardSpaceGender[] genders = CardSpaceGender.values();
		assertNotNull(genders);
		assertEquals(3, genders.length);		
	}

	@Test
	public void testValueOf()
	{
		assertEquals(CardSpaceGender.MALE, CardSpaceGender.valueOf("MALE"));
		assertEquals(CardSpaceGender.FEMALE, CardSpaceGender.valueOf("FEMALE"));
		assertEquals(CardSpaceGender.UNSPECIFIED, CardSpaceGender.valueOf("UNSPECIFIED"));
	}

}
