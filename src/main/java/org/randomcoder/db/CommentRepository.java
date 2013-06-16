package org.randomcoder.db;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Comment repository.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>
{
	/**
	 * Returns a page of comments to be moderated.
	 * 
	 * @param pageable
	 *          range to retrieve
	 * @return Comment Iterator.
	 */
	@Query("from Comment c where c.moderationStatus = 'PENDING'")
	public Page<Comment> findForModeration(Pageable pageable);
}
