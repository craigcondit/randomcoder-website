package org.randomcoder.tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Statisitics for Tag instances with extensions for creating tag clouds.
 */
public class TagCloudEntry extends TagStatistics
{
	private static final long serialVersionUID = 7928407251644710555L;

	private static final Logger logger = LoggerFactory.getLogger(TagCloudEntry.class);

	private int scale;

	/**
	 * Constructs an empty TagCloudEntry.
	 */
	public TagCloudEntry()
	{
		super();
	}

	/**
	 * Creates a Tag cloud entry from the given statistics
	 * 
	 * @param stat
	 *          Tag statistics
	 * @param maximumArticleCount
	 *          ceiling value for article count
	 */
	public TagCloudEntry(TagStatistics stat, int maximumArticleCount)
	{
		super(stat.getTag(), stat.getArticleCount());

		if (maximumArticleCount <= 0)
		{
			scale = 0;
		}
		else
		{
			if (logger.isDebugEnabled())
			{
				logger.debug("Tag: " + getTag().getName() + " -> " + getArticleCount() + " / " + maximumArticleCount + " = "
						+ ((getArticleCount() * 10) / maximumArticleCount));
			}

			setScale((getArticleCount() * 10) / maximumArticleCount);
		}
	}

	/**
	 * Gets the scale factor (0-9) for this tag.
	 * 
	 * @return scale factor
	 */
	public int getScale()
	{
		return scale;
	}

	/**
	 * Sets the scale factor (0-9) for this tag.
	 * <p>
	 * A scale factor of 0 indicates the minimum size. A scale factor of 9
	 * indicates the largest size (most popular). Negative values will be
	 * translated to zero; Values greater than 9 will become 9.
	 * </p>
	 * 
	 * @param scale
	 *          scale factor
	 */
	public void setScale(int scale)
	{
		if (scale < 0)
		{
			scale = 0;
		}
		if (scale > 9)
		{
			scale = 9;
		}
		this.scale = scale;
	}
}
