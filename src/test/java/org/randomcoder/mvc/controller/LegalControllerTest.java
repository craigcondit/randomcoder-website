package org.randomcoder.mvc.controller;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class LegalControllerTest {
	private LegalController c;

	@Before
	public void setUp() {
		c = new LegalController();
	}

	@After
	public void tearDown() {
		c = null;
	}

	@Test
	public void testAbout() {
		assertEquals("legal-about", c.about());
	}

	@Test
	public void testLicense() {
		assertEquals("legal-license", c.license());
	}
}