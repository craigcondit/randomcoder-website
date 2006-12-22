package com.randomcoder.content;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.lang.reflect.Constructor;

import org.junit.Test;
import org.xml.sax.InputSource;

import com.randomcoder.test.mock.content.ContentFilterMock;

public class ContentUtilsTest
{
	private static final String SOURCE = "Line 1\r\nLine 2";
	private static final String RESULT = "<div class=\"text-plain\">Line 1<br/>Line 2</div>";
	
	@Test
	public void testFormat() throws Exception
	{
		String result = ContentUtils.format("text/plain", new InputSource(new StringReader(SOURCE)), new TextFilter());
		result = result.replaceAll("\r", "");
		result = result.replaceAll("\n", "");
		assertEquals(RESULT, result);
	}

	@Test
	public void testFormatText() throws Exception
	{
		String result = ContentUtils.formatText(SOURCE, ContentType.TEXT, new TextFilter());
		result = result.replaceAll("\r", "");
		result = result.replaceAll("\n", "");
		assertEquals(RESULT, result);		
	}

	@Test
	public void testFormatNoTemplates() throws Exception
	{
		ContentUtils.format("bogus", new InputSource(new StringReader(SOURCE)), new ContentFilterMock());
		ContentUtils.formatText("bogus", ContentType.TEXT, new ContentFilterMock());
		
	}

	@Test
	public void testFormatTextNoTemplates() throws Exception
	{
		ContentUtils.formatText("bogus", ContentType.TEXT, new ContentFilterMock());		
	}
	
	/**
	 * Not a test, but tickles the private constructor.
	 */
	@Test
	public void coverDefaultConstructor() throws Exception
	{
		Constructor c = ContentUtils.class.getDeclaredConstructor(new Class[] {});
		c.setAccessible(true);
		c.newInstance(new Object[] {});
	}	
}
