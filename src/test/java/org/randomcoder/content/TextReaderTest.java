package org.randomcoder.content;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;

public class TextReaderTest {
  private static final String TEST_RESOURCE = "/text-reader.txt";
  private static final String EXPECTED_DATA =
      "<text><line>First paragraph.</line><line></line><line>Second paragraph.</line></text>";

  private TextReader reader;
  private TextContentHandler handler;

  @Before public void setUp() {
    handler = new TextContentHandler();
    reader = new TextReader();
    reader.setContentHandler(handler);
    reader.setErrorHandler(handler);
  }

  @After public void tearDown() {
    reader = null;
    handler = null;
  }

  @Test(expected = SAXException.class) public void testParseNoSource()
      throws Exception {
    InputSource source = new InputSource();
    reader.parse(source);
  }

  @Test public void testParseInputStream() throws Exception {
    InputSource source = new InputSource();
    source.setByteStream(getClass().getResourceAsStream(TEST_RESOURCE));
    reader.parse(source);
    assertEquals("Wrong data", EXPECTED_DATA, handler.getData());
  }

  @Test public void testParseReader() throws Exception {
    InputSource source = new InputSource();
    source.setCharacterStream(
        new InputStreamReader(getClass().getResourceAsStream(TEST_RESOURCE)));
    reader.parse(source);
    assertEquals("Wrong data", EXPECTED_DATA, handler.getData());
  }

  @Test public void testParseSystemId() throws Exception {
    InputSource source = new InputSource();
    source.setSystemId(getClass().getResource(TEST_RESOURCE).toExternalForm());
    reader.parse(source);
    assertEquals("Wrong data", EXPECTED_DATA, handler.getData());
  }

  /**
   * Super-simple psuedo-XML handler (doesn't do attributes, etc.)
   */
  static class TextContentHandler extends DefaultHandler {
    private final StringBuilder buf = new StringBuilder();

    public String getData() {
      return buf.toString();
    }

    @Override public void characters(char[] ch, int start, int length)
        throws SAXException {
      buf.append(ch, start, length);
    }

    @Override public void endElement(String uri, String localName, String qName)
        throws SAXException {
      buf.append("</");
      buf.append(localName);
      buf.append(">");
    }

    @Override public void startDocument() throws SAXException {
      buf.setLength(0);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
        Attributes attributes) throws SAXException {
      buf.append("<");
      buf.append(localName);
      buf.append(">");
    }

  }
}
