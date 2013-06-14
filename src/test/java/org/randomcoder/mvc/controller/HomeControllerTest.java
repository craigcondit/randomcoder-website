package org.randomcoder.mvc.controller;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import org.easymock.IMocksControl;
import org.junit.*;
import org.randomcoder.bo.*;
import org.randomcoder.content.ContentFilter;
import org.randomcoder.db.Article;
import org.randomcoder.mvc.command.ArticlePageCommand;
import org.randomcoder.tag.TagCloudEntry;
import org.springframework.ui.Model;

@SuppressWarnings("javadoc")
public class HomeControllerTest
{
	private IMocksControl control;
	private ArticleBusiness ab;
	private ContentFilter cf;
	private TagBusiness tb;
	private Model m;
	private HomeController c;

	@Before
	public void setUp()
	{
		control = createControl();
		ab = control.createMock(ArticleBusiness.class);
		cf = control.createMock(ContentFilter.class);
		tb = control.createMock(TagBusiness.class);
		m = control.createMock(Model.class);
		c = new HomeController();
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
		Date startDate = new Date();
		Date endDate = new Date();
		List<Article> articles = new ArrayList<>();

		expect(ab.listArticlesBetweenDates(startDate, endDate)).andReturn(articles);
		control.replay();

		assertSame(articles, c.listArticlesBetweenDates(null, startDate, endDate));
		control.verify();
	}

	@Test
	public void testListArticlesBeforeDateInRange()
	{
		Date endDate = new Date();
		List<Article> articles = new ArrayList<>();

		expect(ab.listArticlesBeforeDateInRange(endDate, 0, 50)).andReturn(articles);
		control.replay();

		assertSame(articles, c.listArticlesBeforeDateInRange(null, endDate, 0, 50));
		control.verify();
	}

	@Test
	public void testCountArticlesBeforeDate()
	{
		Date endDate = new Date();

		expect(ab.countArticlesBeforeDate(endDate)).andReturn(10);
		control.replay();

		assertEquals(10, c.countArticlesBeforeDate(null, endDate));
		control.verify();
	}

	@Test
	public void testGetSubTitle()
	{
		assertNull(c.getSubTitle(null));
	}

	@Test
	public void testHome()
	{
		expect(ab.listArticlesBetweenDates(isA(Date.class), isA(Date.class))).andReturn(Collections.<Article>emptyList());
		expect(ab.listArticlesBeforeDateInRange(isA(Date.class), eq(0), eq(10))).andReturn(Collections.<Article>emptyList());
		expect(ab.countArticlesBeforeDate(isA(Date.class))).andReturn(0);
		expect(tb.getTagCloud()).andStubReturn(Collections.<TagCloudEntry>emptyList());
		expect(m.addAttribute((String) notNull(), notNull())).andStubReturn(m);
		control.replay();

		assertEquals("home", c.home(new ArticlePageCommand(), m));
		control.verify();
	}
}