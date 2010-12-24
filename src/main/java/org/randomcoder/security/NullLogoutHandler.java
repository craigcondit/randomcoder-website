package com.randomcoder.security;

import javax.servlet.http.*;

import org.acegisecurity.*;
import org.acegisecurity.ui.logout.LogoutHandler;
import org.springframework.beans.factory.annotation.Required;

/**
 * LogoutHandler which forces an Authentication object to be present when
 * none is specified.
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
public class NullLogoutHandler implements LogoutHandler
{
	private LogoutHandler logoutHandler;
	private String username = "anonymous";
	
	/**
	 * Sets the LogoutHandler to wrap.
	 * @param logoutHandler logout handler
	 */
	@Required
	public void setLogoutHandler(LogoutHandler logoutHandler)
	{
		this.logoutHandler = logoutHandler;
	}
	
	/**
	 * Sets the username to assign to the temporary authentication token.
	 * <p>Defaults to <strong>anonymous</strong>.</p>
	 * @param username user name
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	/**
	 * Logs out the current user.
	 * <p>
	 * If a null authentication object is provided, an anonymous token will be
	 * supplied to the underlying logout handler.
	 * </p>
	 * @param request http request
	 * @param response http response
	 * @param authentication auth token
	 */
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
	{
		if (authentication == null) authentication = new NullAuthentication(username);		
		logoutHandler.logout(request, response, authentication);
	}
	
	private static class NullAuthentication implements Authentication
	{
		private static final long serialVersionUID = -6144292174511841584L;
		
		private final String username;
		
		public NullAuthentication(String username) {
			this.username = username;
		}
		
		@Override
		public GrantedAuthority[] getAuthorities()
		{
			return new GrantedAuthority[] {};
		}

		@Override
		public Object getCredentials()
		{
			return null;
		}

		@Override
		public Object getDetails()
		{
			return null;
		}

		@Override
		public Object getPrincipal()
		{
			return username;
		}

		@Override
		public boolean isAuthenticated()
		{
			return false;
		}

		@Override
		public void setAuthenticated(boolean authenticated) throws IllegalArgumentException
		{
			if (authenticated)
				throw new IllegalArgumentException("Cannot set authenticated to true");
		}

		@Override
		public String getName()
		{
			return username;
		}
		
	}
}
