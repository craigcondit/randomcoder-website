package com.randomcoder.saml;

/**
 * Exception thrown when SAML parsing fails.
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
public class SamlException extends Exception
{
	private static final long serialVersionUID = -4412031167646924762L;

	/**
	 * Default constructor.
	 */
	public SamlException()
	{
		super();
	}

	/**
	 * Creates a new exception with the given message and cause.
	 * @param message message
	 * @param cause cause
	 */
	public SamlException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Creates a new exception with the given message
	 * @param message message
	 */
	public SamlException(String message)
	{
		super(message);
	}

	/**
	 * Creates a new exception with the given cause
	 * @param cause cause
	 */
	public SamlException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Gets the message from this exception.
	 * @return message
	 */
	@Override
	public String getMessage()
	{
		return super.getMessage();
	}

	/**
	 * Gets the cause of this exception
	 * @return cause
	 */
	@Override
	public Throwable getCause()
	{
		return super.getCause();
	}
}
