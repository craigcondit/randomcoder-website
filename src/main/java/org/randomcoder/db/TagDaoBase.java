package org.randomcoder.db;

import java.util.List;

import org.randomcoder.tag.TagStatistics;

/**
 * Base Tag data access interface.
 */
public interface TagDaoBase
{
	/**
	 * Lists all Tag statistics.
	 * 
	 * @return List of TagStatistics
	 */
	public List<TagStatistics> queryAllTagStatistics();

	/**
	 * Lists all Tag statistics in range.
	 * 
	 * @param start
	 *          starting result
	 * @param limit
	 *          maximum number of results
	 * @return List of TagStatistics
	 */
	public List<TagStatistics> queryAllTagStatisticsInRange(int start, int limit);

	/**
	 * Calculates the maximum number of articles per tag.
	 * 
	 * @return article count
	 */
	public int queryMostArticles();
}
