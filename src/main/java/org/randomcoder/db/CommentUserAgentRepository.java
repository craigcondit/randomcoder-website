package org.randomcoder.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Comment user agent repository.
 */
@Repository
public interface CommentUserAgentRepository extends JpaRepository<CommentUserAgent, Long> {
	/**
	 * Finds a given {@code CommentUserAgent} by name.
	 * 
	 * @param name
	 *            user agent name
	 * @return {@code CommentUserAgent} instance, or null if not found
	 */
	@Query("from CommentUserAgent cu where cu.userAgentName = ?1")
	public CommentUserAgent findByName(String name);
}
