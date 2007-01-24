package com.randomcoder.user;

import java.util.Locale;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.*;

import com.randomcoder.security.cardspace.CardSpaceCredentials;
import com.randomcoder.validation.DataValidationUtils;

/**
 * Validator for UserProfileCommand objects.
 * 
 * <pre>
 * Copyright (c) 2007, Craig Condit. All rights reserved.
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
public class UserProfileValidator implements Validator
{
	private static final String ERROR_INFOCARD_REQUIRED = "error.profile.infocard.required";
	private static final String ERROR_INFOCARD_EXISTS = "error.profile.infocard.exists";
	private static final String ERROR_INFOCARD_PPID_REQUIRED = "error.profile.infocard.ppid.required";
	private static final String ERROR_INFOCARD_EMAIL_REQUIRED = "error.profile.infocard.email.required";
	
	private CardSpaceTokenDao cardSpaceTokenDao;
	
	/**
	 * Sets the CardSpaceTokenDao implementation to use.
	 * @param cardSpaceTokenDao CardSpaceTokenDao implementation
	 */
	@Required
	public void setCardSpaceTokenDao(CardSpaceTokenDao cardSpaceTokenDao)
	{
		this.cardSpaceTokenDao = cardSpaceTokenDao;
	}
	
	/**
	 * Determines if the target class is supported by this validator.
	 * @param targetClass target class
	 * @returns true if target class is UserProfileCommand, false otherwise
	 */
	public boolean supports(Class targetClass)
	{
		return UserProfileCommand.class.equals(targetClass);
	}

	/**
	 * Validates the given object.
	 * @param target target object to validate
	 * @param errors errors object to populate
	 */
	public void validate(Object target, Errors errors)
	{
		UserProfileCommand command = (UserProfileCommand) target;
		
		CardSpaceCredentials credentials = command.getXmlToken();
		
		if (credentials == null)
		{
			errors.rejectValue("xmlToken", ERROR_INFOCARD_REQUIRED, "infocard required");
			return;
		}
		
		// need PPID
		String ppid = credentials.getPrivatePersonalIdentifier();
		if (ppid == null)
		{
			errors.rejectValue("xmlToken", ERROR_INFOCARD_PPID_REQUIRED, "ppid required");
			return;
		}
		
		// check for existing
		String issuerHash = calculateIssuerHash(credentials);
		
		if (cardSpaceTokenDao.findByPrivatePersonalIdentifier(ppid, issuerHash) != null)
		{
			errors.rejectValue("xmlToken", ERROR_INFOCARD_EXISTS, "infocard exists");
			return;
		}
		
		// need email address
		String emailAddress = credentials.getEmailAddress();
		if (emailAddress == null || emailAddress.trim().length() == 0)
		{
			errors.rejectValue("xmlToken", ERROR_INFOCARD_EMAIL_REQUIRED, "email required");
			return;
		}
		
		// email address must be valid
		if (!DataValidationUtils.isValidEmailAddress(emailAddress))
		{
			errors.rejectValue("xmlToken", ERROR_INFOCARD_EMAIL_REQUIRED, "email required");
			return;
		}
		
	}
	
	private String calculateIssuerHash(CardSpaceCredentials credentials)
	{
		return DigestUtils.shaHex(credentials.getIssuerPublicKey()).toLowerCase(Locale.US);
	}	
}
