package com.randomcoder.xml;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.apache.commons.logging.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class XmlUtilsTest extends TestCase
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
	
	@Override
	public void setUp() throws Exception
	{
		log = LogFactory.getLog("test");		
	}

	@Override
	public void tearDown() throws Exception
	{
		log = null;
	}

	public void testParseXmlValid() throws Exception
	{
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));		
		Document doc = XmlUtils.parseXml(source);		
		assertNotNull(doc);
		
		NodeList list = doc.getElementsByTagName("Entry");
		assertEquals(2, list.getLength());
	}

	public void testParseXmlInvalid() throws Exception
	{
		try
		{
			InputSource source = new InputSource(new StringReader(XML_INVALID_DOCUMENT));		
			XmlUtils.parseXml(source);
			fail("SAXException expected");
		}
		catch (SAXException e)
		{
			// pass
		}
	}

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

	public void testLogXml() throws Exception
	{
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));		
		Document doc = XmlUtils.parseXml(source);
		XmlUtils.logXml(log, doc);
	}

	public void testLogXmlDtd() throws Exception
	{
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));		
		Document doc = XmlUtils.parseXml(source);
		XmlUtils.logXml(log, doc, PUBLIC_ID, SYSTEM_ID);
	}

	public void testLogXmlLogMessage() throws Exception
	{
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));		
		Document doc = XmlUtils.parseXml(source);
		XmlUtils.logXml(log, "test-message", doc);
	}

	public void testLogXmlLogAll() throws Exception
	{
		InputSource source = new InputSource(new StringReader(XML_VALID_DOCUMENT));		
		Document doc = XmlUtils.parseXml(source);
		XmlUtils.logXml(log, "test-message", doc, PUBLIC_ID, SYSTEM_ID);
	}

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
