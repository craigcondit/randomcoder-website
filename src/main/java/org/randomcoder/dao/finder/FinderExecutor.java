package org.randomcoder.dao.finder;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Interface to map custom DAO methods to named queries.
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
public interface FinderExecutor<T>
{

	/**
	 * Gets a count of objects
	 * @param method Method to execute
	 * @param args query arguments
	 * @return count of objects
	 */
	int count(Method method, Object[] args);

	/**
	 * Find a single object
	 * @param method Method to execute
	 * @param args query arguments
	 * @return single object
	 */
	T find(Method method, Object[] args);

	/**
	 * Get a list of objects
	 * @param method Method to execute
	 * @param args query arguments
	 * @return list of objects
	 */
	List<T> list(Method method, Object[] args);

	/**
	 * Get a list of objects
	 * @param method Method to execute
	 * @param args query arguments
	 * @param start start result
	 * @param limit maximum result size
	 * @return list of objects
	 */
	List<T> list(Method method, Object[] args, int start, int limit);

	/**
	 * Get an iterator of objects
	 * @param method Method to execute
	 * @param args query arguments
	 * @return list of objects
	 */
	Iterator<T> iterate(Method method, Object[] args);

	/**
	 * Get an iterator of objects
	 * @param method Method to execute
	 * @param args query arguments
	 * @param start start result
	 * @param limit maximum result size
	 * @return list of objects
	 */
	Iterator<T> iterate(Method method, Object[] args, int start, int limit);
}
