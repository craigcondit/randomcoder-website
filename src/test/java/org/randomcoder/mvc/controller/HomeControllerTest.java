package org.randomcoder.mvc.controller;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.notNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.bo.ArticleBusiness;
import org.randomcoder.bo.TagBusiness;
import org.randomcoder.content.ContentFilter;
import org.randomcoder.db.Article;
import org.randomcoder.mvc.command.ArticleListCommand;
import org.randomcoder.tag.TagCloudEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("javadoc")
public class HomeControllerTest
{
	private IMocksControl control;
	private ArticleBusiness ab;
	private ContentFilter cf;
	private TagBusiness tb;
	private Model m;
	private HomeController c;
	private HttpServletRequest r;

	@Before
	public void setUp()
	{
		control = createControl();
		ab = control.createMock(ArticleBusiness.class);
		cf = control.createMock(ContentFilter.class);
		tb = control.createMock(TagBusiness.class);
		m = control.createMock(Model.class);
		r = control.createMock(HttpServletRequest.class);
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
    expect(r.getRequestURL()).andStubReturn(new StringBuffer("http://localhost/"));
    expect(r.getParameterMap()).andStubReturn(Collections.emptyMap());
    expect(r.getParameter("year")).andStubReturn(null);
    expect(r.getParameter("month")).andStubReturn(null);
		control.replay();

		assertEquals("home", c.home(new ArticleListCommand(), m, new PageRequest(0, 10), r));
		control.verify();
	}
}