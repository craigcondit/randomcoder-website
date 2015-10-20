package org.randomcoder.mvc.controller;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import org.easymock.*;
import org.junit.*;
import org.randomcoder.bo.*;
import org.randomcoder.content.ContentFilter;
import org.randomcoder.db.Article;
import org.randomcoder.mvc.command.ArticleListCommand;
import org.randomcoder.tag.TagCloudEntry;
import org.springframework.data.domain.*;
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
	public void testListArticlesBeforeDate()
	{
		Date endDate = new Date();
		List<Article> articles = new ArrayList<>();
		Page<Article> page = new PageImpl<>(articles);

		Capture<Pageable> pageCap = newCapture();

		expect(ab.listArticlesBeforeDate(eq(endDate), capture(pageCap))).andReturn(page);
		control.replay();

		assertSame(page, c.listArticlesBeforeDate(null, endDate, new PageRequest(0, 50)));
		control.verify();
		assertEquals(0, pageCap.getValue().getOffset());
		assertEquals(50, pageCap.getValue().getPageSize());
	}

	@Test
	public void testGetSubTitle()
	{
		assertNull(c.getSubTitle(null));
	}

	@Test
	public void testHome()
	{
		Capture<Pageable> pageCap = newCapture();

		expect(ab.listArticlesBetweenDates(isA(Date.class), isA(Date.class))).andReturn(Collections.<Article> emptyList());
		expect(ab.listArticlesBeforeDate(isA(Date.class), capture(pageCap))).andReturn(new PageImpl<>(Collections.<Article> emptyList()));
		expect(tb.getTagCloud()).andStubReturn(Collections.<TagCloudEntry> emptyList());
		expect(m.addAttribute((String) notNull(), notNull())).andStubReturn(m);
		control.replay();

		assertEquals("home", c.home(new ArticleListCommand(), m, new PageRequest(0, 10)));
		control.verify();
	}
}