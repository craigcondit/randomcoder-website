package com.randomcoder.security;

import java.security.Principal;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;

import com.randomcoder.bean.*;
import com.randomcoder.citadel.realm.PasswordSecurityRealm;
import com.randomcoder.dao.UserDao;

/**
 * {@code PasswordSecurityRealm} implementation.
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
public class UserSecurityRealm implements PasswordSecurityRealm
{
	private UserDao userDao;

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
	 * Authenticates a user via username / password.
	 * 
	 * @param username user name
	 * @param password supplied password
	 * @param request HTTP request for authentication
	 * @return {@code UserPrincipal} representing the authenticated user, or null
	 * if user is invalid
	 */
	public Principal validatePassword(String username, String password, HttpServletRequest request)
	{
		User user = userDao.findByUserName(username);
		if (user == null)
			return null;
		if (password == null)
			return null;

		// check password
		String dbPassword = user.getPassword();
		if (dbPassword == null)
			return null;
		dbPassword = dbPassword.toLowerCase(Locale.US);
		String userPassword = User.hashPassword(password);

		if (!userPassword.equals(dbPassword))
			return null;

		// get roles
		List<Role> origRoles = user.getRoles();
		Set<String> roles = new HashSet<String>(origRoles.size() + 1);
		for (Role role : origRoles)
		{
			roles.add(role.getName());			
		}
		
		// always give logged-in role
		roles.add("logged-in");

		return new UserPrincipal(username, roles);
	}

	/**
	 * Determines if user belongs to the given role.
	 * 
	 * <p> This method delegates to the underlying {@code UserPrincipal}
	 * implementation. </p>
	 * 
	 * @param principal {@code UserPrincipal} of logged-in user
	 * @param role role name
	 * @return true if user has requested role, false otherwise
	 */
	public boolean isUserInRole(Principal principal, String role)
	{
		if (principal != null && principal instanceof UserPrincipal)
		{
			UserPrincipal up = (UserPrincipal) principal;
			return up.isUserInRole(role);
		}
		return false;
	}
}
