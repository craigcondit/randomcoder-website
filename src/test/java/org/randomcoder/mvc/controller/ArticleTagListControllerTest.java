package org.randomcoder.mvc.controller;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import org.easymock.*;
import org.junit.*;
import org.randomcoder.bo.*;
import org.randomcoder.content.ContentFilter;
import org.randomcoder.db.*;
import org.randomcoder.mvc.command.ArticleTagListCommand;
import org.randomcoder.tag.TagCloudEntry;
import org.springframework.data.domain.*;
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
		
		ArticleTagListCommand cmd = new ArticleTagListCommand();
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
	public void testListArticlesBeforeDate()
	{
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
		
		expect(ab.listArticlesByTagBeforeDate(same(tag), eq(endDate), capture(pageCap))).andReturn(page);
		control.replay();

		assertSame(page, c.listArticlesBeforeDate(cmd, endDate, new PageRequest(0, 50)));
		control.verify();
		assertEquals(0, pageCap.getValue().getOffset());
		assertEquals(50, pageCap.getValue().getPageSize());
	}

	@Test
	public void testGetSubTitle()
	{
		Tag tag = new Tag();
		tag.setName("tag");
		tag.setDisplayName("Tag");
		tag.setId(1L);
		
		ArticleTagListCommand cmd = new ArticleTagListCommand();
		cmd.setTag(tag);
		
		assertEquals("Tag", c.getSubTitle(cmd));
	}

	@Test
	public void testTagList()
	{
		Tag tag = new Tag();
		tag.setName("tag");
		tag.setDisplayName("Tag");
		tag.setId(1L);
		
		ArticleTagListCommand cmd = new ArticleTagListCommand();
		
		Capture<Pageable> pageCap = newCapture();
		
		expect(tb.findTagByName("tag")).andReturn(tag);
		expect(ab.listArticlesByTagBetweenDates(same(tag), isA(Date.class), isA(Date.class))).andReturn(Collections.<Article>emptyList());
		expect(ab.listArticlesByTagBeforeDate(same(tag), isA(Date.class), capture(pageCap))).andReturn(new PageImpl<>(Collections.<Article>emptyList()));
		expect(tb.getTagCloud()).andStubReturn(Collections.<TagCloudEntry>emptyList());
		expect(m.addAttribute((String) notNull(), notNull())).andStubReturn(m);
		control.replay();

		assertEquals("article-tag-list", c.tagList(cmd, m, "tag", new PageRequest(0, 10)));
		assertSame(tag, cmd.getTag());
		control.verify();
	}
}