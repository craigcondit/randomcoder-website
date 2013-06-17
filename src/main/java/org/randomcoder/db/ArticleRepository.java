package org.randomcoder.db;

import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Article repository.
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>
{
	/**
	 * Loads an {@code Article} by its permalink
	 * 
	 * @param permalink
	 *          permalink name
	 * @return article if found, or null if no match
	 */
	@Query("from Article a where a.permalink = ?1")
	public Article findByPermalink(String permalink);

	/**
	 * Lists {@code Article} objects created before the specified date and within
	 * the range specified.
	 * 
	 * @param endDate
	 *          upper bound of date range (exclusive)
	 * @param pageable
	 *          paging parameters
	 * @return page of {@code Article} objects
	 */
	@Query("from Article a where a.creationDate < ?1")
	public Page<Article> findBeforeDate(Date endDate, Pageable pageable);

	/**
	 * Lists {@code Article} objects created before the specified date and within
	 * the range specified.
	 * 
	 * @param tag
	 *          tag to restrict by
	 * @param endDate
	 *          upper bound of date range (exclusive)
	 * @param pageable
	 *          paging parameters
	 * @return page of {@code Article} objects
	 */
	@Query("from Article a where ?1 in elements(a.tags) and a.creationDate < ?2")
	public Page<Article> findByTagBeforeDate(Tag tag, Date endDate, Pageable pageable);

	/**
	 * Lists {@code Article} objects with the given tag.
	 * 
	 * @param tag
	 *          tag
	 * @return list of {@code Article} objects
	 */
	@Query("from Article a where ?1 in elements(a.tags) order by a.creationDate desc")
	public List<Article> findByTag(Tag tag);

	/**
	 * Iterates {@code Article} objects with the given tag.
	 * 
	 * @param tag
	 *          tag
	 * @return Article iterator
	 */
	@Query("from Article a where ?1 in elements(a.tags) order by a.creationDate desc")
	public Iterable<Article> iterateByTag(Tag tag);

	/**
	 * Lists {@code Article} objects created within the specified date range.
	 * 
	 * @param startDate
	 *          lower bound of date range (inclusive)
	 * @param endDate
	 *          upper bound of date range (exclusive)
	 * @return list of {@code Article} objects
	 */
	@Query("from Article a where a.creationDate >= ?1 and a.creationDate < ?2 order by a.creationDate desc")
	public List<Article> findBetweenDates(Date startDate, Date endDate);

	/**
	 * Lists {@code Article} objects created within the specified date range.
	 * 
	 * @param tag
	 *          tag to restrict by
	 * @param startDate
	 *          lower bound of date range (inclusive)
	 * @param endDate
	 *          upper bound of date range (exclusive)
	 * @return list of {@code Article} objects
	 */
	@Query("from Article a where ?1 in elements(a.tags) and a.creationDate >= ?2 and a.creationDate < ?3 order by a.creationDate desc")
	public List<Article> findByTagBetweenDates(Tag tag, Date startDate, Date endDate);
}