package org.randomcoder.mvc.controller;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.List;

import javax.inject.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.randomcoder.bo.ArticleBusiness;
import org.randomcoder.db.Article;
import org.randomcoder.feed.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Feed controller which generates feeds for all articles.
 */
@Controller("feedController")
public class FeedController
{
	private static final int ARTICLE_LIMIT = 20;
	private static final String FEED_TITLE = "randomCoder";
	private static final String FEED_SUBTITLE = "// TODO build a better web";

	private static final URL ATOM_ALL_URL;
	private static final URL RSS20_ALL_URL;
	private static final URL ALT_URL;
	static
	{
		try
		{
			ATOM_ALL_URL = new URL("https://randomcoder.org/feeds/atom/all");
			RSS20_ALL_URL = new URL("https://randomcoder.org/feeds/rss20/all");
			ALT_URL = new URL("https://randomcoder.org/");
		}
		catch (MalformedURLException e)
		{
			throw new ExceptionInInitializerError(e);
		}
	}

	private FeedGenerator atomFeedGenerator;
	private FeedGenerator rss20FeedGenerator;
	private ArticleBusiness articleBusiness;

	/**
	 * Sets the Atom feed generator.
	 * 
	 * @param atomFeedGenerator
	 *            atom feed generator
	 */
	@Inject
	@Named("atomFeedGenerator")
	public void setAtomFeedGenerator(FeedGenerator atomFeedGenerator)
	{
		this.atomFeedGenerator = atomFeedGenerator;
	}
	
	/**
	 * Sets the RSS 2.0 feed generator.
	 * 
	 * @param rss20FeedGenerator
	 *            RSS 2.0 feed generator
	 */
	@Inject
	@Named("rss20FeedGenerator")
	public void setRss20FeedGenerator(FeedGenerator rss20FeedGenerator)
	{
		this.rss20FeedGenerator = rss20FeedGenerator;
	}
	
	/**
	 * Sets the ArticleBusiness implementation to use.
	 * 
	 * @param articleBusiness
	 *            ArticleBusiness implementation
	 */
	@Inject
	public void setArticleBusiness(ArticleBusiness articleBusiness)
	{
		this.articleBusiness = articleBusiness;
	}

	/**
	 * Generates the Atom feed for all articles.
	 * 
	 * @param response
	 *            HTTP servlet response
	 * @throws Exception
	 *             if an error occurs
	 */
	@RequestMapping("/feeds/atom/all")
	public void atomAllFeed(HttpServletResponse response) throws Exception
	{
		generateFeed(atomFeedGenerator, response, "atom-all", ATOM_ALL_URL);
	}

	/**
	 * Generates the RSS 2.0 feed for all articles.
	 * 
	 * @param response
	 *            HTTP servlet response
	 * @throws Exception
	 *             if an error occurs
	 */
	@RequestMapping("/feeds/rss20/all")
	public void rss20AllFeed(HttpServletResponse response) throws Exception
	{
		generateFeed(rss20FeedGenerator, response, "rss20-all", RSS20_ALL_URL);
	}

	private FeedInfo getFeed(String feedId, URL feedUrl)
	{
		List<Article> articles = articleBusiness.listRecentArticles(ARTICLE_LIMIT);

		FeedInfo feedInfo = new FeedInfo();

		feedInfo.setFeedUrl(feedUrl);
		feedInfo.setAltUrl(ALT_URL);
		feedInfo.setFeedId(feedId);
		feedInfo.setTitle(FEED_TITLE);
		feedInfo.setSubtitle(FEED_SUBTITLE);
		feedInfo.setArticles(articles);

		return feedInfo;
	}

	private void generateFeed(FeedGenerator feedGenerator, 
			HttpServletResponse response, String feedId, URL feedUrl)
					throws FeedException, IOException
	{
		// get feed data
		FeedInfo feedInfo = getFeed(feedId, feedUrl);

		// generate feed
		String feed = feedGenerator.generateFeed(feedInfo);

		// output feed
		byte[] data = feed.getBytes(Charset.forName("UTF-8"));

		response.setContentType(feedGenerator.getContentType());
		response.setContentLength(data.length);

		ServletOutputStream out = null;

		try
		{
			out = response.getOutputStream();
			out.write(data);
		}
		finally
		{
			if (out != null)
				try
				{
					out.close();
				}
				catch (Throwable ignored)
				{
				}
		}
	}

}