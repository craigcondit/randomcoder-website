package org.randomcoder.db;

import org.randomcoder.dao.*;

/**
 * Comment user agent data access interface.
 */
public interface CommentUserAgentDao extends CreatableDao<CommentUserAgent, Long>, ReadableDao<CommentUserAgent, Long>
{
	/**
	 * Finds a given {@code CommentUserAgent} by name.
	 * 
	 * @param name
	 *          user agent name
	 * @return {@code CommentUserAgent} instance, or null if not found
	 */
	public CommentUserAgent findByName(String name);
}
