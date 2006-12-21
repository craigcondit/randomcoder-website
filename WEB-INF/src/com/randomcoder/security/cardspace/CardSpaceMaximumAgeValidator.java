package com.randomcoder.security.cardspace;

import java.util.Date;

import org.acegisecurity.*;

/**
 * CardSpace credential validator which checks that a token is not issued
 * beyond a given maximum age.
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
public class CardSpaceMaximumAgeValidator
implements CardSpaceCredentialsValidator
{
	private long clockSkewMilliseconds = 0;
	private long maximumTokenAgeMilliseconds;
	
	/**
	 * Sets the amount of clock skew to allow (in seconds).
	 * <p>
	 * By default, no clock skew is allowed.
	 * </p>
	 * @param clockSkew clock skew in seconds
	 * @throws IllegalArgumentException if clock skew is negative
	 */
	public void setClockSkew(int clockSkew)
	throws IllegalArgumentException
	{
		if (clockSkew < 0)
			throw new IllegalArgumentException("Clock skew cannot be negative");
		
		this.clockSkewMilliseconds = 1000L * clockSkew;
	}
	
	/**
	 * Sets the maximimum time in seconds a token may be valid.
	 * @param maximumTokenAge token age in seconds
	 * @throws IllegalArgumentException if maximumTokenAge is less than 1
	 */
	public void setMaximumTokenAge(int maximumTokenAge)
	throws IllegalArgumentException
	{
		if (maximumTokenAge <= 0)
			throw new IllegalArgumentException("Maximum token age must be postive");
		
		this.maximumTokenAgeMilliseconds = 1000L * maximumTokenAge;
	}
	
	/**
	 * Validates the expiration dates on the given credentials using
	 * the issueInstant claim. 
	 * @throws AuthenticationException if credentials are invalid
	 */
	public void validate(CardSpaceCredentials credentials)
	throws AuthenticationException
	{
		Date received = credentials.getReceivedInstant();
		Date issueInstant = credentials.getIssueInstant();
		
		// start date is issueInstant - clock skew
		Date notBefore = new Date(issueInstant.getTime() - clockSkewMilliseconds);
		
		// end date is issueInstant + max age + clock skew
		Date notOnOrAfter = new Date(issueInstant.getTime() + maximumTokenAgeMilliseconds + clockSkewMilliseconds);
		
		// check validity
		if (received.before(notBefore))
			throw new BadCredentialsException("Credentials not valid yet");
		
		if (!received.before(notOnOrAfter))
			throw new BadCredentialsException("Credentials have expired");
	}

}
