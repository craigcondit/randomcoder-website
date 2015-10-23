package org.randomcoder.mvc.editor;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import org.easymock.IMocksControl;
import org.junit.*;
import org.randomcoder.bo.TagBusiness;
import org.randomcoder.db.Tag;
import org.randomcoder.tag.TagList;

@SuppressWarnings("javadoc")
public class TagListPropertyEditorTest
{
	private IMocksControl control;
	private TagBusiness tb;
	private TagListPropertyEditor editor;

	@Before
	public void setUp()
	{
		control = createControl();
		tb = control.createMock(TagBusiness.class);
		editor = new TagListPropertyEditor(tb);
	}

	@After
	public void tearDown()
	{
		editor = null;
		tb = null;
		control = null;
	}

	@Test
	public void testGetAsText()
	{
		List<Tag> tags = new ArrayList<>();

		Tag tag = new Tag();
		tag.setId(1L);
		tag.setName("test");
		tag.setDisplayName("Test");
		tags.add(tag);

		Tag tag2 = new Tag();
		tag2.setId(2L);
		tag2.setName("test2");
		tag2.setDisplayName("Test2");
		tags.add(tag2);

		TagList value = new TagList(tags);

		editor.setValue(value);
		assertEquals("test, test2", editor.getAsText());
	}

	@Test
	public void testGetAsTextNull()
	{
		editor.setValue(null);
		assertEquals("", editor.getAsText());
	}
	
	@Test
	public void testSetAsText()
	{
		Tag tag = new Tag();
		tag.setId(1L);
		tag.setName("tag");
		tag.setDisplayName("Tag");
		
		expect(tb.findTagByName("tag")).andReturn(tag);
		control.replay();
		
		editor.setAsText("tag");
		control.verify();
		
		TagList tl = (TagList) editor.getValue();
		assertEquals(1, tl.getTags().size());
		assertEquals("tag", tl.getTags().get(0).getName());
		assertEquals("Tag", tl.getTags().get(0).getDisplayName());
		assertEquals(Long.valueOf(1L), tl.getTags().get(0).getId());
	}

	@Test
	public void testSetAsTextNotFound()
	{
		expect(tb.findTagByName("bogus-tag")).andReturn(null);
		control.replay();
		
		editor.setAsText("bogus-tag");
		control.verify();
		
		TagList tl = (TagList) editor.getValue();
		assertEquals(1, tl.getTags().size());
		assertEquals("bogus-tag", tl.getTags().get(0).getName());
		assertEquals("bogus-tag", tl.getTags().get(0).getDisplayName());
		assertNull(tl.getTags().get(0).getId());
	}	
}
