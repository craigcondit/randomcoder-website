package org.randomcoder.controller;

import static org.junit.Assert.*;

import org.junit.*;

@SuppressWarnings("javadoc")
public class LoginControllerTest
{
	private LoginController c;
	
	@Before
	public void setUp()
	{
		c = new LoginController();
	}

	@After
	public void tearDown()
	{
		c = null;
	}

	@Test
	public void testLogin()
	{
		assertEquals("login", c.login());
	}

	@Test
	public void testLoginError()
	{
		assertEquals("login-error", c.loginError());
	}
}