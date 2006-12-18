package com.randomcoder.security;

import java.util.*;

import org.acegisecurity.*;
import org.acegisecurity.userdetails.UserDetails;

import com.randomcoder.user.*;

/**
 * Acegi UserDetails implementation.
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
public final class UserDetailsImpl implements UserDetails
{
	private static final long serialVersionUID = 8725581950129430004L;
	
	private final String username;
	private String password;
	private final boolean enabled;
	private final List<GrantedAuthority> authorities;
	
	/**
	 * Creates a new UserDetailsImpl.
	 * @param user User to read properties from.
	 */
	public UserDetailsImpl(User user)
	{
		username = user.getUserName();
		password = user.getPassword();
		enabled = user.isEnabled();
		
		List<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
		for (Role role : user.getRoles())
		{
			auth.add(new GrantedAuthorityImpl(role.getName()));
		}
		authorities = Collections.unmodifiableList(auth);		
	}

	public GrantedAuthority[] getAuthorities()
	{
		GrantedAuthority[] authArray = new GrantedAuthority[authorities.size()];
		return authorities.toArray(authArray);
	}

	public String getPassword()
	{
		return password;
	}

	public String getUsername()
	{
		return username;
	}

	public boolean isAccountNonExpired()
	{
		return true;
	}

	public boolean isAccountNonLocked()
	{
		return true;
	}

	public boolean isCredentialsNonExpired()
	{
		return true;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

}
