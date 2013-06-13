package org.randomcoder.db;

import org.randomcoder.dao.*;

/**
 * Comment IP data access interface.
 */
public interface CommentIpDao extends CreatableDao<CommentIp, Long>, ReadableDao<CommentIp, Long>
{
	/**
	 * Finds a given {@code CommentIp} by IP address.
	 * 
	 * @param ipAddress
	 *          IP address
	 * @return {@code CommentIp} instance, or null if not found
	 */
	public CommentIp findByIpAddress(String ipAddress);
}
