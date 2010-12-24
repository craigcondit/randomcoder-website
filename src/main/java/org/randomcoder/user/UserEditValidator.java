package org.randomcoder.user;

import org.springframework.validation.Errors;

/**
 * Validator used for editing users.
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
public class UserEditValidator extends UserAddValidator
{
	private static final String ERROR_USER_ID_REQUIRED = "error.user.id.required";
	
	/**
	 * Determines if this validator supports the given class.
	 * @param targetClass class to check
	 * @return true if targetClass is {@code UserEditCommand}, false otherwise
	 */
	@Override
	public boolean supports(Class targetClass)
	{
		return UserEditCommand.class.equals(targetClass);
	}

	/**
	 * Validates the given object.
	 * @param target object to validate
	 * @param errors error object to populate with validation errors
	 */
	@Override
	public void validate(Object target, Errors errors)
	{
		UserEditCommand command = (UserEditCommand) target;
		
		if (!validateCommon(command, errors)) return;
		
		Long id = command.getId();

		if (id == null)
		{
			errors.rejectValue("id", ERROR_USER_ID_REQUIRED, "id required");
		}
		
	}

}
