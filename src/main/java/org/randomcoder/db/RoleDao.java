package org.randomcoder.db;

import java.util.List;

import org.randomcoder.dao.ReadableDao;

/**
 * Role data access interface.
 */
public interface RoleDao extends ReadableDao<Role, Long>
{
	/**
	 * Finds a {@code Role} instance with the given name.
	 * 
	 * @param name
	 *          role name
	 * @return {@code Role} instance, or null if not found
	 */
	public Role findByName(String name);

	/**
	 * Lists all roles, ordered by name.
	 * 
	 * @return List of {@code Role} objects
	 */
	public List<Role> listAll();
}