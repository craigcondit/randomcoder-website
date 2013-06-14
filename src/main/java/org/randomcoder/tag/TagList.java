package org.randomcoder.tag;

import java.io.Serializable;
import java.util.List;

import org.randomcoder.db.Tag;

/**
 * Container for a list of tags.
 */
public class TagList implements Serializable
{
	private static final long serialVersionUID = 8246304617489842857L;

	private List<Tag> tags;

	/**
	 * Default constructor.
	 */
	public TagList()
	{}

	/**
	 * Creates a new TagList populated with the given tag list.
	 * 
	 * @param tags
	 *          tag list
	 */
	public TagList(List<Tag> tags)
	{
		this.tags = tags;
	}

	/**
	 * Getter for tags property.
	 * 
	 * @return list of tags
	 */
	public List<Tag> getTags()
	{
		return tags;
	}

	/**
	 * Setter for tags property.
	 * 
	 * @param tags
	 *          list of tags
	 */
	public void setTags(List<Tag> tags)
	{
		this.tags = tags;
	}
}
