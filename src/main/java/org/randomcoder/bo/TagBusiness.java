package org.randomcoder.bo;

import java.util.List;

import org.randomcoder.io.*;
import org.randomcoder.tag.*;

/**
 * Tag management interface.
 */
public interface TagBusiness
{
	/**
	 * Gets a list of TagCloudEntry objects to produce a tag cloud.
	 * 
	 * @return list of TagCloudEntry objects sorted by display name.
	 */
	public List<TagCloudEntry> getTagCloud();

	/**
	 * Create a new tag.
	 * 
	 * @param producer
	 *          tag producer
	 */
	public void createTag(Producer<Tag> producer);

	/**
	 * Loads a tag for editing.
	 * 
	 * @param consumer
	 *          consumer
	 * @param tagId
	 *          id of tag to load
	 */
	public void loadTagForEditing(Consumer<Tag> consumer, Long tagId);

	/**
	 * Update an existing tag.
	 * 
	 * @param producer
	 *          tag producer
	 * @param tagId
	 *          tag id
	 */
	public void updateTag(Producer<Tag> producer, Long tagId);

	/**
	 * Deletes the tag with the given id.
	 * 
	 * @param tagId
	 *          tag id
	 */
	public void deleteTag(Long tagId);

	/**
	 * Finds a given {@code Tag} by name.
	 * 
	 * @param name
	 *          tag name
	 * @return {@code Tag} instance, or null if not found
	 */
	public Tag findTagByName(String name);

	/**
	 * Lists all Tag statistics.
	 * 
	 * @return List of TagStatistics
	 */
	public List<TagStatistics> queryTagStatistics();

	/**
	 * Lists all Tag statistics in range.
	 * 
	 * @param start
	 *          starting result
	 * @param limit
	 *          maximum number of results
	 * @return List of TagStatistics
	 */
	public List<TagStatistics> queryTagStatisticsInRange(int start, int limit);

	/**
	 * Calculates the maximum number of articles per tag.
	 * 
	 * @return article count
	 */
	public int queryTagMostArticles();

	/**
	 * Counts all tags.
	 * 
	 * @return count of tags
	 */
	public int countTags();
}
