package com.randomcoder.content;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Test;
import org.xml.sax.InputSource;

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
}
