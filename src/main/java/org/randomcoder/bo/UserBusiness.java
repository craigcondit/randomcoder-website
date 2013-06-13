package org.randomcoder.bo;

import java.util.List;

import org.randomcoder.db.*;
import org.randomcoder.io.*;

/**
 * Business interface for user management.
 */
public interface UserBusiness
{
	/**
	 * Change a user's password.
	 * 
	 * @param userName
	 *          user name
	 * @param password
	 *          new password
	 */
	public void changePassword(String userName, String password);

	/**
	 * Create a new user.
	 * 
	 * @param producer
	 *          user producer
	 */
	public void createUser(Producer<User> producer);

	/**
	 * Creates a new account using a password.
	 * 
	 * @param producer
	 *          user producer
	 */
	public void createAccount(Producer<User> producer);

	/**
	 * Loads a user for editing.
	 * 
	 * @param consumer
	 *          consumer
	 * @param userId
	 *          id of user to load
	 */
	public void loadUserForEditing(Consumer<User> consumer, Long userId);

	/**
	 * Update an existing user.
	 * 
	 * @param producer
	 *          user producer
	 * @param userId
	 *          user id
	 */
	public void updateUser(Producer<User> producer, Long userId);

	/**
	 * Deletes a user.
	 * 
	 * @param userId
	 *          user id to delete
	 */
	public void deleteUser(Long userId);

	/**
	 * Marks a user as having logged in as of a particular date and time.
	 * 
	 * @param userName
	 *          user name to update
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
	 *          role name
	 * @return {@code Role} instance, or null if not found
	 */
	public Role findRoleByName(String name);

	/**
	 * Finds a {@code User} with the given user name.
	 * 
	 * @param name
	 *          user name
	 * @return {@code User} instance, or null if not found
	 */
	public User findUserByName(String name);

	/**
	 * Finds an enabled {@code User} with the given user name.
	 * 
	 * @param name
	 *          user name
	 * @return {@code User} instance, or null if not found or not enabled
	 */
	public User findUserByNameEnabled(String name);

	/**
	 * Lists all {@code User} objects in range, ordered by user name.
	 * 
	 * @param start
	 *          starting result
	 * @param limit
	 *          maximum number of results
	 * @return List of {@code User} objects
	 */
	public List<User> listUsersInRange(int start, int limit);

	/**
	 * Counts all {@code User} objects
	 * 
	 * @return count of user objects
	 */
	public int countUsers();
}