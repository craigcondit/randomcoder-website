package com.randomcoder.user;

import org.springframework.validation.*;


/**
 * Validator for the change password form.
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
	 * @param minimumPasswordLength minimum password length
	 */
	public void setMinimumPasswordLength(int minimumPasswordLength)
	{
		this.minimumPasswordLength = minimumPasswordLength;
	}
	
	/**
	 * Determines if this validator supports the given class.
	 * @param givenClass class to check
	 * @return
	 * 	true if givenClass is {@code ChangePasswordCommand}, false otherwise
	 */
	@Override
	public boolean supports(Class givenClass)
	{
		return ChangePasswordCommand.class.equals(givenClass);
	}
	
	/**
	 * Validates the given object.
	 * @param obj object to validate
	 * @param errors error object to populate with validation errors
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
			errors.rejectValue("password", ERROR_PASSWORD_TOO_SHORT, "Password too short.");
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
