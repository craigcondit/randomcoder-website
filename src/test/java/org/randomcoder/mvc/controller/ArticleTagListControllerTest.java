package org.randomcoder.mvc.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.bo.ArticleBusiness;
import org.randomcoder.bo.TagBusiness;
import org.randomcoder.content.ContentFilter;
import org.randomcoder.db.Article;
import org.randomcoder.db.Tag;
import org.randomcoder.mvc.command.ArticleTagListCommand;
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

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ArticleTagListControllerTest {
    private IMocksControl control;
    private ArticleBusiness ab;
    private ContentFilter cf;
    private TagBusiness tb;
    private Model m;
    private ArticleTagListController c;
    private HttpServletRequest r;

    @Before
    public void setUp() {
        control = createControl();
        ab = control.createMock(ArticleBusiness.class);
        cf = control.createMock(ContentFilter.class);
        tb = control.createMock(TagBusiness.class);
        m = control.createMock(Model.class);
        c = new ArticleTagListController();
        c.setArticleBusiness(ab);
        c.setContentFilter(cf);
        c.setTagBusiness(tb);
        r = control.createMock(HttpServletRequest.class);
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
        Tag tag = new Tag();
        tag.setName("tag");
        tag.setDisplayName("Tag");
        tag.setId(1L);

        ArticleTagListCommand cmd = new ArticleTagListCommand();
        cmd.setTag(tag);

        Date startDate = new Date();
        Date endDate = new Date();
        List<Article> articles = new ArrayList<>();

        expect(ab.listArticlesByTagBetweenDates(tag, startDate, endDate))
                .andReturn(articles);
        control.replay();

        assertSame(articles, c.listArticlesBetweenDates(cmd, startDate, endDate));
        control.verify();
    }

    @Test
    public void testListArticlesBeforeDate() {
        Tag tag = new Tag();
        tag.setName("tag");
        tag.setDisplayName("Tag");
        tag.setId(1L);

        ArticleTagListCommand cmd = new ArticleTagListCommand();
        cmd.setTag(tag);

        Date endDate = new Date();
        List<Article> articles = new ArrayList<>();
        Page<Article> page = new PageImpl<>(articles);

        Capture<Pageable> pageCap = newCapture();

        expect(ab.listArticlesByTagBeforeDate(same(tag), eq(endDate),
                capture(pageCap))).andReturn(page);
        control.replay();

        assertSame(page,
                c.listArticlesBeforeDate(cmd, endDate, PageRequest.of(0, 50)));
        control.verify();
        assertEquals(0, pageCap.getValue().getOffset());
        assertEquals(50, pageCap.getValue().getPageSize());
    }

    @Test
    public void testGetSubTitle() {
        Tag tag = new Tag();
        tag.setName("tag");
        tag.setDisplayName("Tag");
        tag.setId(1L);

        ArticleTagListCommand cmd = new ArticleTagListCommand();
        cmd.setTag(tag);

        assertEquals("Tag", c.getSubTitle(cmd));
    }

    @Test
    public void testTagList() {
        Tag tag = new Tag();
        tag.setName("tag");
        tag.setDisplayName("Tag");
        tag.setId(1L);

        ArticleTagListCommand cmd = new ArticleTagListCommand();

        Capture<Pageable> pageCap = newCapture();

        expect(tb.findTagByName("tag")).andReturn(tag);
        expect(ab.listArticlesByTagBetweenDates(same(tag), isA(Date.class),
                isA(Date.class))).andReturn(Collections.emptyList());
        expect(ab.listArticlesByTagBeforeDate(same(tag), isA(Date.class),
                capture(pageCap)))
                .andReturn(new PageImpl<>(Collections.emptyList()));
        expect(tb.getTagCloud())
                .andStubReturn(Collections.emptyList());
        expect(m.addAttribute(notNull(), notNull())).andStubReturn(m);
        expect(r.getRequestURL())
                .andStubReturn(new StringBuffer("http://localhost/"));
        expect(r.getParameterMap()).andStubReturn(Collections.emptyMap());
        expect(r.getParameter("year")).andStubReturn(null);
        expect(r.getParameter("month")).andStubReturn(null);
        control.replay();

        assertEquals("article-tag-list",
                c.tagList(cmd, m, "tag", PageRequest.of(0, 10), r));
        assertSame(tag, cmd.getTag());
        control.verify();
    }
}