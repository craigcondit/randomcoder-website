package org.randomcoder.db;

import java.util.List;

import org.randomcoder.dao.CrudDao;

/**
 * User data access interface.
 */
public interface UserDao extends CrudDao<User, Long>
{
	/**
	 * Finds a {@code User} with the given user name.
	 * 
	 * @param name
	 *          user name
	 * @return {@code User} instance, or null if not found
	 */
	public User findByUserName(String name);

	/**
	 * Finds an enabled {@code User} with the given user name.
	 * 
	 * @param name
	 *          user name
	 * @return {@code User} instance, or null if not found or not enabled
	 */
	public User findByUserNameEnabled(String name);

	/**
	 * Lists all {@code User} objects, ordered by user name.
	 * 
	 * @return List of {@code User} objects
	 */
	public List<User> listAll();

	/**
	 * Lists all {@code User} objects in range, ordered by user name.
	 * 
	 * @param start
	 *          starting result
	 * @param limit
	 *          maximum number of results
	 * @return List of {@code User} objects
	 */
	public List<User> listAllInRange(int start, int limit);

	/**
	 * Counts all {@code User} objects
	 * 
	 * @return count of user objects
	 */
	public int countAll();

	/**
	 * Lists all enabled {@code User} objects, ordered by user name.
	 * 
	 * @return List of {@code User} objects
	 */
	public List<User> listEnabled();
}
