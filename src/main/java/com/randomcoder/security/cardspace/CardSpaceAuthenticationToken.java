package com.randomcoder.security.cardspace;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.providers.AbstractAuthenticationToken;

/**
 * Authentication implementation for use with CardSpace logins.
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
public class CardSpaceAuthenticationToken extends AbstractAuthenticationToken
{	
	private static final long serialVersionUID = -6303673335510941017L;
	
	private final Object principal;
	private final CardSpaceCredentials credentials;
	
	/**
	 * Creates an un-authenticated token.
	 * @param credentials
	 */
	public CardSpaceAuthenticationToken(CardSpaceCredentials credentials)
	{
		super(null);
		this.principal = null;
		this.credentials = credentials;
	}
	
	/**
	 * Creates an authenticated token.
	 * @param principal principal
	 * @param credentials SAML credentials
	 * @param authorities list of granted authorities
	 */
	public CardSpaceAuthenticationToken(Object principal, CardSpaceCredentials credentials, GrantedAuthority[] authorities)
	{
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
		setAuthenticated(true);
	}
	
	/**
	 * Gets the credentials associated with this token.
	 * @return credentials
	 */
	public Object getCredentials()
	{
		return credentials;
	}

	/**
	 * Gets the principal associated with this token.
	 * @return principal
	 */
	public Object getPrincipal()
	{
		return principal;
	}

}
