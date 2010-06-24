package com.randomcoder.user;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.randomcoder.io.Producer;
import com.randomcoder.security.cardspace.CardSpaceCredentials;

/**
 * Command class for updating a user profile.
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
public class UserProfileCommand implements Serializable, Producer<User>
{
	private static final long serialVersionUID = 8464807327958297647L;
	
	private String formType;
	
	private CardSpaceCredentials xmlToken;
	
	private String emailAddress;
	private String website;
	
	/**
	 * Gets the type of form (currently PREFS or INFOCARD).
	 * @return form type
	 */
	public String getFormType()
	{
		return formType;
	}
	
	/**
	 * Sets the type of form (currently PREFS or INFOCARD).
	 * @param formType form type
	 */
	public void setFormType(String formType)
	{
		this.formType = formType;
	}
	
	/**
	 * Gets the CardSpaceCredentials posted to this form.
	 * @return CardSpace credentials
	 */
	public CardSpaceCredentials getXmlToken()
	{
		return xmlToken;
	}
	
	/**
	 * Sets the CardSpaceCredentials posted to this form.
	 * @param xmlToken CardSpace credentials
	 */
	public void setXmlToken(CardSpaceCredentials xmlToken)
	{
		this.xmlToken = xmlToken;
	}
	
	/**
	 * Gets the email address of this user.
	 * @return email address
	 */
	public String getEmailAddress()
	{
		return emailAddress;
	}
	
	/**
	 * Sets the email address of this user.
	 * @param emailAddress email address
	 */
	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = StringUtils.trimToNull(emailAddress);
	}
	
	/**
	 * Gets the website for this user.
	 * @return web site
	 */	
	public String getWebsite()
	{
		return website;
	}
	
	/**
	 * Sets the website for this user.
	 * @param website seb site
	 */
	public void setWebsite(String website)
	{
		this.website = StringUtils.trimToNull(website);
	}

	@Override
	public void produce(User target)
	{
		target.setWebsite(website);
		target.setEmailAddress(emailAddress);
	}
}
