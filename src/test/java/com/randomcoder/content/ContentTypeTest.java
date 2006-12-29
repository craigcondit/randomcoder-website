package com.randomcoder.content;

import junit.framework.TestCase;

public class ContentTypeTest extends TestCase
{
	public void testGetMimeType()
	{
		assertEquals("text/plain", ContentType.TEXT.getMimeType());
		assertEquals("application/xhtml+xml", ContentType.XHTML.getMimeType());
	}

	public void testGetDescription()
	{
		assertEquals("Plain text", ContentType.TEXT.getDescription());
		assertEquals("XHTML", ContentType.XHTML.getDescription());
	}

	public void testGetName()
	{
		assertEquals("TEXT", ContentType.TEXT.getName());
		assertEquals("XHTML", ContentType.XHTML.getName());
	}

	public void testGetOrdinal()
	{
		assertEquals(0,ContentType.TEXT.getOrdinal());
		assertEquals(1,ContentType.XHTML.getOrdinal());
	}
	
	public void testValueOf()
	{
		assertEquals(ContentType.TEXT, ContentType.valueOf("TEXT"));
		assertEquals(ContentType.XHTML, ContentType.valueOf("XHTML"));		
	}
}