package com.randomcoder.article;

import java.io.Serializable;

import org.apache.commons.lang.builder.*;

/**
 * Command class used for article paging.
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
public class ArticlePageCommand implements Serializable
{
	private static final long serialVersionUID = 6439493419331923137L;

	private int start;
	private int limit;
	private int month = -1;
	private int day = -1;
	private int year = -1;

	/**
	 * Sets the starting item number to display (0-based).
	 * @param start item number
	 */
	public void setStart(int start)
	{
		this.start = start;
	}

	/**
	 * Gets the starting item number to display.
	 * @return item number
	 */
	public int getStart()
	{
		return start;
	}

	/**
	 * Sets the number of items to display per page.
	 * @param limit item count
	 */
	public void setLimit(int limit)
	{
		this.limit = limit;
	}

	/**
	 * Gets the number of items to display per page.
	 * @return item count
	 */
	public int getLimit()
	{
		return limit;
	}

	/**
	 * Sets the month to display results for.
	 * @param month month number
	 */
	public void setMonth(int month)
	{
		this.month = month;
	}

	/**
	 * Gets the month to display results for.
	 * @return month number
	 */
	public int getMonth()
	{
		return month;
	}

	/**
	 * Sets the day of month to display results for.
	 * @param day day of month
	 */
	public void setDay(int day)
	{
		this.day = day;
	}

	/**
	 * Gets the day of month to display results for.
	 * @return day of month
	 */
	public int getDay()
	{
		return day;
	}

	/**
	 * Sets the year to display results for.
	 * @param year year
	 */
	public void setYear(int year)
	{
		this.year = year;
	}

	/**
	 * Gets the year to display results for.
	 * @return year
	 */
	public int getYear()
	{
		return year;
	}

	/**
	 * Gets a string representation of this object, suitable for debugging.
	 * @return string representation of this object
	 */
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
