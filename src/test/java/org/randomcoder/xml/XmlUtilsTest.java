package org.randomcoder.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtilsTest {
	private static final String XML_VALID_DOCUMENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
			"<Test value='test'><Entry value='1' /><Entry value='2' /></Test>";

	private static final String XML_INVALID_DOCUMENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
			"<Test value='test'>";

	private static final String PUBLIC_ID = "Public ID";
	private static final String SYSTEM_ID = "system.dtd";

	private Logger log;

	@Before
	public void setUp() {
		log = LoggerFactory.getLogger("test");
	}

	@After
	public void tearDown() {
		log = null;
	}

	@Test
	public void testParseXmlValid() throws Exception {
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));
		Document doc = XmlUtils.parseXml(source);
		assertNotNull(doc);

		NodeList list = doc.getElementsByTagName("Entry");
		assertEquals(2, list.getLength());
	}

	@Test(expected = SAXException.class)
	public void testParseXmlInvalid() throws Exception {
		InputSource source = new InputSource(new StringReader(XML_INVALID_DOCUMENT));
		XmlUtils.parseXml(source);
	}

	@Test
	public void testWriteXml() throws Exception {
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));
		Document doc = XmlUtils.parseXml(source);

		StringWriter writer = new StringWriter();
		XmlUtils.writeXml(doc, new StreamResult(writer), PUBLIC_ID, SYSTEM_ID);
		writer.close();
		String xml = writer.getBuffer().toString();

		assertTrue(xml.contains("<Test"));
	}

	@Test
	public void testLogXml() throws Exception {
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));
		Document doc = XmlUtils.parseXml(source);
		XmlUtils.logXml(log, doc);
	}

	@Test
	public void testLogXmlDtd() throws Exception {
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));
		Document doc = XmlUtils.parseXml(source);
		XmlUtils.logXml(log, doc, PUBLIC_ID, SYSTEM_ID);
	}

	@Test
	public void testLogXmlLogMessage() throws Exception {
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));
		Document doc = XmlUtils.parseXml(source);
		XmlUtils.logXml(log, "test-message", doc);
	}

	@Test
	public void testLogXmlLogAll() throws Exception {
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));
		Document doc = XmlUtils.parseXml(source);
		XmlUtils.logXml(log, "test-message", doc, PUBLIC_ID, SYSTEM_ID);
	}

	@Test
	public void testPrettyPrint() throws Exception {
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));
		Document doc = XmlUtils.parseXml(source);

		StringWriter writer = new StringWriter();
		XmlUtils.prettyPrint(doc, new StreamResult(writer));
		writer.close();
		String xml = writer.getBuffer().toString();

		assertTrue(xml.contains("\n  <Entry"));
	}

	@Test
	public void testPrettyPrintAll() throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		DocumentType dtd = builder.getDOMImplementation().createDocumentType("Test", PUBLIC_ID, SYSTEM_ID);

		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));
		Document doc = XmlUtils.parseXml(source);

		StringWriter writer = new StringWriter();
		XmlUtils.prettyPrint(doc, new StreamResult(writer), dtd);
		writer.close();
		String xml = writer.getBuffer().toString();

		assertTrue(xml.contains("\n  <Entry"));
		assertTrue(xml.contains(PUBLIC_ID));
		assertTrue(xml.contains(SYSTEM_ID));
	}
}
