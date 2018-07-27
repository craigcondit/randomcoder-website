package org.randomcoder.content;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ContentTypeTest {
	@Test
	public void testGetMimeType() {
		assertEquals("text/plain", ContentType.TEXT.getMimeType());
		assertEquals("application/xhtml+xml", ContentType.XHTML.getMimeType());
	}

	@Test
	public void testGetDescription() {
		assertEquals("Plain text", ContentType.TEXT.getDescription());
		assertEquals("XHTML", ContentType.XHTML.getDescription());
	}

	@Test
	public void testGetName() {
		assertEquals("TEXT", ContentType.TEXT.getName());
		assertEquals("XHTML", ContentType.XHTML.getName());
	}

	@Test
	public void testGetOrdinal() {
		assertEquals(0, ContentType.TEXT.getOrdinal());
		assertEquals(1, ContentType.XHTML.getOrdinal());
	}

	@Test
	public void testValueOf() {
		assertEquals(ContentType.TEXT, ContentType.valueOf("TEXT"));
		assertEquals(ContentType.XHTML, ContentType.valueOf("XHTML"));
	}
}
