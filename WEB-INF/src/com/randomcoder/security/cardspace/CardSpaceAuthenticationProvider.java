package com.randomcoder.security.cardspace;

import org.acegisecurity.*;
import org.acegisecurity.providers.*;
import org.acegisecurity.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Required;

/**
 * AuthenticationProvider which validates
 * <code>CardSpaceAuthenticationToken</code> tokens.
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
public class CardSpaceAuthenticationProvider implements AuthenticationProvider
{
	private CardSpaceUserDetailsService cardSpaceUserDetailsService;
	
	@Required
	public void setCardSpaceUserDetailsService(CardSpaceUserDetailsService cardSpaceUserDetailsService)
	{
		this.cardSpaceUserDetailsService = cardSpaceUserDetailsService;
	}

	public Authentication authenticate(Authentication auth) throws AuthenticationException
	{
		if (auth == null) return null;
		
		if (auth instanceof CardSpaceAuthenticationToken)
			return authenticateCardSpaceAuthenticationToken((CardSpaceAuthenticationToken) auth);
		
		if (auth instanceof UsernamePasswordAuthenticationToken)
			return authenticateUsernamePasswordAuthenticationToken((UsernamePasswordAuthenticationToken) auth);
		
			return null;		
	}

	private Authentication authenticateCardSpaceAuthenticationToken(CardSpaceAuthenticationToken token) throws AuthenticationException
	{
		CardSpaceCredentials credentials = (CardSpaceCredentials) token.getCredentials();
		
		// TODO check that credentials are valid within time period
		
		UserDetails userDetails = cardSpaceUserDetailsService.loadUserByCardSpaceCredentials(credentials);
		if (userDetails == null) return null;
		
		return new CardSpaceAuthenticationToken(userDetails, credentials, userDetails.getAuthorities());
	}

	private Authentication authenticateUsernamePasswordAuthenticationToken(UsernamePasswordAuthenticationToken token) throws AuthenticationException
	{
		Object cred = token.getCredentials();
		if (!(cred instanceof CardSpaceCredentials)) return null;
		CardSpaceCredentials credentials = (CardSpaceCredentials) cred;
		
		// TODO check that credentials are valid within time period
		
		UserDetails userDetails = cardSpaceUserDetailsService.loadUserByCardSpaceCredentials(credentials);
		if (userDetails == null) return null;
		
		return new UsernamePasswordAuthenticationToken(userDetails, credentials, userDetails.getAuthorities());
	}
	
	public boolean supports(Class target)
	{
		if (target.equals(CardSpaceAuthenticationToken.class)) return true;
		if (target.equals(UsernamePasswordAuthenticationToken.class)) return true;
		return false;
	}

}
