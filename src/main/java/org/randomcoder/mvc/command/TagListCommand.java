package org.randomcoder.mvc.command;

import java.io.Serializable;

import org.apache.commons.lang.builder.*;

/**
 * Command class used for tag lists.
 */
public class TagListCommand implements Serializable
{
	private static final long serialVersionUID = 5657688205356090811L;

	private int start;
	private int limit;

	/**
	 * Sets the starting item number to display (0-based).
	 * 
	 * @param start
	 *          item number
	 */
	public void setStart(int start)
	{
		this.start = start;
	}

	/**
	 * Gets the starting item number to display.
	 * 
	 * @return item number
	 */
	public int getStart()
	{
		return start;
	}

	/**
	 * Sets the number of items to display per page.
	 * 
	 * @param limit
	 *          item count
	 */
	public void setLimit(int limit)
	{
		this.limit = limit;
	}

	/**
	 * Gets the number of items to display per page.
	 * 
	 * @return item count
	 */
	public int getLimit()
	{
		return limit;
	}

	/**
	 * Gets a string representation of this object, suitable for debugging.
	 * 
	 * @return string representation of this object
	 */
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
