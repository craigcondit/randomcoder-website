package org.randomcoder.db;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Comment IP repository.
 */
@Repository
public interface CommentIpRepository extends JpaRepository<CommentIp, Long>
{
	/**
	 * Finds a given {@code CommentIp} by IP address.
	 * 
	 * @param ipAddress
	 *          IP address
	 * @return {@code CommentIp} instance, or null if not found
	 */
	@Query("from CommentIp ci where ci.ipAddress = ?1")
	public CommentIp findByIpAddress(String ipAddress);
}
