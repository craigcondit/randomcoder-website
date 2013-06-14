package org.randomcoder.mvc.controller;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;
import org.randomcoder.bo.UserBusiness;

@SuppressWarnings("javadoc")
public class UserControllerTest
{
	private IMocksControl control;
	private UserBusiness ub;
	private UserController c;

	@Before
	public void setUp()
	{
		control = createControl();
		ub = control.createMock(UserBusiness.class);
		c = new UserController();
		c.setUserBusiness(ub);
	}

	@After
	public void tearDown()
	{
		c = null;
		ub = null;
		control = null;
	}

	@Test
	public void testDeleteUser() throws Exception
	{
		ub.deleteUser(1L);
		control.replay();

		assertEquals("user-list-redirect", c.deleteUser(1L));
		control.verify();
	}
}