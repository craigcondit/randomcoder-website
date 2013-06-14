package org.randomcoder.mvc.controller;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.net.URL;
import java.util.*;

import org.easymock.*;
import org.junit.*;
import org.randomcoder.bo.ArticleBusiness;
import org.randomcoder.db.Article;
import org.randomcoder.feed.*;
import org.randomcoder.mvc.controller.FeedController;
import org.springframework.mock.web.MockHttpServletResponse;

@SuppressWarnings("javadoc")
public class FeedControllerTest
{
	private IMocksControl control;
	private FeedGenerator atom;
	private FeedGenerator rss;
	private ArticleBusiness ab;
	private MockHttpServletResponse res;
	private FeedController fc;
	
	@Before
	public void setUp()
	{
		control = createControl();
		atom = control.createMock(FeedGenerator.class);
		rss = control.createMock(FeedGenerator.class);
		ab = control.createMock(ArticleBusiness.class);
		res = new MockHttpServletResponse();
		
		fc = new FeedController();
		fc.setArticleBusiness(ab);
		fc.setAtomFeedGenerator(atom);
		fc.setRss20FeedGenerator(rss);
	}

	@After
	public void tearDown()
	{
		fc = null;
		res = null;
		ab = null;
		rss = null;
		atom = null;
		control = null;
	}

	@Test
	public void testAtomAllFeed() throws Exception
	{
		Capture<FeedInfo> fi = new Capture<FeedInfo>();
		List<Article> articles = Collections.singletonList(new Article());
		
		expect(ab.listArticlesInRange(0,  20)).andReturn(articles);
		expect(atom.generateFeed(capture(fi))).andReturn("TEST");
		expect(atom.getContentType()).andReturn("text/plain");
		control.replay();
		
		fc.atomAllFeed(res);
		control.verify();
		
		assertEquals("Feed id", "atom-all", fi.getValue().getFeedId());
		assertEquals("Feed url", new URL("https://randomcoder.org/feeds/atom/all"), fi.getValue().getFeedUrl());
		assertEquals("Alt url", new URL("https://randomcoder.org/"), fi.getValue().getAltUrl());
		assertEquals("Title", "randomCoder", fi.getValue().getTitle());
		assertEquals("Subtitle", "// TODO build a better web", fi.getValue().getSubtitle());
		assertSame(articles, fi.getValue().getArticles());
		
		assertEquals("Content type", "text/plain", res.getContentType());
		assertEquals("Content length", 4, res.getContentLength());
		assertEquals("Content", "TEST", res.getContentAsString());		
	}

	@Test
	public void testRss20AllFeed() throws Exception
	{
		Capture<FeedInfo> fi = new Capture<FeedInfo>();
		List<Article> articles = Collections.singletonList(new Article());
		
		expect(ab.listArticlesInRange(0,  20)).andReturn(articles);
		expect(rss.generateFeed(capture(fi))).andReturn("TEST");
		expect(rss.getContentType()).andReturn("text/plain");
		control.replay();
		
		fc.rss20AllFeed(res);
		control.verify();
		
		assertEquals("Feed id", "rss20-all", fi.getValue().getFeedId());
		assertEquals("Feed url", new URL("https://randomcoder.org/feeds/rss20/all"), fi.getValue().getFeedUrl());
		assertEquals("Alt url", new URL("https://randomcoder.org/"), fi.getValue().getAltUrl());
		assertEquals("Title", "randomCoder", fi.getValue().getTitle());
		assertEquals("Subtitle", "// TODO build a better web", fi.getValue().getSubtitle());
		assertSame(articles, fi.getValue().getArticles());
		
		assertEquals("Content type", "text/plain", res.getContentType());
		assertEquals("Content length", 4, res.getContentLength());
		assertEquals("Content", "TEST", res.getContentAsString());		
	}
}