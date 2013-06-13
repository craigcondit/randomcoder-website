package org.randomcoder.controller;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import org.easymock.*;
import org.junit.*;
import org.randomcoder.article.*;
import org.randomcoder.bo.TagBusiness;
import org.randomcoder.db.*;
import org.randomcoder.tag.*;
import org.springframework.ui.Model;

@SuppressWarnings("javadoc")
public class AbsArticleListControllerTest
{
	private IMocksControl control;
	private Model m;
	private TagBusiness tb;
	private MockArticleListController c;
	
	@Before
	public void setUp()
	{
		control = createControl();
		m = control.createMock(Model.class);
		tb = control.createMock(TagBusiness.class);
		c = new MockArticleListController();
		c.setTagBusiness(tb);
	}

	@After
	public void tearDown()
	{
		c = null;
		tb = null;
		m = null;
		control = null;
	}

	@Test
	public void testPopulateModel()
	{
		ArticlePageCommand cmd = new ArticlePageCommand();
		
		Tag tag = new Tag();
		tag.setId(1L);
		tag.setName("test");
		tag.setDisplayName("Test");
		
		List<TagCloudEntry> tc = new ArrayList<>();
		tc.add(new TagCloudEntry(new TagStatistics(tag, 1), 1));
		
		Capture<List<ArticleDecorator>> ad = new Capture<>();
		Capture<boolean[]> days = new Capture<>();
		
		expect(tb.getTagCloud()).andReturn(tc);
		expect(m.addAttribute(eq("articles"), capture(ad))).andReturn(m);
		expect(m.addAttribute(eq("days"), capture(days))).andReturn(m);
		expect(m.addAttribute("pageCount", 1)).andReturn(m);
		expect(m.addAttribute("pageStart", 0)).andReturn(m);
		expect(m.addAttribute("pageLimit", 10)).andReturn(m);
		expect(m.addAttribute(eq("tagCloud"), same(tc))).andReturn(m);
		expect(m.addAttribute("pageSubTitle", "Subtitle")).andReturn(m);
		control.replay();
		
		c.populateModel(cmd, m);
		control.verify();
		
		assertEquals("test article", ad.getValue().get(0).getArticle().getTitle());		
		assertTrue(days.getValue()[0]);
	}

	static class MockArticleListController extends AbstractArticleListController<ArticlePageCommand>
	{
		private Article createArticle()
		{
			Article a = new Article();
			a.setTitle("test article");
			
			Calendar date = new GregorianCalendar();
			date.set(Calendar.DATE, 1);
			
			a.setCreationDate(date.getTime());
			a.setComments(Collections.<Comment>emptyList());
			return a;
		}
		
		@Override
		protected List<Article> listArticlesBetweenDates(ArticlePageCommand command, Date startDate, Date endDate)
		{			
			return Collections.singletonList(createArticle());
		}

		@Override
		protected List<Article> listArticlesBeforeDateInRange(ArticlePageCommand command, Date cutoffDate, int start, int limit)
		{
			assertEquals(0, start);
			assertEquals(10, limit);
			return Collections.singletonList(createArticle());
		}

		@Override
		protected int countArticlesBeforeDate(ArticlePageCommand command, Date cutoffDate)
		{
			return 1;
		}

		@Override
		protected String getSubTitle(ArticlePageCommand command)
		{
			return "Subtitle";
		}
	}
}