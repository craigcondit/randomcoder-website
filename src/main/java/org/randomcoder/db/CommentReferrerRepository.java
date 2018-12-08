package org.randomcoder.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Comment referrer repository.
 */
@Repository public interface CommentReferrerRepository
    extends JpaRepository<CommentReferrer, Long> {
  /**
   * Finds a given {@code CommentReferrer} by URI.
   *
   * @param uri referrer URI
   * @return {@code CommentReferrer} instance, or null if not found
   */
  @Query("from CommentReferrer cr where cr.referrerUri = ?1")
  public CommentReferrer findByUri(String uri);
}
