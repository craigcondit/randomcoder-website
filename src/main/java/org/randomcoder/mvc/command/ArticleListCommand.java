package org.randomcoder.mvc.command;

import java.io.Serializable;

import org.apache.commons.lang.builder.*;

/**
 * Command class used for article paging.
 */
public class ArticleListCommand implements Serializable
{
	private static final long serialVersionUID = -366354426204104148L;
	
	private int month = -1;
	private int day = -1;
	private int year = -1;

	/**
	 * Sets the month to display results for.
	 * 
	 * @param month
	 *          month number
	 */
	public void setMonth(int month)
	{
		this.month = month;
	}

	/**
	 * Gets the month to display results for.
	 * 
	 * @return month number
	 */
	public int getMonth()
	{
		return month;
	}

	/**
	 * Sets the day of month to display results for.
	 * 
	 * @param day
	 *          day of month
	 */
	public void setDay(int day)
	{
		this.day = day;
	}

	/**
	 * Gets the day of month to display results for.
	 * 
	 * @return day of month
	 */
	public int getDay()
	{
		return day;
	}

	/**
	 * Sets the year to display results for.
	 * 
	 * @param year
	 *          year
	 */
	public void setYear(int year)
	{
		this.year = year;
	}

	/**
	 * Gets the year to display results for.
	 * 
	 * @return year
	 */
	public int getYear()
	{
		return year;
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
