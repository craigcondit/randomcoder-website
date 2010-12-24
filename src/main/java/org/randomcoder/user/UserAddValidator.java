package org.randomcoder.user;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.*;

import org.randomcoder.validation.DataValidationUtils;

/**
 * Validator used for adding users.
 * 
 * <pre>
 * Copyright (c) 2006-2007, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
public class UserAddValidator implements Validator
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
	private UserDao userDao;
	
	/**
	 * Sets the minimum password length.
	 * @param minimumPasswordLength minimum password length
	 */
	public void setMinimumPasswordLength(int minimumPasswordLength)
	{
		this.minimumPasswordLength = minimumPasswordLength;
	}
	
	/**
	 * Sets the minimum username length.
	 * @param minimumUsernameLength minimum username length
	 */
	public void setMinimumUsernameLength(int minimumUsernameLength)
	{
		this.minimumUsernameLength = minimumUsernameLength;
	}
	
	/**
	 * Sets the UserDao implementation to use.
	 * @param userDao UserDao implementation
	 */
	@Required
	public void setUserDao(UserDao userDao)
	{
		this.userDao = userDao;
	}
	
	/**
	 * Determines if this validator supports the given class.
	 * @param targetClass class to check
	 * @return true if targetClass is {@code UserAddCommand}, false otherwise
	 */
	@Override
	public boolean supports(Class targetClass)
	{
		return UserAddCommand.class.equals(targetClass);
	}

	/**
	 * Validates the given object.
	 * @param target object to validate
	 * @param errors error object to populate with validation errors
	 */
	@Override
	public void validate(Object target, Errors errors)
	{
		UserAddCommand command = (UserAddCommand) target;
		
		if (!validateCommon(command, errors)) return;
		
		// username
		String userName = command.getUserName();
		if (userName == null)
		{
			errors.rejectValue("userName", ERROR_USERNAME_REQUIRED, "Username required.");
		}
		else if (userName.length() < minimumUsernameLength)
		{
			errors.rejectValue("userName", ERROR_USERNAME_TOO_SHORT, "Username too short.");
		}
		else if (userDao.findByUserName(userName) != null)
		{
			errors.rejectValue("userName", ERROR_USERNAME_EXISTS, "Username exists.");			
		}
		
		// password
		String password = command.getPassword();
		if (password == null || password.trim().length() == 0)
		{
			errors.rejectValue("password", ERROR_PASSWORD_REQUIRED, "Password required.");							
		}
		
		// password2, but only if no other errors
		if (errors.getFieldErrorCount("password2") == 0)
		{
			String password2 = command.getPassword2();
			if (password2 == null || password2.trim().length() == 0)
			{
				errors.rejectValue("password2", ERROR_PASSWORD_REQUIRED, "Password required.");											
			}
		}
			
	}

	/**
	 * Validate errors common to this class and subclasses.
	 * @param command command to validate
	 * @param errors errors
	 * @return true if validation should continue, false otherwise
	 */
	protected boolean validateCommon(UserAddCommand command, Errors errors)
	{
		if (command == null)
		{
			errors.reject(ERROR_USER_NULL, "Null data received");
			// do not continue processing, as this will lead to NPEs later
			return false;
		}
		
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
		
		// web site
		String website = command.getWebsite();
		if (website != null)
		{
			if (website.length() > 255 || !DataValidationUtils.isValidUrl(website))
			{
				errors.rejectValue("website", ERROR_WEBSITE_INVALID, "Website invalid.");
			}
		}
		
		// password (if specified)
		String password = StringUtils.defaultIfEmpty(command.getPassword(), "");
		
		if (password.length() > 0)
		{
			// password is specified, so validate it
			if (password.trim().length() < minimumPasswordLength)
			{
				errors.rejectValue("password", ERROR_PASSWORD_TOO_SHORT, "Password too short.");				
			}
				
		}
		
		// compare passwords if at least one is specified
		String password2 = StringUtils.defaultIfEmpty(command.getPassword2(), "");
		
		if (password.length() > 0 || password2.length() > 0)
		{			
			if (!password.equals(password2))
			{
				errors.rejectValue("password2", ERROR_PASSWORD_NO_MATCH, "Passwords don't match.");								
			}
		}
		
		return true;
	}
}
