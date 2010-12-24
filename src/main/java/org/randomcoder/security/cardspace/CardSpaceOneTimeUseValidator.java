package org.randomcoder.security.cardspace;

import java.util.Date;

import org.acegisecurity.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

/**
 * CardSpace credential validator which only allows tokens to be used once.
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
public class CardSpaceOneTimeUseValidator implements CardSpaceCredentialsValidator
{	
	private CardSpaceSeenTokenDao cardSpaceSeenTokenDao;
	
	/**
	 * Sets the CardSpaceSeenTokenDao implementation to use.
	 * @param cardSpaceSeenTokenDao CardSpaceSeenTokenDao implementation
	 */
	@Required
	public void setCardSpaceSeenTokenDao(CardSpaceSeenTokenDao cardSpaceSeenTokenDao)
	{
		this.cardSpaceSeenTokenDao = cardSpaceSeenTokenDao;
	}
	
	/**
	 * Validates the given credentials by insuring that a given assertion
	 * is only presented once.
	 * @throws AuthenticationException if credentials are invalid
	 */
	@Override
	@Transactional(noRollbackFor={AuthenticationException.class, BadCredentialsException.class})
	public void validate(CardSpaceCredentials credentials)
	throws AuthenticationException
	{
		String assertionId = credentials.getAssertionId();		
		String ppid = credentials.getPrivatePersonalIdentifier();
		String issuerHash = DigestUtils.shaHex(credentials.getIssuerPublicKey());
		
		CardSpaceSeenToken token = cardSpaceSeenTokenDao.findByKey(assertionId, ppid, issuerHash);
		if (token != null)
			throw new BadCredentialsException("Token has been used already");
		
		token = new CardSpaceSeenToken();
		token.setAssertionId(assertionId);
		token.setPrivatePersonalIdentifier(ppid);
		token.setIssuerHash(issuerHash);
		token.setCreationDate(new Date());
		
		cardSpaceSeenTokenDao.create(token);
	}

}
