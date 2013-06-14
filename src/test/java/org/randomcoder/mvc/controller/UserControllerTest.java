package org.randomcoder.mvc.controller;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.security.Principal;
import java.util.*;

import org.easymock.IMocksControl;
import org.junit.*;
import org.randomcoder.bo.UserBusiness;
import org.randomcoder.db.User;
import org.randomcoder.mvc.command.*;
import org.randomcoder.mvc.validator.*;
import org.springframework.ui.Model;
import org.springframework.validation.*;
import org.springframework.web.bind.WebDataBinder;

@SuppressWarnings("javadoc")
public class UserControllerTest
{
	private IMocksControl control;
	private UserBusiness ub;
	private UserController c;
	private ChangePasswordValidator cpv;
	private AccountCreateValidator acv;
	private UserProfileValidator upv;
	private Principal p;
	private BindingResult br;
	private WebDataBinder wdb;
	private Model m;

	@Before
	public void setUp()
	{
		control = createControl();
		ub = control.createMock(UserBusiness.class);
		m = control.createMock(Model.class);
		cpv = control.createMock(ChangePasswordValidator.class);
		p = control.createMock(Principal.class);
		br = control.createMock(BindingResult.class);
		wdb = control.createMock(WebDataBinder.class);
		acv = control.createMock(AccountCreateValidator.class);
		upv = control.createMock(UserProfileValidator.class);
		c = new UserController();
		c.setUserBusiness(ub);
		c.setChangePasswordValidator(cpv);
		c.setDefaultPageSize(10);
		c.setMaximumPageSize(25);
		c.setAccountCreateValidator(acv);
		c.setUserProfileValidator(upv);
	}

	@After
	public void tearDown()
	{
		acv = null;
		upv = null;
		br = null;
		p = null;
		c = null;
		cpv = null;
		m = null;
		ub = null;
		control = null;
	}

	@Test
	public void testInitBinder() throws Exception
	{
		expect(wdb.getTarget()).andReturn(new Object());
		control.replay();

		c.initBinder(wdb);
		control.verify();
	}

	@Test
	public void testInitBinderUserProfile() throws Exception
	{
		expect(wdb.getTarget()).andReturn(new UserProfileCommand());
		wdb.setValidator(upv);
		control.replay();

		c.initBinder(wdb);
		control.verify();
	}

	@Test
	public void testInitBinderAccountCreate() throws Exception
	{
		expect(wdb.getTarget()).andReturn(new AccountCreateCommand());
		wdb.setValidator(acv);
		control.replay();

		c.initBinder(wdb);
		control.verify();
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

	@Test
	public void testChangePassword()
	{
		ChangePasswordCommand command = new ChangePasswordCommand();

		User user = new User();

		expect(p.getName()).andReturn("test");
		expect(ub.findUserByNameEnabled("test")).andReturn(user);
		control.replay();

		assertEquals("change-password", c.changePassword(command, p));
		assertSame(user, command.getUser());
		control.verify();
	}

	@Test
	public void testChangePasswordCancel()
	{
		assertEquals("user-profile-redirect", c.changePasswordCancel());
	}

	@Test
	public void testChangePasswordSubmit()
	{
		ChangePasswordCommand command = new ChangePasswordCommand();
		command.setPassword("password");
		User user = new User();

		expect(p.getName()).andReturn("test");
		expect(ub.findUserByNameEnabled("test")).andReturn(user);
		cpv.validate(same(command), isA(Errors.class));
		expect(br.hasErrors()).andReturn(false);
		ub.changePassword("test", "password");
		control.replay();

		assertEquals("user-profile-redirect", c.changePasswordSubmit(command, br, p));
		control.verify();
		assertSame(user, command.getUser());
	}

	@Test
	public void testChangePasswordSubmitErrors()
	{
		ChangePasswordCommand command = new ChangePasswordCommand();
		command.setPassword("password");
		User user = new User();

		expect(p.getName()).andReturn("test");
		expect(ub.findUserByNameEnabled("test")).andReturn(user);
		cpv.validate(same(command), isA(Errors.class));
		expect(br.hasErrors()).andReturn(true);
		control.replay();

		assertEquals("change-password", c.changePasswordSubmit(command, br, p));
		control.verify();
		assertSame(user, command.getUser());
	}

	@Test
	public void testUserProfile()
	{
		UserProfileCommand command = new UserProfileCommand();

		User user = new User();

		expect(p.getName()).andReturn("test");
		expect(ub.findUserByName("test")).andReturn(user);
		expect(m.addAttribute("user", user)).andReturn(m);
		control.replay();

		assertEquals("user-profile", c.userProfile(command, m, p));
		control.verify();
	}

	@Test
	public void testUserProfileCancel()
	{
		assertEquals("default", c.userProfileCancel());
	}

	@Test
	public void testUserProfileSubmit()
	{
		UserProfileCommand command = new UserProfileCommand();
		User user = new User();
		user.setId(1L);

		expect(p.getName()).andReturn("test");
		expect(ub.findUserByName("test")).andReturn(user);
		expect(br.hasErrors()).andReturn(false);
		ub.updateUser(command, 1L);
		control.replay();

		assertEquals("default", c.userProfileSubmit(command, br, m, p));
		control.verify();
	}

	@Test
	public void testUserProfileError()
	{
		UserProfileCommand command = new UserProfileCommand();
		User user = new User();
		user.setId(1L);

		expect(p.getName()).andReturn("test");
		expect(ub.findUserByName("test")).andReturn(user);
		expect(br.hasErrors()).andReturn(true);
		expect(m.addAttribute("user", user)).andReturn(m);
		control.replay();

		assertEquals("user-profile", c.userProfileSubmit(command, br, m, p));
		control.verify();
	}

	@Test
	public void testAccountCreate()
	{
		assertEquals("account-create", c.accountCreate(null));
	}

	@Test
	public void testAccountCreateCancel()
	{
		assertEquals("default", c.accountCreateCancel());
	}

	@Test
	public void testAccountCreateSubmit()
	{
		AccountCreateCommand command = new AccountCreateCommand();

		expect(br.hasErrors()).andReturn(false);
		ub.createAccount(command);
		control.replay();

		assertEquals("account-create-done", c.accountCreateSubmit(command, br));
		control.verify();
	}
	
	@Test
	public void testAccountCreateSubmitError()
	{
		AccountCreateCommand command = new AccountCreateCommand();

		expect(br.hasErrors()).andReturn(true);
		control.replay();

		assertEquals("account-create", c.accountCreateSubmit(command, br));
		control.verify();
	}
}