package org.randomcoder.article;

import org.randomcoder.db.Tag;

/**
 * Command object used for paged tag queries.
 */
public class ArticleTagPageCommand extends ArticlePageCommand
{
	private static final long serialVersionUID = -1018974908517660133L;

	private Tag tag;

	/**
	 * Gets the tag associated with this command.
	 * 
	 * @return tag
	 */
	public Tag getTag()
	{
		return tag;
	}

	/**
	 * Sets the tag associated with this command.
	 * 
	 * @param tag
	 *          tag
	 */
	public void setTag(Tag tag)
	{
		this.tag = tag;
	}
}
