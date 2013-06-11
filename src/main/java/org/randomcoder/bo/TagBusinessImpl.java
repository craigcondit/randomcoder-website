package org.randomcoder.bo;

import java.util.*;

import javax.inject.Inject;

import org.randomcoder.article.Article;
import org.randomcoder.db.*;
import org.randomcoder.io.*;
import org.randomcoder.tag.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tag management implementation.
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
@Component("tagBusiness")
public class TagBusinessImpl implements TagBusiness
{
	private TagDao tagDao;
	private ArticleDao articleDao;
	
	/**
	 * Sets the TagDao implementation to use.
	 * @param tagDao TagDao implementation
	 */
	@Inject
	public void setTagDao(TagDao tagDao)
	{
		this.tagDao = tagDao;
	}
	
	/**
	 * Sets the ArticleDao implementation to use.
	 * @param articleDao ArticleDao implementation
	 */
	@Inject
	public void setArticleDao(ArticleDao articleDao)
	{
		this.articleDao = articleDao;
	}

	@Override
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
	@Transactional(readOnly=true)
	public void loadTagForEditing(Consumer<Tag> consumer, Long tagId)
	{
		Tag tag = loadTag(tagId);
		consumer.consume(tag);
	}
	
	@Override
	@Transactional
	public void createTag(Producer<Tag> producer)
	{
		Tag tag = new Tag();
		producer.produce(tag);
		tagDao.create(tag);		
	}

	@Override
	@Transactional
	public void updateTag(Producer<Tag> producer, Long tagId)
	{
		Tag tag = loadTag(tagId);
		producer.produce(tag);
		tagDao.update(tag);
	}

	@Override
	@Transactional
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
	
	private Tag loadTag(Long tagId)
	{
		Tag tag = tagDao.read(tagId);
		if (tag == null) throw new TagNotFoundException();
		return tag;
	}
	
}
