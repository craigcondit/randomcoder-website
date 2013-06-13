package org.randomcoder.db;

import org.randomcoder.dao.*;

/**
 * Comment referrer data access interface.
 */
public interface CommentReferrerDao extends CreatableDao<CommentReferrer, Long>, ReadableDao<CommentReferrer, Long>
{
	/**
	 * Finds a given {@code CommentReferrer} by uri.
	 * 
	 * @param uri
	 *          referrer uri
	 * @return {@code CommentReferrer} instance, or null if not found
	 */
	public CommentReferrer findByUri(String uri);
}
