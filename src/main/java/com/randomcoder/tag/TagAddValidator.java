package com.randomcoder.tag;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.*;


/**
 * Validator used for adding tags.
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
public class TagAddValidator implements Validator
{
	private static final String ERROR_TAG_NULL = "error.tag.null";
	private static final String ERROR_NAME_REQUIRED = "error.tag.name.required";
	private static final String ERROR_NAME_EXISTS = "error.tag.name.exists";
	private static final String ERROR_DISPLAY_NAME_REQUIRED = "error.tag.displayname.required";
	
	private TagDao tagDao;
	
	/**
	 * Sets the TagDao implementation to use.
	 * @param tagDao TagDao implementation
	 */
	@Required
	public void setTagDao(TagDao tagDao)
	{
		this.tagDao = tagDao;
	}
	
	/**
	 * Determines if this validator supports the given class.
	 * @param targetClass class to check
	 * @return true if class is {@code TagAddCommand}, false otherwise
	 */
	@Override
	public boolean supports(Class targetClass)
	{
		return TagAddCommand.class.equals(targetClass);
	}

	/**
	 * Validates the given object.
	 * @param target object to validate
	 * @param errors errors object to hold resulting validation errors
	 */
	@Override
	public void validate(Object target, Errors errors)
	{
		TagAddCommand command = (TagAddCommand) target;
		
		if (!validateCommon(command, errors)) return;
		
		// tag name
		String name = command.getName();
		if (name == null)
		{
			errors.rejectValue("name", ERROR_NAME_REQUIRED, "Name required.");
		}
		else if (tagDao.findByName(name) != null)
		{
			errors.rejectValue("name", ERROR_NAME_EXISTS, "Name exists.");			
		}
	}

	/**
	 * Validate errors common to this class and subclasses.
	 * @param command command to validate
	 * @param errors errors
	 * @return true if validation should continue, false otherwise
	 */
	protected boolean validateCommon(TagAddCommand command, Errors errors)
	{
		if (command == null)
		{
			errors.reject(ERROR_TAG_NULL, "Null data received");
			// do not continue processing, as this will lead to NPEs later
			return false;
		}
		
		// display name
		String displayName = command.getDisplayName();
		if (displayName == null)
		{
			errors.rejectValue("displayName", ERROR_DISPLAY_NAME_REQUIRED, "Display name required.");
		}
		
		return true;
	}
}
