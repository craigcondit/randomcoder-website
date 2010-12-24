package com.randomcoder.tag;

import java.util.*;

import org.hibernate.Query;

import com.randomcoder.dao.hibernate.HibernateDao;

/**
 * Tag data access implementation.
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
public class TagDaoImpl extends HibernateDao<Tag, Long> implements TagDaoBase
{
	
	private static final String QUERY_ALL_TAG_STATISTICS = "Tag.AllTagStatistics";
	private static final String QUERY_MOST_ARTICLES = "Tag.MostArticles";

	/**
	 * Default constructor.
	 */
	public TagDaoImpl()
	{
		super(Tag.class);
	}

	@Override
	public List<TagStatistics> queryAllTagStatistics()
	{
		return queryAllTagStatisticsInRange(0, 0);
	}

	@Override
	public List<TagStatistics> queryAllTagStatisticsInRange(int start, int limit)
	{
		Query query = getSession().getNamedQuery(QUERY_ALL_TAG_STATISTICS);
		
		if (start > 0) query.setFirstResult(start);
		if (limit > 0) query.setMaxResults(limit);
		
		List results = query.list();
		
		List<TagStatistics> tagStats = new ArrayList<TagStatistics>(results.size());
		
		for (Object result : results)
		{
			Object[] data = (Object[]) result;
			
			Tag tag = (Tag) data[0];
			int articleCount = ((Number) data[1]).intValue(); 
			
			tagStats.add(new TagStatistics(tag, articleCount));
		}
		
		return tagStats;
	}

	@Override
	public int queryMostArticles()
	{
		Number result = (Number) getSession().getNamedQuery(QUERY_MOST_ARTICLES).uniqueResult();
		return result == null ? 0 : result.intValue();
	}
}