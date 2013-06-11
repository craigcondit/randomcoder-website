package org.randomcoder.bo;

import java.util.List;

import org.randomcoder.io.*;
import org.randomcoder.user.*;

/**
 * Business interface for user management.
 * 
 * <pre>
 * Copyright (c) 2006-2007, Craig Condit. All rights reserved.
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
public interface UserBusiness
{
	/**
	 * Change a user's password.
	 * 
	 * @param userName
	 *            user name
	 * @param password
	 *            new password
	 */
	public void changePassword(String userName, String password);

	/**
	 * Create a new user.
	 * 
	 * @param producer
	 *            user producer
	 */
	public void createUser(Producer<User> producer);

	/**
	 * Creates a new account using a password.
	 * 
	 * @param producer
	 *            user producer
	 */
	public void createAccount(Producer<User> producer);

	/**
	 * Loads a user for editing.
	 * 
	 * @param consumer
	 *            consumer
	 * @param userId
	 *            id of user to load
	 */
	public void loadUserForEditing(Consumer<User> consumer, Long userId);

	/**
	 * Update an existing user.
	 * 
	 * @param producer
	 *            user producer
	 * @param userId
	 *            user id
	 */
	public void updateUser(Producer<User> producer, Long userId);

	/**
	 * Deletes a user.
	 * 
	 * @param userId
	 *            user id to delete
	 */
	public void deleteUser(Long userId);

	/**
	 * Marks a user as having logged in as of a particular date and time.
	 * 
	 * @param userName
	 *            user name to update
	 */
	public void auditUsernamePasswordLogin(String userName);

	/**
	 * Lists all roles, ordered by name.
	 * 
	 * @return List of {@code Role} objects
	 */
	public List<Role> listRoles();

	/**
	 * Finds a {@code Role} instance with the given name.
	 * 
	 * @param name
	 *            role name
	 * @return {@code Role} instance, or null if not found
	 */
	public Role findRoleByName(String name);

	/**
	 * Finds a {@code User} with the given user name.
	 * 
	 * @param name
	 *            user name
	 * @return {@code User} instance, or null if not found
	 */
	public User findUserByName(String name);

	/**
	 * Finds an enabled {@code User} with the given user name.
	 * 
	 * @param name
	 *            user name
	 * @return {@code User} instance, or null if not found or not enabled
	 */
	public User findUserByNameEnabled(String name);

	/**
	 * Lists all {@code User} objects in range, ordered by user name.
	 * @param start starting result
	 * @param limit maximum number of results
	 * @return List of {@code User} objects
	 */
	public List<User> listUsersInRange(int start, int limit);
	
	/**
	 * Counts all {@code User} objects
	 * @return count of user objects
	 */
	public int countUsers();	
}