package org.randomcoder.mvc.controller;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import org.easymock.*;
import org.junit.*;
import org.randomcoder.article.ArticleDecorator;
import org.randomcoder.bo.TagBusiness;
import org.randomcoder.db.*;
import org.randomcoder.mvc.command.ArticleListCommand;
import org.randomcoder.tag.*;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Direction;
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
		ArticleListCommand cmd = new ArticleListCommand();
		
		Tag tag = new Tag();
		tag.setId(1L);
		tag.setName("test");
		tag.setDisplayName("Test");
		
		List<TagCloudEntry> tc = new ArrayList<>();
		tc.add(new TagCloudEntry(new TagStatistics(tag, 1), 1));
		
		Capture<List<ArticleDecorator>> ad = newCapture();
		Capture<boolean[]> days = newCapture();
		
		Pageable pr = new PageRequest(0, 10);
		
		expect(tb.getTagCloud()).andReturn(tc);
		expect(m.addAttribute(eq("articles"), capture(ad))).andReturn(m);
		expect(m.addAttribute(eq("pager"), isA(Page.class))).andReturn(m);
		expect(m.addAttribute(eq("days"), capture(days))).andReturn(m);
		expect(m.addAttribute(eq("tagCloud"), same(tc))).andReturn(m);
		expect(m.addAttribute("pageSubTitle", "Subtitle")).andReturn(m);
		control.replay();
		
		c.populateModel(cmd, m, pr);
		control.verify();
		
		assertEquals("test article", ad.getValue().get(0).getArticle().getTitle());		
		assertTrue(days.getValue()[0]);
	}

	static class MockArticleListController extends AbstractArticleListController<ArticleListCommand>
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
		protected List<Article> listArticlesBetweenDates(ArticleListCommand command, Date startDate, Date endDate)
		{			
			return Collections.singletonList(createArticle());
		}

		@Override
		protected Page<Article> listArticlesBeforeDate(ArticleListCommand command, Date cutoffDate, Pageable pageable)
		{
			assertEquals(0, pageable.getOffset());
			assertEquals(10, pageable.getPageSize());
			assertEquals(new Sort(Direction.DESC, "creationDate"), pageable.getSort());
			return new PageImpl<>(Collections.singletonList(createArticle()));
		}

		@Override
		protected String getSubTitle(ArticleListCommand command)
		{
			return "Subtitle";
		}
	}
}