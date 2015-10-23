package org.randomcoder.tag;

import java.io.Serializable;

import org.randomcoder.db.Tag;

/**
 * Statisitics for Tag instances (article count, etc).
 */
public class TagStatistics implements Serializable
{
	private static final long serialVersionUID = 7563892982366083919L;

	private Tag tag;
	private int articleCount;

	/**
	 * Default constructor.
	 */
	public TagStatistics()
	{}

	/**
	 * Creates a new TagStatistics object with the given tag and article count.
	 * 
	 * @param tag
	 *          tag
	 * @param articleCount
	 *          article count
	 */
	public TagStatistics(Tag tag, int articleCount)
	{
		this.tag = tag;
		this.articleCount = articleCount;
	}

	/**
	 * Gets the tag to which statistics apply.
	 * 
	 * @return Tag instance
	 */
	public Tag getTag()
	{
		return tag;
	}

	/**
	 * Sets the tag to which statistics apply.
	 * 
	 * @param tag
	 *          Tag instance
	 */
	public void setTag(Tag tag)
	{
		this.tag = tag;
	}

	/**
	 * Gets the number of articles which this tag contains.
	 * 
	 * @return article count
	 */
	public int getArticleCount()
	{
		return articleCount;
	}

	/**
	 * Sets the number of articles which this tag contains.
	 * 
	 * @param articleCount
	 *          article count
	 */
	public void setArticleCount(int articleCount)
	{
		this.articleCount = articleCount;
	}

}
