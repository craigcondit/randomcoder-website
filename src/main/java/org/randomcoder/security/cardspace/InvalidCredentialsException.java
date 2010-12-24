package org.randomcoder.security.cardspace;

import org.acegisecurity.AuthenticationException;

/**
 * Exception thrown due to invalid / missing CardSpace credentials.
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
public class InvalidCredentialsException extends AuthenticationException
{
	private static final long serialVersionUID = 1729140205772443446L;

	/**
	 * Creates a new exception with the given message.
	 * @param message message
	 */
	public InvalidCredentialsException(String message)
	{
		super(message);
	}

	/**
	 * Creates a new exception with the given message and cause.
	 * @param message message
	 * @param cause cause
	 */
	public InvalidCredentialsException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Gets the underlying cause of this exception.
	 * @return cause of the exception
	 */
	@Override
	public Throwable getCause()
	{
		return super.getCause();
	}
	
	/**
	 * Gets the message associated with this exception.
	 * @return message
	 */
	@Override
	public String getMessage()
	{
		return super.getMessage();
	}
}
