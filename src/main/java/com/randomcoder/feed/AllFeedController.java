package com.randomcoder.feed;

import java.net.URL;
import java.util.List;

import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.Required;

import com.randomcoder.article.*;

/**
 * Feed controller which generates feeds for all articles.
 * 
 * <pre>
 * Copyright (c) 2007, Craig Condit. All rights reserved.
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
public class AllFeedController extends AbstractFeedController
{
	private URL feedUrl;
	private URL altUrl;
	private String title;
	private String subtitle;
	private ArticleDao articleDao;
	private String feedId;
	private int limit;
	
	/**
	 * Sets the URL for this feed.
	 * 
	 * @param feedUrl
	 *          feed URL
	 */
	@Required
	public void setFeedUrl(URL feedUrl)
	{
		this.feedUrl = feedUrl;
	}

	/**
	 * Sets the alternate URL for this feed.
	 * 
	 * @param altUrl
	 *          alternate URL
	 */
	public void setAltUrl(URL altUrl)
	{
		this.altUrl = altUrl;
	}

	/**
	 * Sets the title of this feed.
	 * 
	 * @param title
	 *          feed title
	 */
	@Required
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * Sets the subtitle of this feed.
	 * 
	 * @param subtitle
	 *          feed subtitle
	 */
	public void setSubtitle(String subtitle)
	{
		this.subtitle = subtitle;
	}
	
	/**
	 * Sets the Article DAO to use.
	 * 
	 * @param articleDao
	 *          Article DAO
	 */
	@Required
	public void setArticleDao(ArticleDao articleDao)
	{
		this.articleDao = articleDao;
	}
	
	/**
	 * Sets the maximum number of articles to add to the feed.
	 * 
	 * @param limit
	 *          article limit
	 */
	@Required
	public void setLimit(int limit)
	{
		this.limit = limit;
	}
	
	/**
	 * Sets the unique identifier for this feed.
	 * 
	 * @param feedId
	 *          feed id
	 */
	@Required
	public void setFeedId(String feedId)
	{
		this.feedId = feedId;
	}
	
	@Override
	protected FeedInfo getFeed(
			HttpServletRequest request, HttpServletResponse response,
			String... params) throws Exception
	{
		List<Article> articles = articleDao.listAllInRange(0, limit);
		
		FeedInfo feedInfo = new FeedInfo();
		
		feedInfo.setFeedUrl(feedUrl);
		feedInfo.setAltUrl(altUrl);
		feedInfo.setFeedId(feedId);
		feedInfo.setTitle(title);
		feedInfo.setSubtitle(subtitle);
		feedInfo.setArticles(articles);

		return feedInfo;
	}
}