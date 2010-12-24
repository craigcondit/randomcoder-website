package com.randomcoder.security.cardspace;

import java.util.Date;

import org.acegisecurity.*;

/**
 * CardSpace credential validator which checks notBefore and notOnOrAfter
 * constraints.
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
public class CardSpaceDateConstraintValidator
implements CardSpaceCredentialsValidator
{
	private long clockSkewMilliseconds = 0;
	
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
	 * Validates the expiration dates on the given credentials using
	 * the notBefore and notOnOrAfter claims. 
	 * @throws AuthenticationException if credentials are invalid
	 */
	@Override
	public void validate(CardSpaceCredentials credentials)
	throws AuthenticationException
	{
		Date received = credentials.getReceivedInstant();
		Date notBefore = credentials.getNotBefore();
		Date notOnOrAfter = credentials.getNotOnOrAfter();
		
		// check that notBefore is before notOnOrAfter
		if (notBefore.after(notOnOrAfter))
			throw new BadCredentialsException("NotBefore is not before NotOnOrAfter");
		
		// alter notBefore constraint by subtracting clock skew
		notBefore = new Date(notBefore.getTime() - clockSkewMilliseconds);
		
		// alter notOnOrAfter constraint by adding clock skew
		notOnOrAfter = new Date(notOnOrAfter.getTime() + clockSkewMilliseconds);
		
		// check validity
		if (received.before(notBefore))
			throw new BadCredentialsException("Credentials not valid yet");
		
		if (!received.before(notOnOrAfter))
			throw new BadCredentialsException("Credentials have expired");
	}

}
