package org.randomcoder.user;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.*;
import org.randomcoder.io.Producer;

/**
 * Command class for adding users.
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
public class AccountCreateCommand implements Serializable, Producer<User>
{	
	private static final long serialVersionUID = 7346261261522108772L;
	
	private String userName;
	private String emailAddress;
	private String website;
	private String password;
	private String password2;
	
	/**
	 * Gets the username of this user.
	 * @return user name
	 */
	public String getUserName()
	{
		return userName;
	}
	
	/**
	 * Sets the username of this user.
	 * @param userName user name
	 */
	public void setUserName(String userName)
	{
		this.userName = StringUtils.trimToNull(userName);
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
	 * Sets the web site for this user.
	 * @param website web site
	 */
	public void setWebsite(String website)
	{
		this.website = StringUtils.trimToNull(website);
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
	 * Gets the password associated with this user. 
	 * @return password
	 */
	public String getPassword()
	{
		return password;
	}
	
	/**
	 * Sets the password associated with this user.
	 * @param password password
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	/**
	 * Gets the password for this user again for validation.
	 * @return password
	 */
	public String getPassword2()
	{
		return password2;
	}
	
	/**
	 * Sets the password for this user again for validation.
	 * @param password2 password
	 */
	public void setPassword2(String password2)
	{
		this.password2 = password2;
	}
	
	/**
	 * Writes out the contents of the current form to the given user.
	 */
	@Override
	public void produce(User user)
	{		
		user.setUserName(userName);
		user.setEmailAddress(emailAddress);
		user.setWebsite(website);
		user.setEnabled(true);
		user.setPassword(User.hashPassword(password));			
		user.setRoles(new ArrayList<Role>());
	}

	/**
	 * Gets a string representation of this object, suitable for debugging.
	 * @return string representation of this object
	 */
	@Override
	public String toString()
	{
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
}
