package org.randomcoder.content;

import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

@SuppressWarnings("javadoc")
public class TextReaderTest extends TestCase
{
	private static final String TEST_RESOURCE = "/text-reader.txt";
	private static final String EXPECTED_DATA = "<text><line>First paragraph.</line><line></line><line>Second paragraph.</line></text>";
	
	private TextReader reader;
	private TextContentHandler handler;
	
	@Override
	protected void setUp() throws Exception
	{
		handler = new TextContentHandler();
		reader = new TextReader();
		reader.setContentHandler(handler);
		reader.setErrorHandler(handler);
	}

	@Override
	protected void tearDown() throws Exception
	{
		reader = null;
		handler = null;
	}

	public void testParseNoSource() throws Exception
	{
		try
		{
			InputSource source = new InputSource();
			reader.parse(source);
			fail("No exception thrown");
		}
		catch (SAXException e)
		{
			// pass
		}
	}
	
	public void testParseInputStream() throws Exception
	{
		InputSource source = new InputSource();
		source.setByteStream(getClass().getResourceAsStream(TEST_RESOURCE));
		reader.parse(source);
		assertEquals("Wrong data", EXPECTED_DATA, handler.getData());
	}

	public void testParseReader() throws Exception
	{
		InputSource source = new InputSource();
		source.setCharacterStream(new InputStreamReader(getClass().getResourceAsStream(TEST_RESOURCE)));
		reader.parse(source);
		assertEquals("Wrong data", EXPECTED_DATA, handler.getData());
	}

	public void testParseSystemId() throws Exception
	{
		InputSource source = new InputSource();
		source.setSystemId(getClass().getResource(TEST_RESOURCE).toExternalForm());
		reader.parse(source);
		assertEquals("Wrong data", EXPECTED_DATA, handler.getData());
	}
	
	/**
	 * Super-simple psuedo-XML handler (doesn't do attributes, etc.)
	 */
	static class TextContentHandler extends DefaultHandler
	{
		private final StringBuilder buf = new StringBuilder();

		public String getData() { return buf.toString(); }
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			buf.append(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			buf.append("</");
			buf.append(localName);
			buf.append(">");
		}

		@Override
		public void startDocument() throws SAXException
		{
			buf.setLength(0);
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			buf.append("<");
			buf.append(localName);
			buf.append(">");
		}
		
	}
}
