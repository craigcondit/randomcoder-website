package com.randomcoder.dao.finder;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.IntroductionInterceptor;

/**
 * Advisor used to supplement generic dao with type-specific query interfaces.
 * 
 * <p>For any method beginning with "find", "list", "iterate", this interceptor
 * will use the FinderExecutor to call a Hibernate named query.</p>
 * 
 * <p>Any method ending with InRange will be executed using a paged result set.</p>
 * 
 * <p>Inspired by Per Mellqvist's IBM developerWorks article, <a
 * href="http://www-128.ibm.com/developerworks/java/library/j-genericdao.html">Don't
 * repeat the DAO!</a>.</p>
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
public class FinderIntroductionInterceptor implements IntroductionInterceptor
{

	/**
	 * @see IntroductionInterceptor#invoke(MethodInvocation)
	 */
	public Object invoke(MethodInvocation methodInvocation) throws Throwable
	{

		FinderExecutor executor = (FinderExecutor) methodInvocation.getThis();

		String methodName = methodInvocation.getMethod().getName();
		if (methodName.startsWith("find"))
			return handleFind(methodInvocation, executor);

		if (methodName.startsWith("list"))
			return handleList(methodInvocation, executor, methodName);

		if (methodName.startsWith("iterate"))
			return handleIterate(methodInvocation, executor, methodName);

		if (methodName.startsWith("count"))
			return handleCount(methodInvocation, executor);

		// not a dao method
		return methodInvocation.proceed();
	}

	/**
	 * @see IntroductionInterceptor#implementsInterface(Class)
	 */
	public boolean implementsInterface(Class intf)
	{
		return intf.isInterface() && FinderExecutor.class.isAssignableFrom(intf);
	}

	private Object handleCount(MethodInvocation methodInvocation, FinderExecutor executor)
	{
		return executor.count(methodInvocation.getMethod(), methodInvocation.getArguments());
	}

	private Object handleIterate(MethodInvocation methodInvocation, FinderExecutor executor, String methodName)
	{
		Object[] arguments = methodInvocation.getArguments();

		if (methodName.endsWith("InRange"))
		{
			Object[] args = extractRangedArguments(arguments);
			int start = extractRangeStart(arguments);
			int limit = extractRangeLimit(arguments);
			return executor.iterate(methodInvocation.getMethod(), args, start, limit);
		}

		return executor.iterate(methodInvocation.getMethod(), arguments);
	}

	private Object handleList(MethodInvocation methodInvocation, FinderExecutor executor, String methodName)
	{
		Object[] arguments = methodInvocation.getArguments();

		if (methodName.endsWith("InRange"))
		{
			Object[] args = extractRangedArguments(arguments);
			int start = extractRangeStart(arguments);
			int limit = extractRangeLimit(arguments);
			return executor.list(methodInvocation.getMethod(), args, start, limit);
		}

		return executor.list(methodInvocation.getMethod(), arguments);
	}

	private Object handleFind(MethodInvocation methodInvocation, FinderExecutor executor)
	{
		return executor.find(methodInvocation.getMethod(), methodInvocation.getArguments());
	}

	private Object[] extractRangedArguments(Object[] arguments)
	{
		if (arguments.length < 2)
			return new Object[] {};

		// remove last two arguments
		Object[] result = new Object[arguments.length - 2];
		for (int i = 0; i < arguments.length - 2; i++)
		{
			result[i] = arguments[i];
		}

		return result;
	}

	private int extractRangeStart(Object[] arguments)
	{
		if (arguments.length < 2)
			return 0;

		// range start is second-to-last
		Object start = arguments[arguments.length - 2];
		if (start == null || !(start instanceof Integer))
			return 0;

		return (Integer) start;
	}

	private int extractRangeLimit(Object[] arguments)
	{
		if (arguments.length < 2)
			return 0;

		// range limit is last
		Object limit = arguments[arguments.length - 1];
		if (limit == null || !(limit instanceof Integer))
			return 0;

		return (Integer) limit;
	}

}
