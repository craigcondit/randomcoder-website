package com.randomcoder.security;

import java.io.IOException;

import javax.servlet.ServletException;

import org.acegisecurity.*;
import org.acegisecurity.intercept.web.FilterInvocation;
import org.acegisecurity.securechannel.ChannelProcessor;

/**
 * Acegi ChannelProcessor implementation which allows any access method.
 * <p>
 * This class allows specifying <code>REQUIRES_ANY</code> in a
 * <code>ChannelProcessingFilter</code> to specify urls which should be
 * accessible from any context.
 * </p> 
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
public class AnyChannelProcessor implements ChannelProcessor
{
	private static final String ATTRIBUTE = "REQUIRES_ANY";
	
	/**
	 * Does nothing.
	 * 
	 * @param invocation not used
	 * @param definition not used
	 */
	public void decide(FilterInvocation invocation, ConfigAttributeDefinition definition)
	throws IOException, ServletException
	{
	}

	/**
	 * Determines if this class supports the given configuration attribute.
	 * @param att attribute to query
	 * @return true if attribute matches <code>REQUIRES_ANY</code>
	 */
	public boolean supports(ConfigAttribute att)	
	{
		return (ATTRIBUTE.equals(att.getAttribute()));
	}

}
