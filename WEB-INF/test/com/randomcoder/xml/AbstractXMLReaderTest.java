package com.randomcoder.xml;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class AbstractXMLReaderTest
{
	private AbstractXMLReaderMock reader;
	
	@Before
	public void setUp() throws Exception
	{
		reader = new AbstractXMLReaderMock();
	}

	@After
	public void tearDown() throws Exception
	{
		reader = null;
	}

	@Test
	public void testParse() throws SAXException, IOException
	{
		reader.parse("test/data/AbstractXmlReaderTest.xml");
		assertEquals("test/data/AbstractXmlReaderTest.xml", reader.getInputSource().getSystemId());		
	}

	@Test
	public void testGetContentHandler()
	{
		DefaultHandler handler = new DefaultHandler();
		reader.setContentHandler(handler);
		assertEquals(handler, reader.getContentHandler());
	}

	@Test
	public void testGetDTDHandler()
	{
		DefaultHandler handler = new DefaultHandler();
		reader.setDTDHandler(handler);
		assertEquals(handler, reader.getDTDHandler());
	}

	@Test
	public void testGetEntityResolver()
	{
		DefaultHandler handler = new DefaultHandler();
		reader.setEntityResolver(handler);
		assertEquals(handler, reader.getEntityResolver());
	}

	@Test
	public void testGetErrorHandler()
	{
		DefaultHandler handler = new DefaultHandler();
		reader.setErrorHandler(handler);
		assertEquals(handler, reader.getErrorHandler());
	}

	@Test
	public void testGetFeature() throws SAXException
	{
		reader.setFeature("test-feature", true);
		assertTrue(reader.getFeature("test-feature"));
		assertFalse(reader.getFeature("bogus-feature"));
	}

	@Test
	public void testGetProperty() throws SAXException
	{
		reader.setProperty("test-property", "test-value");
		assertEquals("test-value", reader.getProperty("test-property"));
		assertNull(reader.getProperty("bogus-property"));
	}

	protected class AbstractXMLReaderMock extends AbstractXMLReader
	{
		private InputSource input;
		
		@Override
		public void parse(InputSource _input) throws IOException, SAXException
		{
			input = _input;
		}		
		
		public InputSource getInputSource() { return input; }
	}
}
