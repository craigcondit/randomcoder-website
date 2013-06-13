package org.randomcoder.controller;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import org.easymock.IMocksControl;
import org.junit.*;
import org.randomcoder.article.ArticleTagPageCommand;
import org.randomcoder.bo.*;
import org.randomcoder.content.ContentFilter;
import org.randomcoder.db.*;
import org.randomcoder.tag.TagCloudEntry;
import org.springframework.ui.Model;

@SuppressWarnings("javadoc")
public class ArticleTagListControllerTest
{
	private IMocksControl control;
	private ArticleBusiness ab;
	private ContentFilter cf;
	private TagBusiness tb;
	private Model m;
	private ArticleTagListController c;

	@Before
	public void setUp()
	{
		control = createControl();
		ab = control.createMock(ArticleBusiness.class);
		cf = control.createMock(ContentFilter.class);
		tb = control.createMock(TagBusiness.class);
		m = control.createMock(Model.class);
		c = new ArticleTagListController();
		c.setArticleBusiness(ab);
		c.setContentFilter(cf);
		c.setTagBusiness(tb);
	}

	@After
	public void tearDown()
	{
		c = null;
		tb = null;
		cf = null;
		ab = null;
	}

	@Test
	public void testListArticlesBetweenDates()
	{
		Tag tag = new Tag();
		tag.setName("tag");
		tag.setDisplayName("Tag");
		tag.setId(1L);
		
		ArticleTagPageCommand cmd = new ArticleTagPageCommand();
		cmd.setTag(tag);
		
		Date startDate = new Date();
		Date endDate = new Date();
		List<Article> articles = new ArrayList<>();

		expect(ab.listArticlesByTagBetweenDates(tag, startDate, endDate)).andReturn(articles);
		control.replay();

		assertSame(articles, c.listArticlesBetweenDates(cmd, startDate, endDate));
		control.verify();
	}

	@Test
	public void testListArticlesBeforeDateInRange()
	{
		Tag tag = new Tag();
		tag.setName("tag");
		tag.setDisplayName("Tag");
		tag.setId(1L);
		
		ArticleTagPageCommand cmd = new ArticleTagPageCommand();
		cmd.setTag(tag);
		
		Date endDate = new Date();
		List<Article> articles = new ArrayList<>();

		expect(ab.listArticlesByTagBeforeDateInRange(tag, endDate, 0, 50)).andReturn(articles);
		control.replay();

		assertSame(articles, c.listArticlesBeforeDateInRange(cmd, endDate, 0, 50));
		control.verify();
	}

	@Test
	public void testCountArticlesBeforeDate()
	{
		Tag tag = new Tag();
		tag.setName("tag");
		tag.setDisplayName("Tag");
		tag.setId(1L);
		
		ArticleTagPageCommand cmd = new ArticleTagPageCommand();
		cmd.setTag(tag);
		
		Date endDate = new Date();

		expect(ab.countArticlesByTagBeforeDate(tag, endDate)).andReturn(10);
		control.replay();

		assertEquals(10, c.countArticlesBeforeDate(cmd, endDate));
		control.verify();
	}

	@Test
	public void testGetSubTitle()
	{
		Tag tag = new Tag();
		tag.setName("tag");
		tag.setDisplayName("Tag");
		tag.setId(1L);
		
		ArticleTagPageCommand cmd = new ArticleTagPageCommand();
		cmd.setTag(tag);
		
		assertEquals("Tag", c.getSubTitle(cmd));
	}

	@Test
	public void testHome()
	{
		Tag tag = new Tag();
		tag.setName("tag");
		tag.setDisplayName("Tag");
		tag.setId(1L);
		
		ArticleTagPageCommand cmd = new ArticleTagPageCommand();
		
		expect(tb.findTagByName("tag")).andReturn(tag);
		expect(ab.listArticlesByTagBetweenDates(same(tag), isA(Date.class), isA(Date.class))).andReturn(Collections.<Article>emptyList());
		expect(ab.listArticlesByTagBeforeDateInRange(same(tag), isA(Date.class), eq(0), eq(10))).andReturn(Collections.<Article>emptyList());
		expect(ab.countArticlesByTagBeforeDate(same(tag), isA(Date.class))).andReturn(0);
		expect(tb.getTagCloud()).andStubReturn(Collections.<TagCloudEntry>emptyList());
		expect(m.addAttribute((String) notNull(), notNull())).andStubReturn(m);
		control.replay();

		assertEquals("article-tag-list", c.tagList(cmd, m, "tag"));
		assertSame(tag, cmd.getTag());
		control.verify();
	}
}