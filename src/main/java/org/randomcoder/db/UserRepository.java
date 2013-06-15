package org.randomcoder.db;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * User repository.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
	/**
	 * Finds a {@code User} with the given user name.
	 * 
	 * @param name
	 *            user name
	 * @return {@code User} instance, or null if not found
	 */
	@Query("from User u where u.userName = ?1")
	public User findByUserName(String name);

	/**
	 * Finds an enabled {@code User} with the given user name.
	 * 
	 * @param name
	 *            user name
	 * @return {@code User} instance, or null if not found or not enabled
	 */
	@Query("from User u where u.userName = ?1 and u.enabled = true")
	public User findByUserNameEnabled(String name);
}