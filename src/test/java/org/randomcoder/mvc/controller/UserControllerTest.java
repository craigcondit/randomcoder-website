package org.randomcoder.mvc.controller;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import org.easymock.IMocksControl;
import org.junit.*;
import org.randomcoder.bo.UserBusiness;
import org.randomcoder.db.User;
import org.randomcoder.mvc.command.UserListCommand;
import org.springframework.ui.Model;

@SuppressWarnings("javadoc")
public class UserControllerTest
{
	private IMocksControl control;
	private UserBusiness ub;
	private UserController c;
	private Model m;

	@Before
	public void setUp()
	{
		control = createControl();
		ub = control.createMock(UserBusiness.class);
		m = control.createMock(Model.class);
		c = new UserController();
		c.setUserBusiness(ub);
		c.setDefaultPageSize(10);
		c.setMaximumPageSize(25);
	}

	@After
	public void tearDown()
	{
		c = null;
		m = null;
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

	@Test
	public void testListUsersValueOutOfRange() throws Exception
	{
		List<User> users = new ArrayList<>();
		
		UserListCommand command = new UserListCommand();
		command.setStart(-1);
		command.setLimit(-1);
		
		expect(ub.listUsersInRange(0, 10)).andReturn(users);
		expect(ub.countUsers()).andReturn(0);
		expect(m.addAttribute("users", users)).andReturn(m);
		expect(m.addAttribute("pageCount", 0)).andReturn(m);
		expect(m.addAttribute("pageStart", 0)).andReturn(m);
		expect(m.addAttribute("pageLimit", 10)).andReturn(m);
		control.replay();

		c.listUsers(command, m);
		control.verify();		
	}
	
	@Test
	public void testListUsersLimitTooHigh() throws Exception
	{
		List<User> users = new ArrayList<>();
		
		UserListCommand command = new UserListCommand();
		command.setStart(-1);
		command.setLimit(100);
		
		expect(ub.listUsersInRange(0, 25)).andReturn(users);
		expect(ub.countUsers()).andReturn(0);
		expect(m.addAttribute("users", users)).andReturn(m);
		expect(m.addAttribute("pageCount", 0)).andReturn(m);
		expect(m.addAttribute("pageStart", 0)).andReturn(m);
		expect(m.addAttribute("pageLimit", 25)).andReturn(m);
		control.replay();

		c.listUsers(command, m);
		control.verify();		
	}
	
	@Test
	public void testListUsersFirstPage() throws Exception
	{
		List<User> users = new ArrayList<>();
		
		UserListCommand command = new UserListCommand();
		command.setStart(0);
		command.setLimit(25);
		
		expect(ub.listUsersInRange(0, 25)).andReturn(users);
		expect(ub.countUsers()).andReturn(0);
		expect(m.addAttribute("users", users)).andReturn(m);
		expect(m.addAttribute("pageCount", 0)).andReturn(m);
		expect(m.addAttribute("pageStart", 0)).andReturn(m);
		expect(m.addAttribute("pageLimit", 25)).andReturn(m);
		control.replay();

		c.listUsers(command, m);
		control.verify();		
	}
	
	@Test
	public void testListUsersLastPage() throws Exception
	{
		List<User> users = new ArrayList<>();
		
		UserListCommand command = new UserListCommand();
		command.setStart(75);
		command.setLimit(25);
		
		expect(ub.listUsersInRange(75, 25)).andReturn(users);
		expect(ub.countUsers()).andReturn(100);
		expect(m.addAttribute("users", users)).andReturn(m);
		expect(m.addAttribute("pageCount", 100)).andReturn(m);
		expect(m.addAttribute("pageStart", 75)).andReturn(m);
		expect(m.addAttribute("pageLimit", 25)).andReturn(m);
		control.replay();

		c.listUsers(command, m);
		control.verify();		
	}
	
	@Test
	public void testListUsersRollOffEnd() throws Exception
	{
		List<User> users = new ArrayList<>();
		
		UserListCommand command = new UserListCommand();
		command.setStart(76);
		command.setLimit(25);
		
		expect(ub.listUsersInRange(76, 25)).andReturn(users);
		expect(ub.countUsers()).andReturn(100);
		expect(m.addAttribute("users", users)).andReturn(m);
		expect(m.addAttribute("pageCount", 100)).andReturn(m);
		expect(m.addAttribute("pageStart", 76)).andReturn(m);
		expect(m.addAttribute("pageLimit", 25)).andReturn(m);
		control.replay();

		c.listUsers(command, m);
		control.verify();		
	}
}