package org.randomcoder.xml;

import static org.junit.Assert.*;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.*;
import org.junit.*;
import org.w3c.dom.*;
import org.xml.sax.*;

@SuppressWarnings("javadoc")
public class XmlUtilsTest
{
	private static final String XML_VALID_DOCUMENT =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
					"<Test value='test'><Entry value='1' /><Entry value='2' /></Test>";

	private static final String XML_INVALID_DOCUMENT =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
					"<Test value='test'>";

	private static final String PUBLIC_ID = "Public ID";
	private static final String SYSTEM_ID = "system.dtd";

	private Log log;

	@Before
	public void setUp()
	{
		log = LogFactory.getLog("test");
	}

	@After
	public void tearDown()
	{
		log = null;
	}

	@Test
	public void testParseXmlValid() throws Exception
	{
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));
		Document doc = XmlUtils.parseXml(source);
		assertNotNull(doc);

		NodeList list = doc.getElementsByTagName("Entry");
		assertEquals(2, list.getLength());
	}

	@Test(expected = SAXException.class)
	public void testParseXmlInvalid() throws Exception
	{
		InputSource source = new InputSource(new StringReader(XML_INVALID_DOCUMENT));
		XmlUtils.parseXml(source);
	}

	@Test
	public void testWriteXml() throws Exception
	{
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));
		Document doc = XmlUtils.parseXml(source);

		StringWriter writer = new StringWriter();
		XmlUtils.writeXml(doc, new StreamResult(writer), PUBLIC_ID, SYSTEM_ID);
		writer.close();
		String xml = writer.getBuffer().toString();

		assertTrue(xml.contains("<Test"));
	}

	@Test
	public void testLogXml() throws Exception
	{
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));
		Document doc = XmlUtils.parseXml(source);
		XmlUtils.logXml(log, doc);
	}

	@Test
	public void testLogXmlDtd() throws Exception
	{
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));
		Document doc = XmlUtils.parseXml(source);
		XmlUtils.logXml(log, doc, PUBLIC_ID, SYSTEM_ID);
	}

	@Test
	public void testLogXmlLogMessage() throws Exception
	{
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));
		Document doc = XmlUtils.parseXml(source);
		XmlUtils.logXml(log, "test-message", doc);
	}

	@Test
	public void testLogXmlLogAll() throws Exception
	{
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));
		Document doc = XmlUtils.parseXml(source);
		XmlUtils.logXml(log, "test-message", doc, PUBLIC_ID, SYSTEM_ID);
	}

	@Test
	public void testPrettyPrint() throws Exception
	{
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));
		Document doc = XmlUtils.parseXml(source);

		StringWriter writer = new StringWriter();
		XmlUtils.prettyPrint(doc, new StreamResult(writer));
		writer.close();
		String xml = writer.getBuffer().toString();

		assertTrue(xml.contains("\n  <Entry"));
	}

	@Test
	public void testPrettyPrintAll() throws Exception
	{
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
