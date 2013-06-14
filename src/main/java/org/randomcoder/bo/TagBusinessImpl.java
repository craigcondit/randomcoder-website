package org.randomcoder.bo;

import java.util.*;

import javax.inject.Inject;

import org.randomcoder.db.*;
import org.randomcoder.io.*;
import org.randomcoder.tag.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tag management implementation.
 */
@Component("tagBusiness")
public class TagBusinessImpl implements TagBusiness
{
	private TagDao tagDao;
	private ArticleDao articleDao;

	/**
	 * Sets the TagDao implementation to use.
	 * 
	 * @param tagDao
	 *          TagDao implementation
	 */
	@Inject
	public void setTagDao(TagDao tagDao)
	{
		this.tagDao = tagDao;
	}

	/**
	 * Sets the ArticleDao implementation to use.
	 * 
	 * @param articleDao
	 *          ArticleDao implementation
	 */
	@Inject
	public void setArticleDao(ArticleDao articleDao)
	{
		this.articleDao = articleDao;
	}

	@Override
	@Transactional(value = "hibernateTransactionManager", readOnly = true)
	public List<TagCloudEntry> getTagCloud()
	{
		List<TagStatistics> tagStats = tagDao.queryAllTagStatistics();
		int mostArticles = tagDao.queryMostArticles();

		List<TagCloudEntry> cloud = new ArrayList<TagCloudEntry>(tagStats.size());

		for (TagStatistics tag : tagStats)
		{
			if (tag.getArticleCount() > 0)
				cloud.add(new TagCloudEntry(tag, mostArticles));
		}

		return cloud;
	}

	@Override
	@Transactional(value = "hibernateTransactionManager", readOnly = true)
	public void loadTagForEditing(Consumer<Tag> consumer, Long tagId)
	{
		Tag tag = loadTag(tagId);
		consumer.consume(tag);
	}

	@Override
	@Transactional("hibernateTransactionManager")
	public void createTag(Producer<Tag> producer)
	{
		Tag tag = new Tag();
		producer.produce(tag);
		tagDao.create(tag);
	}

	@Override
	@Transactional("hibernateTransactionManager")
	public void updateTag(Producer<Tag> producer, Long tagId)
	{
		Tag tag = loadTag(tagId);
		producer.produce(tag);
		tagDao.update(tag);
	}

	@Override
	@Transactional("hibernateTransactionManager")
	public void deleteTag(Long tagId)
	{
		Tag tag = loadTag(tagId);

		// remove tag from all articles which it applies to
		// failing to do this will result in ObjectNotFoundExceptions
		// we use an iterator here because the list of articles could be large
		Iterator<Article> articles = articleDao.iterateByTag(tag);
		while (articles.hasNext())
		{
			Article article = articles.next();
			article.getTags().remove(tag);
		}

		tagDao.delete(tag);
	}

	@Override
	@Transactional(value = "hibernateTransactionManager", readOnly = true)
	public Tag findTagByName(String name)
	{
		return tagDao.findByName(name);
	}

	private Tag loadTag(Long tagId)
	{
		Tag tag = tagDao.read(tagId);
		if (tag == null)
			throw new TagNotFoundException();
		return tag;
	}

	@Override
	@Transactional(value = "hibernateTransactionManager", readOnly = true)
	public List<TagStatistics> queryTagStatistics()
	{
		return tagDao.queryAllTagStatistics();
	}

	@Override
	@Transactional(value = "hibernateTransactionManager", readOnly = true)
	public List<TagStatistics> queryTagStatisticsInRange(int start, int limit)
	{
		return tagDao.queryAllTagStatisticsInRange(start, limit);
	}

	@Override
	@Transactional(value = "hibernateTransactionManager", readOnly = true)
	public int queryTagMostArticles()
	{
		return tagDao.queryMostArticles();
	}

	@Override
	@Transactional(value = "hibernateTransactionManager", readOnly = true)
	public int countTags()
	{
		return tagDao.countAll();
	}
}