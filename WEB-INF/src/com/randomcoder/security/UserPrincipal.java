package com.randomcoder.security;

import java.io.Serializable;
import java.security.Principal;
import java.util.Set;

import org.apache.commons.lang.builder.*;

/**
 * {@code Principal} implementation.
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
public final class UserPrincipal implements Principal, Serializable
{
	private static final long serialVersionUID = 7563780907316194193L;

	private final String name;
	private final Set<String> roles;

	/**
	 * Creates a principal with the given username and roles.
	 * 
	 * <p> This constructor is purposely package-protected to help prevent its use
	 * by untrusted code. It is recommended that a security manager be used to
	 * enforce this. </p>
	 * 
	 * @param name username
	 * @param roles set of roles
	 */
	UserPrincipal(String name, Set<String> roles)
	{
		this.name = name;
		this.roles = roles;
	}

	/**
	 * Gets the user name.
	 * @return user name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Determines if the principal has the given role
	 * @param role role to check
	 * @return true if principal has role, false otherwise
	 */
	public boolean isUserInRole(String role)
	{
		return roles.contains(role);
	}

	/**
	 * Gets a string representation of this object, suitable for debugging.
	 * @return string representation of this object
	 */
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
