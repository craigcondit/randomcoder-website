package com.randomcoder.tag;

import java.util.*;

import com.randomcoder.dao.TagDao;

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
public class TagBusinessImpl implements TagBusiness
{
	private TagDao tagDao;
	
	/**
	 * Sets the TagDao implementation to use.
	 * @param tagDao TagDao implementation
	 */
	public void setTagDao(TagDao tagDao)
	{
		this.tagDao = tagDao;
	}

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
}
