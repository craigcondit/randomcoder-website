package org.randomcoder.db;

import java.util.List;

import org.randomcoder.dao.CrudDao;
import org.randomcoder.user.User;

/**
 * User data access interface.
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
public interface UserDao extends CrudDao<User, Long>
{

	/**
	 * Finds a {@code User} with the given user name.
	 * @param name user name
	 * @return {@code User} instance, or null if not found
	 */
	public User findByUserName(String name);

	/**
	 * Finds an enabled {@code User} with the given user name.
	 * @param name user name
	 * @return {@code User} instance, or null if not found or not enabled
	 */
	public User findByUserNameEnabled(String name);

	/**
	 * Lists all {@code User} objects, ordered by user name.
	 * @return List of {@code User} objects
	 */
	public List<User> listAll();
	
	/**
	 * Lists all {@code User} objects in range, ordered by user name.
	 * @param start starting result
	 * @param limit maximum number of results
	 * @return List of {@code User} objects
	 */
	public List<User> listAllInRange(int start, int limit);
	
	
	/**
	 * Counts all {@code User} objects
	 * @return count of user objects
	 */
	public int countAll();	

	/**
	 * Lists all enabled {@code User} objects, ordered by user name.
	 * @return List of {@code User} objects
	 */
	public List<User> listEnabled();
}
