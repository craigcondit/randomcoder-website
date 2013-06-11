package org.randomcoder.controller;

import static org.junit.Assert.*;

import org.junit.*;

@SuppressWarnings("javadoc")
public class LegalControllerTest
{
	private LegalController c;
	
	@Before
	public void setUp()
	{
		c = new LegalController();
	}

	@After
	public void tearDown()
	{
		c = null;
	}

	@Test
	public void testAbout()
	{
		assertEquals("legal-about", c.about());
	}

	@Test
	public void testLicense()
	{
		assertEquals("legal-license", c.license());
	}
}