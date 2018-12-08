package org.randomcoder.content;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class XHTMLReaderTest {
  private static final String TEST_PREFIX = "<html><body>";
  private static final String TEST_SUFFIX = "</body></html>";

  private XHTMLReader reader;
  private MockContentHandler handler;

  @Before public void setUp() throws Exception {
    handler = new MockContentHandler();
    Set<String> allowedClasses = new HashSet<String>();
    allowedClasses.add("allowed");
    allowedClasses.add("allowed2");
    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);
    XMLReader xmlReader = spf.newSAXParser().getXMLReader();
    reader = new XHTMLReader(xmlReader, allowedClasses, null);
    reader.setContentHandler(handler);
    reader.setErrorHandler(handler);
  }

  @After public void tearDown() {
    reader = null;
    handler = null;
  }

  private InputSource buildInputSource(String text) {
    return new InputSource(
        new StringReader(XHTMLFilter.PREFIX + text + XHTMLFilter.SUFFIX));
  }

  private InputSource buildRawInputSource(String text) {
    return new InputSource(new StringReader(text));
  }

  @Test public void testMissingRoot() throws Exception {
    InputSource source = buildRawInputSource("<strong>Test</strong>");
    reader.parse(source);
    assertEquals(TEST_PREFIX + "<strong>Test</strong>" + TEST_SUFFIX,
        handler.getData());
  }

  @Test public void testAllowedClass() throws Exception {
    InputSource source =
        buildInputSource("<strong class='allowed'>Test</strong>");
    reader.parse(source);
    assertEquals(
        TEST_PREFIX + "<strong class=\"allowed\">Test</strong>" + TEST_SUFFIX,
        handler.getData());
  }

  @Test public void testDisallowedClass() throws Exception {
    InputSource source =
        buildInputSource("<strong class='bogus'>Test</strong>");
    reader.parse(source);
    assertEquals(TEST_PREFIX + "<strong>Test</strong>" + TEST_SUFFIX,
        handler.getData());
  }

  @Test public void testMixedClass() throws Exception {
    InputSource source = buildInputSource(
        "<strong class='bogus allowed allowed2'>Test</strong>");
    reader.parse(source);
    assertEquals(
        TEST_PREFIX + "<strong class=\"allowed allowed2\">Test</strong>"
            + TEST_SUFFIX, handler.getData());
  }

  @Test public void testAllowedAttribute() throws Exception {
    InputSource source = buildInputSource("<span title='test'>Test</span>");
    reader.parse(source);
    assertEquals(TEST_PREFIX + "<span title=\"test\">Test</span>" + TEST_SUFFIX,
        handler.getData());
  }

  @Test public void testAllowedAttributeByTag() throws Exception {
    InputSource source =
        buildInputSource("<del datetime='20070101'>Test</del>");
    reader.parse(source);
    assertEquals(
        TEST_PREFIX + "<del datetime=\"20070101\">Test</del>" + TEST_SUFFIX,
        handler.getData());
  }

  @Test public void testDisallowedAttribute() throws Exception {
    InputSource source = buildInputSource("<span bogus='bogus'>Test</span>");
    reader.parse(source);
    assertEquals(TEST_PREFIX + "<span>Test</span>" + TEST_SUFFIX,
        handler.getData());
  }

  @Test public void testDisallowedAttributeByTag() throws Exception {
    InputSource source =
        buildRawInputSource("<html bogus='test'><body>Test</body></html>");
    reader.parse(source);
    assertEquals(TEST_PREFIX + "Test" + TEST_SUFFIX, handler.getData());
  }

  @Test public void testUrlAttributeAllowed() throws Exception {
    InputSource source =
        buildInputSource("<a href='http://localhost/'>Test</a>");
    reader.parse(source);
    assertEquals(
        TEST_PREFIX + "<a href=\"http://localhost/\">Test</a>" + TEST_SUFFIX,
        handler.getData());
  }

  @Test public void testUrlAttributeAllowedByTag() throws Exception {
    InputSource source = buildInputSource(
        "<img src='http://localhost/test.jpg' longdesc='http://localhost/' />");
    reader.parse(source);
    assertEquals(TEST_PREFIX
        + "<img longdesc=\"http://localhost/\" src=\"http://localhost/test.jpg\"></img>"
        + TEST_SUFFIX, handler.getData());
  }

  @Test public void testInvalidUrlProtocol() throws Exception {
    InputSource source =
        buildInputSource("<a href='gopher://localhost/'>Test</a>");
    reader.parse(source);
    assertEquals(TEST_PREFIX + "<a href=\"#\">Test</a>" + TEST_SUFFIX,
        handler.getData());
  }

  @Test public void testMalformedUrl() throws Exception {
    InputSource source = buildInputSource("<a href='badproto:test'>Test</a>");
    reader.parse(source);
    assertEquals(TEST_PREFIX + "<a href=\"#\">Test</a>" + TEST_SUFFIX,
        handler.getData());
  }

  @Test public void testReplacedElement() throws Exception {
    InputSource source = buildInputSource("<b>Test</b>");
    reader.parse(source);
    assertEquals(TEST_PREFIX + "<strong>Test</strong>" + TEST_SUFFIX,
        handler.getData());
  }

  @Test public void testProcessingInstruction() throws Exception {
    InputSource source = buildRawInputSource(
        "<?xml-stylesheet href='test.css' type='text/css' ?><html><body>Test</body></html>");
    reader.parse(source);
    assertEquals(TEST_PREFIX + "Test" + TEST_SUFFIX, handler.getData());
  }

  @Test(expected = SAXParseException.class) public void testUnparsedEntityDecl()
      throws Exception {
    InputSource source = buildInputSource("Test&nbsp;");
    reader.parse(source);
  }

  /**
   * Super-simple psuedo-XML handler. Doesn't produce valid XML, but is
   * deterministic.
   */
  static class MockContentHandler extends DefaultHandler {
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

      List<String> attNames = new ArrayList<String>();

      for (int i = 0; i < attributes.getLength(); i++) {
        attNames.add(
            " " + attributes.getLocalName(i) + "=\"" + attributes.getValue(i)
                + "\"");
      }
      Collections.sort(attNames);
      for (String att : attNames)
        buf.append(att);

      buf.append(">");
    }
  }
}
