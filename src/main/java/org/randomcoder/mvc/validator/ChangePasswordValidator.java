package org.randomcoder.mvc.validator;

import org.randomcoder.db.User;
import org.randomcoder.mvc.command.ChangePasswordCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.*;

/**
 * Validator for the change password form.
 */
@Component("changePasswordValidator")
public class ChangePasswordValidator implements Validator
{
	private static final int DEFAULT_MINIMUM_PASSWORD_LENGTH = 6;

	private static final String ERROR_OLD_PASSWORD_REQUIRED = "error.changepassword.oldpassword.required";
	private static final String ERROR_OLD_PASSWORD_NO_MATCH = "error.changepassword.oldpassword.nomatch";

	private static final String ERROR_PASSWORD_REQUIRED = "error.changepassword.password.required";
	private static final String ERROR_PASSWORD_TOO_SHORT = "error.changepassword.password.tooshort";
	private static final String ERROR_PASSWORD_NO_MATCH = "error.changepassword.password.nomatch";

	private int minimumPasswordLength = DEFAULT_MINIMUM_PASSWORD_LENGTH;

	/**
	 * Sets the minimum password length required.
	 * 
	 * @param minimumPasswordLength
	 *            minimum password length
	 */
	@Value("${password.length.minimum}")
	public void setMinimumPasswordLength(int minimumPasswordLength)
	{
		this.minimumPasswordLength = minimumPasswordLength;
	}

	/**
	 * Determines if this validator supports the given class.
	 * 
	 * @param givenClass
	 *            class to check
	 * @return true if givenClass is {@code ChangePasswordCommand}, false
	 *         otherwise
	 */
	@Override
	public boolean supports(Class<?> givenClass)
	{
		return ChangePasswordCommand.class.equals(givenClass);
	}

	/**
	 * Validates the given object.
	 * 
	 * @param obj
	 *            object to validate
	 * @param errors
	 *            error object to populate with validation errors
	 */
	@Override
	public void validate(Object obj, Errors errors)
	{
		ChangePasswordCommand command = (ChangePasswordCommand) obj;

		User user = command.getUser();
		String oldPassword = command.getOldPassword();
		String password = command.getPassword();
		String password2 = command.getPassword2();

		if (user.getPassword() == null)
		{
			// no password, so user must not enter one
			if (oldPassword != null && oldPassword.length() > 0)
				errors.rejectValue("oldPassword", ERROR_OLD_PASSWORD_NO_MATCH, "Old password is wrong.");
		}
		else
		{
			// validate old password
			if (oldPassword == null || oldPassword.trim().length() == 0)
			{
				errors.rejectValue("oldPassword", ERROR_OLD_PASSWORD_REQUIRED, "Old password is required.");
			}
			else
			{
				String oldHash = User.hashPassword(oldPassword);
				String userHash = user.getPassword();
				if (!oldHash.equals(userHash))
				{
					errors.rejectValue("oldPassword", ERROR_OLD_PASSWORD_NO_MATCH, "Old password is wrong.");
				}
			}
		}

		// validate new password
		if (password == null || password.trim().length() == 0)
		{
			errors.rejectValue("password", ERROR_PASSWORD_REQUIRED, "Password is required.");
		}
		else if (password.length() < minimumPasswordLength)
		{
			errors.rejectValue("password", ERROR_PASSWORD_TOO_SHORT, new Object[] { Integer.valueOf(minimumPasswordLength) }, "Password too short.");
		}

		// validate new password match
		if (password2 == null || password2.trim().length() == 0)
		{
			errors.rejectValue("password2", ERROR_PASSWORD_REQUIRED, "Password is required.");
		}
		else if (password != null && !password.equals(password2))
		{
			errors.rejectValue("password2", ERROR_PASSWORD_NO_MATCH, "Passwords don't match.");
		}
	}
}
