package org.randomcoder.mvc.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.article.ArticleDecorator;
import org.randomcoder.article.CalendarInfo;
import org.randomcoder.bo.TagBusiness;
import org.randomcoder.dao.Page;
import org.randomcoder.dao.Pagination;
import org.randomcoder.db.Article;
import org.randomcoder.db.Tag;
import org.randomcoder.mvc.command.ArticleListCommand;
import org.randomcoder.pagination.PagerInfo;
import org.randomcoder.tag.TagCloudEntry;
import org.randomcoder.tag.TagStatistics;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AbsArticleListControllerTest {
    private IMocksControl control;
    private Model m;
    private TagBusiness tb;
    private MockArticleListController c;
    private HttpServletRequest r;

    @Before
    public void setUp() {
        control = createControl();
        m = control.createMock(Model.class);
        tb = control.createMock(TagBusiness.class);
        r = control.createMock(HttpServletRequest.class);
        c = new MockArticleListController();
        c.setTagBusiness(tb);
    }

    @After
    public void tearDown() {
        c = null;
        tb = null;
        m = null;
        control = null;
    }

    @Test
    public void testPopulateModel() {
        ArticleListCommand cmd = new ArticleListCommand();

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setDisplayName("Test");

        List<TagCloudEntry> tc = new ArrayList<>();
        tc.add(new TagCloudEntry(new TagStatistics(tag, 1), 1));

        Capture<List<ArticleDecorator>> ad = newCapture();
        Capture<boolean[]> days = newCapture();

        Capture<CalendarInfo> cal = newCapture();

        Pagination pg = Pagination.of(0, 10);
        expect(tb.getTagCloud()).andReturn(tc);
        expect(m.addAttribute(eq("articles"), capture(ad))).andReturn(m);
        expect(m.addAttribute(eq("pager"), isA(Page.class))).andReturn(m);
        expect(m.addAttribute(eq("days"), capture(days))).andReturn(m);
        expect(m.addAttribute(eq("tagCloud"), same(tc))).andReturn(m);
        expect(m.addAttribute(eq("pagerInfo"), isA(PagerInfo.class))).andReturn(m);
        expect(m.addAttribute("pageSubTitle", "Subtitle")).andReturn(m);
        expect(m.addAttribute(eq("calendar"), capture(cal))).andReturn(m);
        expect(r.getRequestURL())
                .andStubReturn(new StringBuffer("http://localhost/"));
        expect(r.getParameterMap()).andStubReturn(Collections.emptyMap());
        expect(r.getParameter("year")).andStubReturn("2015");
        expect(r.getParameter("month")).andStubReturn("1");
        control.replay();

        c.populateModel(cmd, m, pg, r);
        control.verify();

        assertEquals("test article", ad.getValue().get(0).getArticle().getTitle());
        assertTrue(days.getValue()[0]);

        CalendarInfo cinfo = cal.getValue();
        assertEquals("/", cinfo.getSelfLink());
        assertTrue("month", cinfo.getPrevMonthLink().contains("month=12"));
        assertTrue("year", cinfo.getPrevMonthLink().contains("year=2014"));
    }

    static class MockArticleListController extends AbstractArticleListController<ArticleListCommand> {
        private Article createArticle() {
            Article a = new Article();
            a.setTitle("test article");

            Calendar date = new GregorianCalendar();
            date.set(Calendar.DATE, 1);

            a.setCreationDate(date.getTime());
            a.setComments(Collections.emptyList());
            return a;
        }

        @Override
        protected List<Article> listArticlesBetweenDates(ArticleListCommand command, Date startDate, Date endDate) {
            return Collections.singletonList(createArticle());
        }

        @Override
        protected Page<Article> listArticlesBeforeDate(ArticleListCommand command, Date cutoffDate, long offset, long length) {
            assertEquals(0L, offset);
            assertEquals(10L, length);
            return new Page<>(Collections.singletonList(createArticle()), offset, 1, length);
        }

        @Override
        protected String getSubTitle(ArticleListCommand command) {
            return "Subtitle";
        }
    }

}
