package org.randomcoder.mvc.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.db.User;
import org.randomcoder.mvc.command.ChangePasswordCommand;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

public class ChangePasswordValidatorTest {
	private ChangePasswordValidator validator;

	@Before
	public void setUp() {
		validator = new ChangePasswordValidator();
		validator.setMinimumPasswordLength(6);
	}

	@After
	public void tearDown() {
		validator = null;
	}

	@Test
	public void testSupports() {
		assertTrue("Validator doesn't support command class", validator.supports(ChangePasswordCommand.class));
	}

	@Test
	public void testValidate() {
		FieldError error;
		BindException errors;

		// setup
		ChangePasswordCommand command = new ChangePasswordCommand();
		User user = new User();
		user.setUserName("validate-user");
		command.setUser(user);

		// old password supplied when user doesn't have one
		user.setPassword(null);
		command.setOldPassword("Password1");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for oldPassword", 1, errors.getFieldErrorCount("oldPassword"));

		// no data supplied
		user.setPassword(User.hashPassword("Password1"));
		command.setOldPassword(null);
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong number of errors occurred", 3, errors.getErrorCount());
		assertEquals("Wrong error count for oldPassword", 1, errors.getFieldErrorCount("oldPassword"));
		assertEquals("Wrong error count for password", 1, errors.getFieldErrorCount("password"));
		assertEquals("Wrong error count for password2", 1, errors.getFieldErrorCount("password2"));

		// incorrect old password
		command.setOldPassword("bogus1");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for oldPassword", 1, errors.getFieldErrorCount("oldPassword"));
		error = errors.getFieldErrors("oldPassword").get(0);
		assertEquals("Wrong error code", "error.changepassword.oldpassword.nomatch", error.getCode());

		// correct old password
		command.setOldPassword("Password1");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for oldPassword", 0, errors.getFieldErrorCount("oldPassword"));

		// password too short
		command.setPassword("short");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for password", 1, errors.getFieldErrorCount("password"));
		error = errors.getFieldErrors("password").get(0);
		assertEquals("Wrong error code", "error.changepassword.password.tooshort", error.getCode());

		// password2 doesn't match
		command.setPassword("Password2");
		command.setPassword2("Password2-nomatch");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for password2", 1, errors.getFieldErrorCount("password2"));
		error = errors.getFieldErrors("password2").get(0);
		assertEquals("Wrong error code", "error.changepassword.password.nomatch", error.getCode());

		// ok
		command.setPassword2("Password2");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Errors occurred", 0, errors.getErrorCount());
	}
}
