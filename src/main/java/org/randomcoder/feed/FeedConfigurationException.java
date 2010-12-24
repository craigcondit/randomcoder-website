package org.randomcoder.feed;

/**
 * Feed exception thrown when configuration is invalid.
 * 
 * <pre>
 * Copyright (c) 2007, Craig Condit. All rights reserved.
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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS &quot;AS IS&quot;
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
public class FeedConfigurationException extends FeedException
{
	private static final long serialVersionUID = 7305739829048095127L;

	/**
	 * Creates a new exception.
	 */
	public FeedConfigurationException()
	{
		super();
	}

	/**
	 * Creates a new exception with the given message.
	 * 
	 * @param message
	 *          error message
	 */
	public FeedConfigurationException(String message)
	{
		super(message);
	}

	/**
	 * Creates a new exception with the given cause.
	 * 
	 * @param cause
	 *          root cause
	 */
	public FeedConfigurationException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Creates a new exception with the given message and cause.
	 * 
	 * @param message
	 *          error message
	 * @param cause
	 *          root cause
	 */
	public FeedConfigurationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
