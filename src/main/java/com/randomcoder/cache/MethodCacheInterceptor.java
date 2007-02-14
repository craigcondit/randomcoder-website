package com.randomcoder.cache;

import java.io.Serializable;

import net.sf.ehcache.*;

import org.aopalliance.intercept.*;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Required;

/**
 * AOP interceptor which caches the results of method calls.
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
public class MethodCacheInterceptor implements MethodInterceptor
{
	private static final Log logger = LogFactory.getLog(MethodCacheInterceptor.class);
	
	private Cache cache;

	/**
	 * Sets the EHCache instance to use.
	 * @param cache cache instance to use
	 */
  @Required
	public void setCache(Cache cache)
	{
		this.cache = cache;
	}

  /**
   * Invokes the given method, caching the results if an exception is not thrown.
   * @param invocation method to invoke
   * @throws Throwable if invoked method throws an exception
   * @return result of the method invocation
   */
	public Object invoke(MethodInvocation invocation) throws Throwable
	{
		String className = invocation.getThis().getClass().getName();
		String methodName = invocation.getMethod().getName();
		Object[] arguments = invocation.getArguments();
		Object result;

		String key = buildCacheKey(className, methodName, arguments);
		
		logger.debug("Invoking: " + key);

		// load from cache
		Element element = cache.get(key);
		if (element == null)
		{
			logger.debug("Proceeding with invocation");
			result = invocation.proceed();
			element = new Element(key, (Serializable) result);
			cache.put(element);
		}
		else
		{
			logger.debug("Using cached value");
		}
		
		return element.getValue();
	}

	private String buildCacheKey(String className, String methodName, Object[] arguments)
	{
		StringBuilder buf = new StringBuilder();
		buf.append(className);
		buf.append("#");
		buf.append(methodName);
		buf.append("(");
		if (arguments != null)
		{
			for (int i = 0; i < arguments.length; i++)
			{
				if (i > 0)
					buf.append(",");
				if (arguments[i] != null)
				{
					buf.append("{");
					buf.append(arguments[i]);
					buf.append("}");
				}
			}
		}
		buf.append(")");

		return buf.toString();
	}
}