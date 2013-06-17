package org.randomcoder.mvc.validator;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.easymock.IMocksControl;
import org.junit.*;
import org.randomcoder.bo.UserBusiness;
import org.randomcoder.db.*;
import org.randomcoder.mvc.command.AccountCreateCommand;
import org.springframework.validation.BindException;

@SuppressWarnings("javadoc")
public class AccountCreateValidatorTest
{
	private IMocksControl control;
	private UserBusiness ub;
	private AccountCreateValidator validator;

	@Before
	public void setUp()
	{
		control = createControl();
		ub = control.createMock(UserBusiness.class);
		validator = new AccountCreateValidator();
		validator.setMinimumUsernameLength(6);
		validator.setMinimumPasswordLength(6);
		validator.setUserBusiness(ub);
	}

	@After
	public void tearDown()
	{
		validator = null;
		ub = null;
		control = null;
	}

	@Test
	public void testSupports()
	{
		assertTrue("Validator doesn't support command class", validator.supports(AccountCreateCommand.class));
	}

	@Test
	public void testValidate()
	{
		BindException errors;

		// setup
		AccountCreateCommand command = new AccountCreateCommand();

		User user = new User();
		user.setUserName("existing-user");
		user.setEmailAddress("existing@example.com");
		user.setPassword(User.hashPassword("Password1"));
		user.setRoles(new ArrayList<Role>());
		user.setEnabled(true);

		expect(ub.findUserByName("existing-user")).andStubReturn(user);
		expect(ub.findUserByName("new-user")).andStubReturn(null);
		control.replay();

		// null command
		errors = new BindException(command, "command");
		validator.validate(null, errors);
		assertEquals("Wrong number of errors occurred", 1, errors.getErrorCount());

		// empty form
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong number of errors occurred", 4, errors.getErrorCount());
		assertEquals("Wrong error count for userName", 1, errors.getFieldErrorCount("userName"));
		assertEquals("Wrong error count for emailAddress", 1, errors.getFieldErrorCount("emailAddress"));
		assertEquals("Wrong error count for password", 1, errors.getFieldErrorCount("password"));
		assertEquals("Wrong error count for password2", 1, errors.getFieldErrorCount("password2"));

		// username too short
		command.setUserName("short");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for userName", 1, errors.getFieldErrorCount("userName"));

		// username exists
		command.setUserName("existing-user");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for userName", 1, errors.getFieldErrorCount("userName"));

		// username valid
		command.setUserName("new-user");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for userName", 0, errors.getFieldErrorCount("userName"));

		// password too short (and no match with password2)
		command.setPassword("short");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for password", 1, errors.getFieldErrorCount("password"));
		assertEquals("Wrong error count for password2", 1, errors.getFieldErrorCount("password2"));

		// passwords valid
		command.setPassword("testPassword");
		command.setPassword2("testPassword");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for password", 0, errors.getFieldErrorCount("password"));
		assertEquals("Wrong error count for password2", 0, errors.getFieldErrorCount("password2"));

		// invalid email address
		command.setEmailAddress("bogus");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for emailAddress", 1, errors.getFieldErrorCount("emailAddress"));

		// valid email address
		command.setEmailAddress("test@example.com");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for emailAddress", 0, errors.getFieldErrorCount("emailAddress"));

		// invalid website
		command.setWebsite("bogus");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for website", 1, errors.getFieldErrorCount("website"));

		// valid website
		command.setWebsite("http://www.exmaple.com/");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for website", 0, errors.getFieldErrorCount("website"));

		assertEquals("Wrong number of errors occurred", 0, errors.getErrorCount());

		control.verify();
	}
}