package com.randomcoder.user;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.*;
import org.apache.commons.logging.*;

import com.randomcoder.bean.*;
import com.randomcoder.io.Producer;

/**
 * Command class for adding users.
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
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
public class UserAddCommand implements Serializable, Producer<User>
{
	private static final long serialVersionUID = -4063217084413700225L;
	
	private static final Log logger = LogFactory.getLog(UserAddCommand.class);
	
	private String userName;
	private String emailAddress;
	private boolean enabled;	
	private String password;
	private String password2;
	
	private Role[] roles;
	
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
	 * Determines if this user is enabled.
	 * @return true if enabled, false otherwise
	 */
	public boolean isEnabled()
	{
		return enabled;
	}
	
	/**
	 * Sets whether this user is enabled.
	 * @param enabled true if enabled, false otherwise
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
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
	 * Gets the roles associated with this user.
	 * @return array of roles
	 */
	public Role[] getRoles()
	{
		return roles;
	}
	
	/**
	 * Sets the roles associated with this user.
	 * @param roles array of roles
	 */
	public void setRoles(Role[] roles)
	{
		this.roles = roles;
	}
	
	/**
	 * Writes out the contents of the current form to the given user.
	 */
	public void produce(User user)
	{		
		if (user.getId() == null)
			user.setUserName(userName); // only for new users
		
		user.setEmailAddress(emailAddress);
		user.setEnabled(enabled);
		
		if (password != null && password.trim().length() > 0)
		{
			user.setPassword(User.hashPassword(password));
		}
		
		if (user.getRoles() == null) user.setRoles(new ArrayList<Role>());
		
		
		
		Set<Role> currentRoles = new HashSet<Role>(user.getRoles());
		Set<Role> selectedRoles = new HashSet<Role>();
		if (roles != null) selectedRoles.addAll(Arrays.asList(roles));
		
		// get list of deleted roles (current - selected)
		Set<Role> deletedRoles = new HashSet<Role>(currentRoles);
		deletedRoles.removeAll(selectedRoles);
		
		// get list of added roles (selected - current)
		Set<Role> addedRoles = new HashSet<Role>(selectedRoles);
		addedRoles.removeAll(currentRoles);
		
		if (logger.isDebugEnabled())
		{
			logger.debug("Deleted roles: ");
			for (Role role : deletedRoles) logger.debug("  " + role);

			logger.debug("Added roles: ");
			for (Role role : addedRoles) logger.debug("  " + role);			
		}
		
		// remove deleted roles 
		user.getRoles().removeAll(deletedRoles);
		
		// add new roles
		user.getRoles().addAll(addedRoles);		
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
