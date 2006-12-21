package com.randomcoder.user;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.*;
import org.springframework.validation.*;

import com.randomcoder.test.mock.UserDaoMock;

public class UserEditValidatorTest
{
	private UserEditValidator validator;
	private UserDaoMock userDao;
	
	@Before public void setUp() throws Exception
	{		
		userDao = new UserDaoMock();
		validator = new UserEditValidator();
		validator.setMinimumPasswordLength(6);
		validator.setMinimumUsernameLength(6);
		validator.setUserDao(userDao);
	}

	@Test	public void testSupports()
	{
		assertTrue("Validator doesn't support command class", validator.supports(UserEditCommand.class));
	}

	@Test	public void testValidate()
	{
		FieldError error;
		BindException errors;
		
		// setup
		UserEditCommand command = new UserEditCommand();
		
		User user = new User();
		user.setUserName("existing-user");
		user.setEmailAddress("existing@example.com");
		user.setPassword(User.hashPassword("Password1"));
		user.setRoles(new ArrayList<Role>());
		user.setEnabled(true);		
		Long id = userDao.create(user);
		
		command.setId(id);
		command.consume(user);
		
		// no command
		errors = new BindException(command, "command");
		validator.validate(null, errors);
		assertEquals("Wrong number of errors occurred", 1, errors.getErrorCount());
		
		// no data supplied
		command.setId(null);
		command.setEmailAddress(null);
		command.setPassword(null);
		command.setPassword2(null);
		command.setRoles(new Role[] {});
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong number of errors occurred", 2, errors.getErrorCount());
		assertEquals("Wrong error count for emailAddress", 1, errors.getFieldErrorCount("emailAddress"));
		
		command.setId(id);
		
		// email address invalid
		command.setEmailAddress("bogus email address");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for emailAddress", 1, errors.getFieldErrorCount("emailAddress"));
		error = (FieldError) errors.getFieldErrors("emailAddress").get(0);
		assertEquals("Wrong error code", "error.user.emailaddress.invalid", error.getCode());
		
		// email address valid
		command.setEmailAddress("valid@example.com");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for emailAddress", 0, errors.getFieldErrorCount("emailAddress"));
		
		// password too short
		command.setPassword("short");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for password", 1, errors.getFieldErrorCount("password"));
		error = (FieldError) errors.getFieldErrors("password").get(0);
		assertEquals("Wrong error code", "error.user.password.tooshort", error.getCode());
		
		// password valid
		command.setPassword("Password1");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for password", 0, errors.getFieldErrorCount("password"));
		
		// password 2 doesn't match		
		command.setPassword2("Password2");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for password2", 1, errors.getFieldErrorCount("password2"));
		error = (FieldError) errors.getFieldErrors("password2").get(0);
		assertEquals("Wrong error code", "error.user.password.nomatch", error.getCode());
		
		// all data valid
		command.setPassword("Password2");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Errors occurred", 0, errors.getErrorCount());
	}

	@After public void tearDown() throws Exception
	{
		validator = null;
		userDao = null;
	}
}
