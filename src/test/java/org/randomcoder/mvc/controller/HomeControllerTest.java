package org.randomcoder.mvc.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.bo.ArticleBusiness;
import org.randomcoder.bo.TagBusiness;
import org.randomcoder.content.ContentFilter;
import org.randomcoder.dao.Page;
import org.randomcoder.dao.Pagination;
import org.randomcoder.db.Article;
import org.randomcoder.mvc.command.ArticleListCommand;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class HomeControllerTest {
    private IMocksControl control;
    private ArticleBusiness ab;
    private ContentFilter cf;
    private TagBusiness tb;
    private Model m;
    private HomeController c;
    private HttpServletRequest r;

    @Before
    public void setUp() {
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
    public void tearDown() {
        c = null;
        tb = null;
        cf = null;
        ab = null;
    }

    @Test
    public void testListArticlesBetweenDates() {
        Date startDate = new Date();
        Date endDate = new Date();
        List<Article> articles = new ArrayList<>();

        expect(ab.listArticlesBetweenDates(startDate, endDate)).andReturn(articles);
        control.replay();

        assertSame(articles, c.listArticlesBetweenDates(null, startDate, endDate));
        control.verify();
    }

    @Test
    public void testListArticlesBeforeDate() {
        Date endDate = new Date();
        List<Article> articles = new ArrayList<>();
        Page<Article> page = new Page<>(articles, 0, 0, 50);

        expect(ab.listArticlesBeforeDate(eq(endDate), eq(0L), eq(50L))).andReturn(page);
        control.replay();

        assertSame(page, c.listArticlesBeforeDate(null, endDate, 0, 50));
        control.verify();
    }

    @Test
    public void testGetSubTitle() {
        assertNull(c.getSubTitle(null));
    }

    @Test
    public void testHome() {
        expect(ab.listArticlesBetweenDates(isA(Date.class), isA(Date.class))).andReturn(Collections.emptyList());
        expect(ab.listArticlesBeforeDate(isA(Date.class), eq(0L), eq(10L)))
                .andReturn(new Page<>(Collections.emptyList(), 0, 0, 10));
        expect(tb.getTagCloud()).andStubReturn(Collections.emptyList());
        expect(m.addAttribute(notNull(), notNull())).andStubReturn(m);
        expect(r.getRequestURL()).andStubReturn(new StringBuffer("http://localhost/"));
        expect(r.getParameterMap()).andStubReturn(Collections.emptyMap());
        expect(r.getParameter("year")).andStubReturn(null);
        expect(r.getParameter("month")).andStubReturn(null);
        control.replay();

        assertEquals("home", c.home(new ArticleListCommand(), m, Pagination.of(0, 10), r));
        control.verify();
    }

}