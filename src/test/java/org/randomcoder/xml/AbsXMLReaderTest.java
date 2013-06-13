package org.randomcoder.xml;

import java.io.IOException;

import junit.framework.TestCase;

import org.randomcoder.test.mock.xml.AbstractXMLReaderMock;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@SuppressWarnings("javadoc")
public class AbsXMLReaderTest extends TestCase
{
	private AbstractXMLReaderMock reader;
	
	@Override
	public void setUp() throws Exception
	{
		reader = new AbstractXMLReaderMock();
	}

	@Override
	public void tearDown() throws Exception
	{
		reader = null;
	}

	public void testParse() throws SAXException, IOException
	{
		reader.parse("test.xml");
		assertEquals("test.xml", reader.getInputSource().getSystemId());		
	}

	public void testGetContentHandler()
	{
		DefaultHandler handler = new DefaultHandler();
		reader.setContentHandler(handler);
		assertEquals(handler, reader.getContentHandler());
	}

	public void testGetDTDHandler()
	{
		DefaultHandler handler = new DefaultHandler();
		reader.setDTDHandler(handler);
		assertEquals(handler, reader.getDTDHandler());
	}

	public void testGetEntityResolver()
	{
		DefaultHandler handler = new DefaultHandler();
		reader.setEntityResolver(handler);
		assertEquals(handler, reader.getEntityResolver());
	}

	public void testGetErrorHandler()
	{
		DefaultHandler handler = new DefaultHandler();
		reader.setErrorHandler(handler);
		assertEquals(handler, reader.getErrorHandler());
	}

	public void testGetFeature() throws SAXException
	{
		reader.setFeature("test-feature", true);
		assertTrue(reader.getFeature("test-feature"));
		assertFalse(reader.getFeature("bogus-feature"));
	}

	public void testGetProperty() throws SAXException
	{
		reader.setProperty("test-property", "test-value");
		assertEquals("test-value", reader.getProperty("test-property"));
		assertNull(reader.getProperty("bogus-property"));
	}
}
