package com.randomcoder.content;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Test;
import org.xml.sax.InputSource;

public class ContentUtilsTest
{
	private static final String SOURCE = "Line 1\r\nLine 2";
	private static final String RESULT = "<div class=\"text-plain\">\r\nLine 1<br/>\r\nLine 2\r\n</div>\r\n";
	
	@Test
	public void testFormat() throws Exception
	{
		assertEquals(RESULT, ContentUtils.format("text/plain", new InputSource(new StringReader(SOURCE)), new TextFilter()));
	}

	@Test
	public void testFormatText() throws Exception
	{
		assertEquals(RESULT, ContentUtils.formatText(SOURCE, ContentType.TEXT, new TextFilter()));
	}
}
