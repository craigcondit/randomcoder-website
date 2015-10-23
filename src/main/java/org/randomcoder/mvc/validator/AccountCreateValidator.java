package org.randomcoder.mvc.validator;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.randomcoder.bo.UserBusiness;
import org.randomcoder.mvc.command.AccountCreateCommand;
import org.randomcoder.validation.DataValidationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.*;

/**
 * Validator used for adding accounts.
 */
@Component("accountCreateValidator")
public class AccountCreateValidator implements Validator
{
	private static final int DEFAULT_MINIMUM_USERNAME_LENGTH = 3;
	private static final int DEFAULT_MINIMUM_PASSWORD_LENGTH = 6;

	private static final String ERROR_USER_NULL = "error.user.null";
	private static final String ERROR_USERNAME_REQUIRED = "error.user.username.required";
	private static final String ERROR_USERNAME_TOO_SHORT = "error.user.username.tooshort";
	private static final String ERROR_USERNAME_EXISTS = "error.user.username.exists";
	private static final String ERROR_EMAIL_ADDRESS_REQUIRED = "error.user.emailaddress.required";
	private static final String ERROR_EMAIL_ADDRESS_INVALID = "error.user.emailaddress.invalid";
	private static final String ERROR_WEBSITE_INVALID = "error.user.website.invalid";
	private static final String ERROR_PASSWORD_REQUIRED = "error.user.password.required";
	private static final String ERROR_PASSWORD_TOO_SHORT = "error.user.password.tooshort";
	private static final String ERROR_PASSWORD_NO_MATCH = "error.user.password.nomatch";

	private int minimumPasswordLength = DEFAULT_MINIMUM_PASSWORD_LENGTH;
	private int minimumUsernameLength = DEFAULT_MINIMUM_USERNAME_LENGTH;
	private UserBusiness userBusiness;

	/**
	 * Sets the minimum password length.
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
	 * Sets the minimum username length.
	 * 
	 * @param minimumUsernameLength
	 *            minimum username length
	 */
	@Value("${username.length.minimum}")
	public void setMinimumUsernameLength(int minimumUsernameLength)
	{
		this.minimumUsernameLength = minimumUsernameLength;
	}

	/**
	 * Sets the UserBusiness implementation to use.
	 * 
	 * @param userBusiness
	 *            UserBusiness implementation
	 */
	@Inject
	public void setUserBusiness(UserBusiness userBusiness)
	{
		this.userBusiness = userBusiness;
	}

	/**
	 * Determines if this validator supports the given class.
	 * 
	 * @param targetClass
	 *            class to test
	 * @return true if targetClass is {@code AccountCreateCommand}, false
	 *         otherwise
	 */
	@Override
	public boolean supports(Class<?> targetClass)
	{
		return AccountCreateCommand.class.equals(targetClass);
	}

	/**
	 * Validates the given object.
	 * 
	 * @param target
	 *            object to validate
	 * @param errors
	 *            error object to populate with validation errors
	 */
	@Override
	public void validate(Object target, Errors errors)
	{
		AccountCreateCommand command = (AccountCreateCommand) target;

		if (command == null)
		{
			errors.reject(ERROR_USER_NULL, "Null data received");
			// do not continue processing, as this will lead to NPEs later
			return;
		}

		// new account using password
		validateUsername(command, errors);
		validatePassword(command, errors);
		validateEmailAddress(command, errors);
		validateWebsite(command, errors);
	}

	private void validateWebsite(AccountCreateCommand command, Errors errors)
	{
		// web site
		String website = command.getWebsite();
		if (website != null)
		{
			if (website.length() > 255 || !DataValidationUtils.isValidUrl(website))
			{
				errors.rejectValue("website", ERROR_WEBSITE_INVALID, "Website invalid.");
			}
		}
	}

	private void validateEmailAddress(AccountCreateCommand command, Errors errors)
	{
		// email address
		String emailAddress = command.getEmailAddress();
		if (emailAddress == null)
		{
			errors.rejectValue("emailAddress", ERROR_EMAIL_ADDRESS_REQUIRED, "Email address required.");
		}
		else if (!DataValidationUtils.isValidEmailAddress(emailAddress))
		{
			errors.rejectValue("emailAddress", ERROR_EMAIL_ADDRESS_INVALID, "Email address invalid.");
		}
	}

	private void validatePassword(AccountCreateCommand command, Errors errors)
	{
		// password
		String password = StringUtils.defaultIfEmpty(command.getPassword(), "");
		String password2 = StringUtils.defaultIfEmpty(command.getPassword2(), "");

		if (password.trim().length() == 0)
		{
			errors.rejectValue("password", ERROR_PASSWORD_REQUIRED, "Password required.");
		}
		else if (password.trim().length() < minimumPasswordLength)
		{
			errors.rejectValue("password", ERROR_PASSWORD_TOO_SHORT, new Object[] { Integer.valueOf(minimumPasswordLength) }, "Password too short.");
		}

		// compare passwords if at least one is specified
		if (password.length() > 0 || password2.length() > 0)
		{
			if (!password.equals(password2))
			{
				errors.rejectValue("password2", ERROR_PASSWORD_NO_MATCH, "Passwords don't match.");
			}
		}

		// password2, but only if no other errors
		if (errors.getFieldErrorCount("password2") == 0)
		{
			if (password2 == null || password2.trim().length() == 0)
			{
				errors.rejectValue("password2", ERROR_PASSWORD_REQUIRED, "Password required.");
			}
		}
	}

	private void validateUsername(AccountCreateCommand command, Errors errors)
	{
		// username
		String userName = command.getUserName();
		if (userName == null)
		{
			errors.rejectValue("userName", ERROR_USERNAME_REQUIRED, "Username required.");
		}
		else if (userName.length() < minimumUsernameLength)
		{
			errors.rejectValue("userName", ERROR_USERNAME_TOO_SHORT, new Object[] { Integer.valueOf(minimumUsernameLength) }, "Username too short.");
		}
		else if (userBusiness.findUserByName(userName) != null)
		{
			errors.rejectValue("userName", ERROR_USERNAME_EXISTS, "Username exists.");
		}
	}
}