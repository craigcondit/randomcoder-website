package org.randomcoder.download;

/**
 * Exception thrown if errors occur during package list generation. 
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
public class PackageListException extends Exception
{
	private static final long serialVersionUID = 4298370367619312048L;

	/**
	 * Creates a new exception.
	 */
	public PackageListException()
	{
		super();
	}

	/**
	 * Creates an exception with the given message.
	 * @param message message to associate with this exception
	 */
	public PackageListException(String message)
	{
		super(message);
	}

	/**
	 * Creates an exception with the given cause.
	 * @param cause root cause of this exception
	 */
	public PackageListException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Creates an exception with the given message and cause.
	 * @param message message to associate with this exception
	 * @param cause root cause of this exception
	 */
	public PackageListException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
