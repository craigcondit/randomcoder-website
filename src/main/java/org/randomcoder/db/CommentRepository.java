package org.randomcoder.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Comment repository.
 */
@Repository
public interface CommentRepository
        extends JpaRepository<Comment, Long> {
    /**
     * Returns a page of comments to be moderated.
     *
     * @param pageable range to retrieve
     * @return Comment Iterator.
     */
    @Query("from Comment c where c.moderationStatus = 'PENDING'")
    Page<Comment> findForModeration(Pageable pageable);
}
