package com.randomcoder.dao.finder;

import java.lang.reflect.Method;

/**
 * Gets query names based on type and method called.
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
public class DefaultFinderNamingStrategy implements FinderNamingStrategy
{

	/**
	 * Gets the query to execute based on the method name called.
	 * 
	 * <p> This method handles query mapping based on the following rules: <ol>
	 * <li>"find", "list", and "iterate" prefixes are stripped</li> <li>"count"
	 * prefix is replaced with "Count"</li> <li>"InRange" suffix is stripped</li>
	 * <li>The resulting method name is prepended with the simple name of the
	 * target type and a period</li> </ol> </p>
	 * 
	 * <p> For example:<br /><br /> If findTargetType is {@code User} and
	 * finderMethod is {@code "findAllInRange"}, the resulting query method would
	 * be <strong>User.All</strong>. </p>
	 */
	@Override
	public String queryNameFromMethod(Class findTargetType, Method finderMethod)
	{
		String methodName = finderMethod.getName();
		String methodPart = methodName;
		boolean inRangeAllowed = false;

		if (methodName.startsWith("find"))
		{
			methodPart = methodName.substring("find".length());
			inRangeAllowed = false;
		}
		else if (methodName.startsWith("list"))
		{
			methodPart = methodName.substring("list".length());
			inRangeAllowed = true;
		}
		else if (methodName.startsWith("iterate"))
		{
			methodPart = methodName.substring("iterate".length());
			inRangeAllowed = true;
		}
		else if (methodName.startsWith("count"))
		{
			methodPart = "Count" + methodName.substring("count".length());
			inRangeAllowed = false;
		}

		// extract InRange
		if (inRangeAllowed && methodPart.endsWith("InRange"))
			methodPart = methodPart.substring(0, methodPart.length() - "InRange".length());

		return findTargetType.getSimpleName() + "." + methodPart;
	}

}
