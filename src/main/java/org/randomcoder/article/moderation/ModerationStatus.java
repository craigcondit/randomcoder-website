package org.randomcoder.article.moderation;

/**
 * Moderation status enumeration.
 */
public enum ModerationStatus
{
	/**
	 * Pending.
	 */
	PENDING,

	/**
	 * Spam.
	 */
	SPAM,

	/**
	 * Ham (not spam).
	 */
	HAM;

	/**
	 * Getter method to expose name as JavaBean property.
	 * 
	 * @return name
	 */
	public String getName()
	{
		return name();
	}

	/**
	 * Getter method to expose ordinal as JavaBean property.
	 * 
	 * @return ordinal
	 */
	public int getOrdinal()
	{
		return ordinal();
	}
}
