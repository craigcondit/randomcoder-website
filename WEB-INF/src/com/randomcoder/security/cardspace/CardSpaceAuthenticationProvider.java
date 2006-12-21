package com.randomcoder.security.cardspace;

import java.util.*;

import org.acegisecurity.*;
import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.userdetails.UserDetails;
import org.springframework.beans.factory.InitializingBean;
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
public class CardSpaceAuthenticationProvider
implements AuthenticationProvider, InitializingBean
{
	private CardSpaceUserDetailsService cardSpaceUserDetailsService;
	
	private List<CardSpaceCredentialsValidator> validators
		= new ArrayList<CardSpaceCredentialsValidator>();
	
	/**
	 * Sets the CardSpaceUserDetailsService implementation to use. 
	 * @param cardSpaceUserDetailsService user details service
	 */
	@Required
	public void setCardSpaceUserDetailsService(CardSpaceUserDetailsService cardSpaceUserDetailsService)
	{
		this.cardSpaceUserDetailsService = cardSpaceUserDetailsService;
	}
	
	/**
	 * Sets a list of CardSpaceCredentialsValidator objects to query.
	 * @param validators list of validators
	 */
	public void setValidators(List<CardSpaceCredentialsValidator> validators)
	{
		this.validators = validators;
	}
	
	/**
	 * Sets a single CardSpaceCredentialsValidator.
	 * @param validator validator
	 */
	public void setValidator(CardSpaceCredentialsValidator validator)
	{
		this.validators = new ArrayList<CardSpaceCredentialsValidator>();
		validators.add(validator);
	}
	
	/**
	 * Fired after all properties have been set.
	 * @throws IllegalArgumentException if properties are not valid
	 */
	public void afterPropertiesSet() throws IllegalArgumentException
	{
		if (validators == null || validators.size() == 0)
			throw new IllegalArgumentException("At least one validator must be specified");
	}

	/**
	 * Attempts to authenticate a CardSpace authentication token.
	 * <p>
	 * This class handles <code>CardSpaceAuthenticationToken</code> objects.
	 * </p>
	 * @param auth authentication token
	 * @throws AuthenticationException if authentication fails
	 * @return fully authenticated CardSpaceAuthenticationToken
	 */
	public Authentication authenticate(Authentication auth) throws AuthenticationException
	{
		if (auth == null) return null;
		if (!(auth instanceof CardSpaceAuthenticationToken)) return null;
		
		CardSpaceCredentials credentials = (CardSpaceCredentials) ((CardSpaceAuthenticationToken) auth).getCredentials();

		for (CardSpaceCredentialsValidator validator : validators)
			validator.validate(credentials);
		
		UserDetails userDetails = cardSpaceUserDetailsService.loadUserByCardSpaceCredentials(credentials);
		if (userDetails == null) return null;
		
		return new CardSpaceAuthenticationToken(userDetails, credentials, userDetails.getAuthorities());
	}

	/**
	 * This class supports <code>CardSpaceAuthenticationToken</code> objects.
	 * @param target target class to query
	 * @return true if target is <code>CardSpaceAuthenticationToken</code>,
	 *         false otherwise.
	 */
	public boolean supports(Class target)
	{
		if (target.equals(CardSpaceAuthenticationToken.class)) return true;
		return false;
	}
}
