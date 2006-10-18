package com.randomcoder.feed;

import java.io.IOException;

import org.xml.sax.*;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * RSS feed parser.
 * 
 * <p>TODO Finish this up, including javadocs...</p>
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
public class RSSParser extends DefaultHandler2
{
	private static final String RSS_0_90_SAMPLE = "sample-0.90.xml";
	private static final String RSS_0_91_SAMPLE = "sample-0.91.xml";
	private static final String RSS_1_0_SAMPLE = "sample-1.0.xml";
	private static final String RSS_2_0_SAMPLE = "sample-2.0.xml";
	private static final String GOOGLE_SAMPLE = "google.xml";
	private static final String YAHOO_SAMPLE = "yahoo.xml";
	private static final String NEWS_SAMPLE = "news.xml";
	private static final String SLASHDOT_SAMPLE = "slashdot.xml";
	private static final String DIGG_SAMPLE = "digg.xml";
	private static final String AJAXIAN_SAMPLE = "ajaxian.xml";
	private static final String ALISTAPART_SAMPLE = "alistapart.xml";
	private static final String developerWorks_SAMPLE = "developerWorks.xml";
	private static final String JAVALOBBY_SAMPLE = "javalobby.xml";
	private static final String ONJAVA_SAMPLE = "onjava.xml";
	private static final String SIMPLEBITS_SAMPLE = "simplebits.xml";
	private static final String WOOT_SAMPLE = "woot.xml";

	private static final String RDF_NAMESPACE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private static final String RSS_0_90_NAMESPACE_URI = "http://my.netscape.com/rdf/simple/0.9/";
	private static final String RSS_1_0_NAMESPACE_URI = "http://purl.org/rss/1.0/";

	private String format;
	private String version;

	@Override
	public void notationDecl(String name, String publicId, String systemId) throws SAXException
	{
		System.out.println("notationDecl(name=" + name + ",publicId=" + publicId + ",systemId=" + systemId + ")");
		super.notationDecl(name, publicId, systemId);
	}

	@Override
	public void processingInstruction(String target, String data) throws SAXException
	{
		System.out.println("processingInstruction(target=" + target + ",data=" + data + ")");
		super.processingInstruction(target, data);
	}

	@Override
	public void startDocument() throws SAXException
	{
		System.out.println("startDocument()");
		format = null;
		version = null;
	}

	@Override
	public void endDocument() throws SAXException
	{
		System.out.println("endDocument()");
		System.out.println("  Detected: " + format + " " + version);

		format = null;
		version = null;
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException
	{
		System.out.println("resolveEntity(publicId=" + publicId + ",systemId=" + systemId + ")");

		return super.resolveEntity(publicId, systemId);
	}

	@Override
	public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException
	{
		System.out.println("unparsedEntityDecl(name=" + name + ",publicId=" + publicId + ",systemId=" + systemId + ",notationName=" + notationName + ")");
		super.unparsedEntityDecl(name, publicId, systemId, notationName);
	}

	private void updateVersion(String uri, String localName, Attributes attributes)
	{
		// determine format
		if (format == null)
		{
			if (RDF_NAMESPACE_URI.equals(uri) && "RDF".equals(localName))
			{
				format = "RDF";
			}
			if ("".equals(uri) && "rss".equals(localName))
			{
				format = "RSS";
				if ("0.91".equals(attributes.getValue("version")))
				{
					version = "0.91";
				}
				if ("2.0".equals(attributes.getValue("version")))
				{
					version = "2.0";
				}
			}
		}

		// determine version
		if (version == null)
		{
			if ("RDF".equals(format) && RSS_0_90_NAMESPACE_URI.equals(uri))
			{
				version = "0.90";
			}
			if ("RDF".equals(format) && RSS_1_0_NAMESPACE_URI.equals(uri))
			{
				version = "1.0";
			}
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		System.out.println("startElement(uri=" + uri + ",localName=" + localName + ",qname=" + qName + ")");

		// update version if we haven't gotten it yet
		if (format == null || version == null)
			updateVersion(uri, localName, attributes);

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		System.out.println("endElement(uri=" + uri + ",localName=" + localName + ",qname=" + qName + ")");
	}

	@Override
	public void skippedEntity(String name) throws SAXException
	{
		System.out.println("skippedEntity(name=" + name + ")");
		super.skippedEntity(name);
	}

	@Override
	public void startEntity(String name) throws SAXException
	{
		System.out.println("startEntity(name=" + name + ")");
		super.startEntity(name);
	}

	@Override
	public void endEntity(String name) throws SAXException
	{
		System.out.println("endEntity(name=" + name + ")");
		super.endEntity(name);
	}

	@Override
	public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException
	{
		System.out.println("externalEntityDecl(name=" + name + ",publicId=" + publicId + ",systemId=" + systemId + ")");
		super.externalEntityDecl(name, publicId, systemId);
	}

	@Override
	public void internalEntityDecl(String name, String value) throws SAXException
	{
		System.out.println("internalEntityDecl(name=" + name + ",value=" + value + ")");
		super.internalEntityDecl(name, value);
	}

	public static void main(String[] args)
	{
		try
		{
			RSSParser parser = new RSSParser();

			XMLReader reader = XMLReaderFactory.createXMLReader();

			reader.setFeature("http://xml.org/sax/features/namespaces", true);
			reader.setContentHandler(parser);
			reader.setEntityResolver(parser);
			reader.setErrorHandler(parser);
			reader.setDTDHandler(parser);

			reader.parse(new InputSource(RSSParser.class.getResourceAsStream(WOOT_SAMPLE)));

		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
}
