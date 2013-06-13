package org.randomcoder.db;

import java.util.*;

import org.randomcoder.dao.CrudDao;

/**
 * Article data access interface.
 */
public interface ArticleDao extends CrudDao<Article, Long>
{
	/**
	 * Loads an {@code Article} by its permalink
	 * 
	 * @param permalink
	 *          permalink name
	 * @return article if found, or null if no match
	 */
	public Article findByPermalink(String permalink);

	/**
	 * Lists {@code Article} objects within the range specified.
	 * 
	 * @param start
	 *          starting result to return, from 0
	 * @param limit
	 *          maximum number of results to return
	 * @return list of {@code Article} objects
	 */
	public List<Article> listAllInRange(int start, int limit);

	/**
	 * Lists {@code Article} objects created before the specified date and within
	 * the range specified.
	 * 
	 * @param endDate
	 *          upper bound of date range (exclusive)
	 * @param start
	 *          starting result to return, from 0
	 * @param limit
	 *          maximum number of results to return
	 * @return list of {@code Article} objects
	 */
	public List<Article> listBeforeDateInRange(Date endDate, int start, int limit);

	/**
	 * Lists {@code Article} objects created before the specified date and within
	 * the range specified.
	 * 
	 * @param tag
	 *          tag to restrict by
	 * @param endDate
	 *          upper bound of date range (exclusive)
	 * @param start
	 *          starting result to return, from 0
	 * @param limit
	 *          maximum number of results to return
	 * @return list of {@code Article} objects
	 */
	public List<Article> listByTagBeforeDateInRange(Tag tag, Date endDate, int start, int limit);

	/**
	 * Lists {@code Article} objects with the given tag.
	 * 
	 * @param tag
	 *          tag
	 * @return list of {@code Article} objects
	 */
	public List<Article> listByTag(Tag tag);

	/**
	 * Iterates {@code Article} objects with the given tag.
	 * 
	 * @param tag
	 *          tag
	 * @return Article iterator
	 */
	public Iterator<Article> iterateByTag(Tag tag);

	/**
	 * Lists {@code Article} objects created within the specified date range.
	 * 
	 * @param startDate
	 *          lower bound of date range (inclusive)
	 * @param endDate
	 *          upper bound of date range (exclusive)
	 * @return list of {@code Article} objects
	 */
	public List<Article> listBetweenDates(Date startDate, Date endDate);

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
	public List<Article> listByTagBetweenDates(Tag tag, Date startDate, Date endDate);

	/**
	 * Counts all {@code Article} objects created before the specified date.
	 * 
	 * @param endDate
	 *          upper bound of date range (exclusive)
	 * @return count of articles
	 */
	public int countBeforeDate(Date endDate);

	/**
	 * Counts all {@code Article} objects created before the specified date.
	 * 
	 * @param tag
	 *          tag to restrict by
	 * @param endDate
	 *          upper bound of date range (exclusive)
	 * @return count of articles
	 */
	public int countByTagBeforeDate(Tag tag, Date endDate);
}