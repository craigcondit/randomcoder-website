package org.randomcoder.db;

import java.util.*;

import org.randomcoder.article.Article;
import org.randomcoder.dao.CrudDao;
import org.randomcoder.tag.Tag;

/**
 * Article data access interface.
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
public interface ArticleDao extends CrudDao<Article, Long>
{

	/**
	 * Loads an {@code Article} by its permalink
	 * @param permalink permalink name
	 * @return article if found, or null if no match
	 */
	public Article findByPermalink(String permalink);

	/**
	 * Lists {@code Article} objects within the range specified.
	 * @param start starting result to return, from 0
	 * @param limit maximum number of results to return
	 * @return list of {@code Article} objects
	 */
	public List<Article> listAllInRange(int start, int limit);

	/**
	 * Lists {@code Article} objects created before the specified date and within
	 * the range specified.
	 * @param endDate upper bound of date range (exclusive)
	 * @param start starting result to return, from 0
	 * @param limit maximum number of results to return
	 * @return list of {@code Article} objects
	 */
	public List<Article> listBeforeDateInRange(Date endDate, int start, int limit);

	/**
	 * Lists {@code Article} objects created before the specified date and within
	 * the range specified.
	 * @param tag tag to restrict by
	 * @param endDate upper bound of date range (exclusive)
	 * @param start starting result to return, from 0
	 * @param limit maximum number of results to return
	 * @return list of {@code Article} objects
	 */
	public List<Article> listByTagBeforeDateInRange(Tag tag, Date endDate, int start, int limit);

	/**
	 * Lists {@code Article} objects with the given tag. 
	 * @param tag tag
	 * @return list of {@code Article} objects
	 */
	public List<Article> listByTag(Tag tag);

	/**
	 * Iterates {@code Article} objects with the given tag. 
	 * @param tag tag
	 * @return Article iterator
	 */
	public Iterator<Article> iterateByTag(Tag tag);
	
	/**
	 * Lists {@code Article} objects created within the specified date range.
	 * @param startDate lower bound of date range (inclusive)
	 * @param endDate upper bound of date range (exclusive)
	 * @return list of {@code Article} objects
	 */
	public List<Article> listBetweenDates(Date startDate, Date endDate);

	
	/**
	 * Lists {@code Article} objects created within the specified date range.
	 * @param tag tag to restrict by
	 * @param startDate lower bound of date range (inclusive)
	 * @param endDate upper bound of date range (exclusive)
	 * @return list of {@code Article} objects
	 */
	public List<Article> listByTagBetweenDates(Tag tag, Date startDate, Date endDate);
	
	/**
	 * Counts all {@code Article} objects created before the specified date.
	 * @param endDate upper bound of date range (exclusive)
	 * @return count of articles
	 */
	public int countBeforeDate(Date endDate);
	
	/**
	 * Counts all {@code Article} objects created before the specified date.
	 * @param tag tag to restrict by
	 * @param endDate upper bound of date range (exclusive)
	 * @return count of articles
	 */
	public int countByTagBeforeDate(Tag tag, Date endDate);
}
