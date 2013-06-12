package org.randomcoder.db;

import java.util.List;

import org.randomcoder.dao.CrudDao;
import org.randomcoder.tag.Tag;

/**
 * Tag data access interface.
 */
public interface TagDao extends CrudDao<Tag, Long>, TagDaoBase
{
	/**
	 * Finds a given {@code Tag} by name.
	 * 
	 * @param name
	 *          tag name
	 * @return {@code Tag} instance, or null if not found
	 */
	public Tag findByName(String name);

	/**
	 * Lists all {@code Tag} objects, sorted by displayName.
	 * 
	 * @return List of {@code Tag} objects
	 */
	public List<Tag> listAll();

	/**
	 * Lists all {@code Tag} objects in range, sorted by displayName.
	 * 
	 * @param start
	 *          starting result
	 * @param limit
	 *          maximum number of results
	 * @return List of {@code Tag} objects
	 */
	public List<Tag> listAllInRange(int start, int limit);

	/**
	 * Counts all tags.
	 * 
	 * @return count of tags
	 */
	public int countAll();
}
