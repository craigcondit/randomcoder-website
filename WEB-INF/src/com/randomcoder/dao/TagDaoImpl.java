package com.randomcoder.dao;

import java.util.*;

import org.hibernate.Query;

import com.randomcoder.bean.Tag;
import com.randomcoder.dao.hibernate.HibernateDao;
import com.randomcoder.tag.TagStatistics;

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

	public List<TagStatistics> queryAllTagStatistics()
	{
		return queryAllTagStatisticsInRange(0, 0);
	}

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

	public int queryMostArticles()
	{
		return ((Number) getSession().getNamedQuery(QUERY_MOST_ARTICLES).uniqueResult()).intValue();
	}
}
