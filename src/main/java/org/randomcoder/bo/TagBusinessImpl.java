package org.randomcoder.bo;

import java.util.*;

import javax.inject.Inject;

import org.randomcoder.db.*;
import org.randomcoder.io.*;
import org.randomcoder.tag.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tag management implementation.
 */
@Component("tagBusiness")
public class TagBusinessImpl implements TagBusiness
{
	private TagRepository tagRepository;
	private ArticleRepository articleRepository;

	/**
	 * Sets the tag repository to use.
	 * 
	 * @param tagRepository
	 *          tag repository
	 */
	@Inject
	public void setTagRepository(TagRepository tagRepository)
	{
		this.tagRepository = tagRepository;
	}

	/**
	 * Sets the article repository to use.
	 * 
	 * @param articleRepository
	 *          article repository
	 */
	@Inject
	public void setArticleRepository(ArticleRepository articleRepository)
	{
		this.articleRepository = articleRepository;
	}

	@Override
	@Transactional(value = "transactionManager", readOnly = true)
	public List<TagCloudEntry> getTagCloud()
	{
		List<TagStatistics> tagStats = tagRepository.findAllTagStatistics();
		int mostArticles = tagRepository.maxArticleCount();

		List<TagCloudEntry> cloud = new ArrayList<TagCloudEntry>(tagStats.size());

		for (TagStatistics tag : tagStats)
		{
			if (tag.getArticleCount() > 0)
				cloud.add(new TagCloudEntry(tag, mostArticles));
		}

		return cloud;
	}

	@Override
	@Transactional(value = "transactionManager", readOnly = true)
	public void loadTagForEditing(Consumer<Tag> consumer, Long tagId)
	{
		Tag tag = loadTag(tagId);
		consumer.consume(tag);
	}

	@Override
	@Transactional("transactionManager")
	public void createTag(Producer<Tag> producer)
	{
		Tag tag = new Tag();
		producer.produce(tag);
		tagRepository.save(tag);
	}

	@Override
	@Transactional("transactionManager")
	public void updateTag(Producer<Tag> producer, Long tagId)
	{
		Tag tag = loadTag(tagId);
		producer.produce(tag);
		tagRepository.save(tag);
	}

	@Override
	@Transactional("transactionManager")
	public void deleteTag(Long tagId)
	{
		Tag tag = tagRepository.findOne(tagId);
		if (tag == null)
		{
			return;
		}

		// TODO bug here; articles continue to show old tags
		
		// remove tag from all articles which it applies to
		// failing to do this will result in ObjectNotFoundExceptions
		// we use an iterator here because the list of articles could be large
		for (Article article : articleRepository.iterateByTag(tag))
		{
			article.getTags().remove(tag);
		}
		tagRepository.delete(tag);
	}

	@Override
	@Transactional(value = "transactionManager", readOnly = true)
	public Tag findTagByName(String name)
	{
		return tagRepository.findByName(name);
	}

	private Tag loadTag(Long tagId)
	{
		Tag tag = tagRepository.findOne(tagId);
		if (tag == null)
		{
			throw new TagNotFoundException();
		}
		return tag;
	}

	@Override
	@Transactional(value = "transactionManager", readOnly = true)
	public Page<TagStatistics> findTagStatistics(Pageable pageable)
	{
		return tagRepository.findAllTagStatistics(pageable);
	}
}