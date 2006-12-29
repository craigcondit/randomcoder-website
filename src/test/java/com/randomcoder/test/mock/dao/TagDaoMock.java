package com.randomcoder.test.mock.dao;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.randomcoder.article.ArticleDao;
import com.randomcoder.tag.*;

public class TagDaoMock implements TagDao
{
	private final ArticleDao articleDao;
	
	private final List<Tag> tags = new ArrayList<Tag>();
	private long primaryKey = 0;
	
	public TagDaoMock(ArticleDao articleDao)
	{
		this.articleDao = articleDao;
	}
	
	public int countAll()
	{
		return tags.size();
	}

	public Tag findByName(String name)
	{
		for (Tag tag : tags)
		{
			if (tag.getName().equals(name)) 
			{
				tag.setArticles(articleDao.listByTag(tag));
				return tag;
			}
		}
		return null;
	}

	public List<Tag> listAll()
	{
		List<Tag> list = new ArrayList<Tag>(tags);
		Collections.sort(list, Tag.DISPLAY_NAME_COMPARATOR);
		
		// populate articles
		for (Tag tag : list) tag.setArticles(articleDao.listByTag(tag));
		
		return list;
	}

	public List<Tag> listAllInRange(int start, int limit)
	{
		List<Tag> list = listAll();
		
		// validate range
		if (start < 0 || start >= list.size()) return new ArrayList<Tag>();
		
		int end = start + limit;
		if (limit < 1) return new ArrayList<Tag>();
		
		if (end > list.size()) end = list.size();

		return list.subList(start, end);
	}

	public Long create(Tag newInstance)
	{
		validateRequiredFields(newInstance);
		
		String name = newInstance.getName();
		if (name != null && findByName(name) != null)
			throw new IllegalArgumentException("UNIQUE violation: name = " + name);
				
		newInstance.setId(primaryKey++);
		tags.add(newInstance);
		
		return newInstance.getId();
	}

	public void delete(Tag persistentObject)
	{
		Long id = persistentObject.getId();
		
		for (Iterator<Tag> it = tags.iterator(); it.hasNext();)
		{
			if (it.next().getId().equals(id))
			{
				it.remove();
				return;
			}
		}
		throw new IllegalArgumentException("NOT FOUND: id = " + id);
	}

	public void update(Tag transientObject)
	{
		Long id = transientObject.getId();
		
		Tag loaded = read(id);
		if (loaded == null)
			throw new IllegalArgumentException("NOT FOUND: id = " + id);
		
		validateRequiredFields(transientObject);
		
		delete(loaded);
		
		tags.add(transientObject);
	}

	public Tag read(Long id)
	{
		for (Tag tag : tags)
			if (tag.getId().equals(id))
				return tag;
		
		return null;
	}

	public List<TagStatistics> queryAllTagStatistics()
	{
		List<Tag> tagList = listAll();
		List<TagStatistics> statList = new ArrayList<TagStatistics>(tagList.size());
		
		for (Tag tag : tagList) statList.add(new TagStatistics(tag, tag.getArticles().size()));		
		return statList;
	}

	public List<TagStatistics> queryAllTagStatisticsInRange(int start, int limit)
	{
		List<TagStatistics> list = queryAllTagStatistics();
		
		// validate range
		if (start < 0 || start >= list.size()) return new ArrayList<TagStatistics>();
		
		int end = start + limit;
		if (limit < 1) return new ArrayList<TagStatistics>();
		
		if (end > list.size()) end = list.size();

		return list.subList(start, end);
	}

	public int queryMostArticles()
	{
		int max = 0;
		List<Tag> list = listAll();
		for (Tag tag : list)
		{
			int size = tag.getArticles().size();
			if (size > max) max = size;
		}
		return max;
	}

	private void validateRequiredFields(Tag tag)
	{
		if (StringUtils.trimToNull(tag.getName()) == null)
			throw new IllegalArgumentException("name required");
		
		if (StringUtils.trimToNull(tag.getDisplayName()) == null)
			throw new IllegalArgumentException("displayName required");
	}
	
}
