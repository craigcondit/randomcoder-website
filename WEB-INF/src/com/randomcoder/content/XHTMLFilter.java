package com.randomcoder.content;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * Simple XHTML content filter.
 * 
 * <p>This class implements a large subset of XHTML (minus dangerous,
 * deprecated, or otherwise undesirable stuff). Tag and attribute names are
 * canonicalized, non-semantic markup is converted to semantic, and disallowed
 * elements, their children, and attributes are removed.</p>
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
public class XHTMLFilter implements ContentFilter
{
	protected static final Log logger = LogFactory.getLog(XHTMLFilter.class);
	
	private static final String XSL_RESOURCE = "xhtml-to-xhtml.xsl";
	private static final String XSD_RESOURCE = "xhtml1-transitional.xsd";
	private static final String NS_RESOURCE = "namespace.xsd";

	private static final String PREFIX = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>Untitled</title></head><body>";
	private static final String SUFFIX = "</body></html>";

	private final Templates templates;
	private final Schema schema;

	private Set<String> allowedClasses = new HashSet<String>();

	/**
	 * Sets a list of allowed CSS class names in the markup.
	 * @param allowedClasses Set of CSS class names
	 */
	@Required
	public void setAllowedClasses(Set<String> allowedClasses)
	{
		this.allowedClasses = allowedClasses;
	}

	/**
	 * Constructs a new XHTML filter
	 * @throws TransformerConfigurationException if transformer factory fails
	 * @throws SAXException if schema validation fails
	 */
	public XHTMLFilter() throws TransformerConfigurationException, SAXException
	{
		// cache templates for later use
		TransformerFactory tFactory = TransformerFactory.newInstance();
		templates = tFactory.newTemplates(new SAXSource(new InputSource(getClass().getResourceAsStream(XSL_RESOURCE))));

		SchemaFactory sFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Source xhtmlSource = new StreamSource(getClass().getResourceAsStream(XSD_RESOURCE));
		Source nsSource = new StreamSource(getClass().getResourceAsStream(NS_RESOURCE));

		schema = sFactory.newSchema(new Source[] { nsSource, xhtmlSource });
	}

	public XMLReader getXMLReader(String contentType) throws SAXException
	{
		return new XHTMLReader(XMLReaderFactory.createXMLReader(), allowedClasses);
	}

	public Templates getXSLTemplates(String contentType)
	{
		return templates;
	}

	public String getPrefix(String contentType)
	{
		return PREFIX;
	}

	public String getSuffix(String contentType)
	{
		return SUFFIX;
	}

	public void validate(String contentType, Reader content) throws InvalidContentException, InvalidContentTypeException, IOException
	{
		Validator validator = schema.newValidator();
		XHTMLErrorHandler handler = new XHTMLErrorHandler();
		validator.setErrorHandler(handler);

		try
		{
			validator.validate(new StreamSource(content));
		}
		catch (SAXException e)
		{
			if (handler.getMessage() != null)
			{
				// we caught it
				throw new InvalidContentException(handler.getMessage(), handler.getLineNumber(), handler.getColumnNumber());
			}
			// something else bad happened
			throw new InvalidContentException(e.getMessage(), 1, 1);
		}
	}

	/**
	 * {@link ErrorHandler} implementation used to derive line and column numbers.
	 */
	static class XHTMLErrorHandler implements ErrorHandler
	{
		private int lineNumber = 1;
		private int columnNumber = 1;
		private String message = null;

		/**
		 * Gets the line number where processing failed.
		 * @return line number
		 */
		public int getLineNumber()
		{
			return lineNumber;
		}

		/**
		 * Gets the column number where processing failed.
		 * @return column number
		 */
		public int getColumnNumber()
		{
			return columnNumber;
		}

		/**
		 * Gets the error message.
		 * @return error message
		 */
		public String getMessage()
		{
			return message;
		}

		private void handle(SAXParseException ex)
		{
			lineNumber = ex.getLineNumber();
			columnNumber = ex.getColumnNumber();
			message = ex.getMessage();

			// account for prefix
			if (lineNumber == 1)
				columnNumber -= PREFIX.length();
		}

		public void error(SAXParseException ex) throws SAXException
		{
			handle(ex);
			throw ex;
		}

		public void fatalError(SAXParseException ex) throws SAXException
		{
			handle(ex);
			throw ex;
		}

		public void warning(SAXParseException ex) throws SAXException
		{
			handle(ex);
			throw ex;
		}
	}

	/**
	 * {@link XMLFilter} implementation which filters out dangerous and/or
	 * unwanted markup from the input XML.
	 */
	static class XHTMLReader extends XMLFilterImpl
	{
		private static final Attributes NO_ATTRIBUTES = new AttributesImpl();
		private static final String HTML = "html".toLowerCase(Locale.US);

		private static final Set<String> ALLOWED_TAGS;
		static
		{
			String[] tags =
			{
					"a", "abbr", "acronym", "address", "bdo", "big", "blockquote",
					"body", "br", "caption", "cite", "code", "colgroup", "dd", "del",
					"dfn", "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6",
					"hr", "html", "img", "ins", "kbd", "li", "ol", "p", "pre", "q",
					"samp", "small", "span", "strong", "sub", "sup", "table", "tbody",
					"td", "tfoot", "th", "thead", "tr", "tt", "ul", "var"
			};

			Set<String> set = new HashSet<String>(tags.length);
			for (String tag : tags)
				set.add(tag.toLowerCase(Locale.US));
			ALLOWED_TAGS = Collections.unmodifiableSet(set);
		}

		private static final Map<String, String> REPLACED_TAGS;
		static
		{
			String[][] tags =
			{
					{ "b", "strong" },
					{ "i", "em" },
					{ "s", "del" },
					{ "strike", "del" },
					{ "u", "em" }
			};
			HashMap<String, String> map = new HashMap<String, String>(tags.length);
			for (String[] entry : tags)
				map.put(entry[0].toLowerCase(Locale.US), entry[1].toLowerCase(Locale.US));
			REPLACED_TAGS = Collections.unmodifiableMap(map);
		}

		private static final Set<String> ALLOWED_ATTRIBUTES;
		static
		{
			String[] atts =
			{
					"*.dir", "*.lang", "*.title", "a.href", "a.charset", "a.hreflang",
					"a.type", "blockquote.cite", "body.-", "colgroup.align",
					"colgroup.char", "colgroup.charoff", "colgroup.span",
					"colgroup.valign", "colgroup.width", "del.cite", "del.datetime",
					"html.-", "img.alt", "img.src", "img.height", "img.longdesc",
					"img.width", "ins.cite", "ins.datetime", "pre.width", "q.cite",
					"table.border", "table.cellpadding", "table.cellspacing",
					"table.frame", "table.rules", "table.summary", "table.width",
					"tbody.align", "tbody.char", "tbody.charoff", "tbody.valign",
					"td.abbr", "td.align", "td.axis", "td.char", "td.charoff",
					"td.colspan", "td.headers", "td.rowspan", "td.scope", "td.valign",
					"tfoot.align", "tfoot.char", "tfoot.charoff", "tfoot.valign",
					"th.abbr", "th.align", "th.axis", "th.char", "th.charoff",
					"th.colspan", "th.headers", "th.rowspan", "th.scope", "th.valign",
					"thead.align", "thead.char", "thead.charoff", "thead.valign",
					"tr.align", "tr.char", "tr.charoff", "tr.valign"
			};
			Set<String> set = new HashSet<String>(atts.length);
			for (String att : atts)
				set.add(att.toLowerCase(Locale.US));
			ALLOWED_ATTRIBUTES = Collections.unmodifiableSet(set);
		}
		
		private static final Set<String> URL_ATTRIBUTES;
		static
		{
			String[] atts =
			{
					"*.href", "*.src", "*.cite", "*.xmlns", "body.background",
					"form.action", "frame.longdesc", "head.profile", "img.ismap",
					"img.longdesc", "img.usemap", "object.archive", "object.codebase",
					"object.data", "object.usemap"
			};
			
			Set<String> set = new HashSet<String>(atts.length);
			for (String att : atts)
				set.add(att.toLowerCase(Locale.US));
			URL_ATTRIBUTES = Collections.unmodifiableSet(set);
		}

		private static final Set<String> ALLOWED_PROTOCOLS;
		static
		{
			String[] protos = { "http", "https", "ftp", "mailto" };
			Set<String> set = new HashSet<String>(protos.length);
			for (String proto : protos)
				set.add(proto.toLowerCase(Locale.US));
			ALLOWED_PROTOCOLS = Collections.unmodifiableSet(set);
		}
		
		private int elementLevel = -1;
		private int filterLevel = -1;

		private final Set<String> allowedClasses;

		/**
		 * Creates a new XHTMLReader.
		 * @param parent parent reader to wrap
		 * @param allowedClasses set of allowed css classes
		 */
		public XHTMLReader(XMLReader parent, Set<String> allowedClasses)
		{
			super(parent);
			this.allowedClasses = allowedClasses;
		}

		private boolean isAllowedElement(String elName)
		{
			if (elName == null)
				return false;
			return ALLOWED_TAGS.contains(elName.toLowerCase(Locale.US));
		}

		private boolean isAllowedAttribute(String elName, String attName)
		{
			if (elName == null)
				return false;
			if (attName == null)
				return false;

			if (ALLOWED_ATTRIBUTES.contains(elName + ".*"))
				return true;
			if (ALLOWED_ATTRIBUTES.contains(elName + ".-"))
				return false;
			if (ALLOWED_ATTRIBUTES.contains("*." + attName))
				return true;

			return ALLOWED_ATTRIBUTES.contains(elName + "." + attName);
		}
		
		private boolean isUrlAttribute(String elName, String attName)
		{
			if (elName == null)
				return false;
			if (attName == null)
				return false;
			
			if (URL_ATTRIBUTES.contains(elName + ".*"))
				return true;
			if (URL_ATTRIBUTES.contains(elName + ".-"))
				return false;
			if (URL_ATTRIBUTES.contains("*." + attName))
				return true;
			
			return URL_ATTRIBUTES.contains(elName + "." + attName);
		}
		
		private boolean validateUrl(String url)
		{
			try
			{
				URL context = new URL("http://localhost/");				
				URL testUrl = new URL(context, url);
				
				String proto = testUrl.getProtocol();
				if (proto == null) return true;
				proto = proto.toLowerCase(Locale.US);
				
				if (ALLOWED_PROTOCOLS.contains(proto)) return true;
				
				logger.warn("Invalid protocol: " + proto);
				
				return false;
			} 
			catch (MalformedURLException e)
			{
				logger.warn("Malformed URL: + " + url + " (" + e.getMessage() + ")");
			}			
			
			return false;
		}

		private String getCanonicalElement(String localName)
		{
			if (localName == null)
				return null;
			String canon = REPLACED_TAGS.get(localName);
			if (canon != null)
				return canon;
			return localName.toLowerCase(Locale.US);
		}

		private String getCanonicalAttribute(String localName)
		{
			if (localName == null)
				return null;
			return localName.toLowerCase(Locale.US);
		}

		private void addClassAttribute(AttributesImpl attributes, String value)
		{
			String[] classes = value.split("\\s+");

			StringBuilder buf = new StringBuilder();
			for (String cl : classes)
			{
				cl = cl.trim();
				if (allowedClasses.contains(cl))
				{
					if (buf.length() > 0)
						buf.append(" ");
					buf.append(cl);
				}
			}
			if (buf.length() > 0)
				attributes.addAttribute("", "class", "class", "CDATA", buf.toString());
		}

		@Override
		public void startDocument() throws SAXException
		{
			super.startDocument();
			elementLevel = -1;
			filterLevel = -1;
		}

		@Override
		public void endDocument() throws SAXException
		{
			super.endDocument();
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
		{
			String tagName = getCanonicalElement(localName);

			elementLevel++;

			if (elementLevel == 0)
			{
				// if 'html' is not the root element, substitute our own
				if (!HTML.equals(tagName))
				{
					// substitute our own wrapper
					super.startElement("", "html", "html", NO_ATTRIBUTES);
					super.startElement("", "body", "body", NO_ATTRIBUTES);
					return;
				}
			}

			// if filtering, skip this
			if (filterLevel >= 0)
				return;

			// check to see if element is allowed
			if (!isAllowedElement(tagName))
			{
				filterLevel = elementLevel;
				return;
			}

			// filter attributes
			AttributesImpl filteredAtts = new AttributesImpl();
			for (int i = 0; i < atts.getLength(); i++)
			{
				String attName = getCanonicalAttribute(atts.getLocalName(i));

				if ("class".equals(attName))
				{
					addClassAttribute(filteredAtts, atts.getValue(i));
				}
				else if (isAllowedAttribute(tagName, attName))
				{
					if (isUrlAttribute(tagName, attName))
					{
						// filter out urls
						
						if (validateUrl(atts.getValue(i)))
						{
							filteredAtts.addAttribute("", attName, attName, atts.getType(i), atts.getValue(i));
						}
						else
						{
							// not valid -- replace with safe value
							filteredAtts.addAttribute("", attName, attName, atts.getType(i), "#");
						}
						
					}
					else
					{
						filteredAtts.addAttribute("", attName, attName, atts.getType(i), atts.getValue(i));
					}
				}
			}

			super.startElement("", tagName, tagName, filteredAtts);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			try
			{
				String tagName = getCanonicalElement(localName);

				if (elementLevel == 0)
				{
					// ending root element
					if (!HTML.equals(tagName))
					{
						super.endElement("", "body", "body");
						super.endElement("", "html", "html");
						return;
					}
				}

				// check for filtered elements
				if (filterLevel >= 0)
				{
					if (filterLevel == elementLevel)
					{
						filterLevel = -1;
					}
					return;
				}

				super.endElement("", tagName, tagName);
			}
			finally
			{
				elementLevel--;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			if (filterLevel >= 0)
				return;
			super.characters(ch, start, length);
		}

		@Override
		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
		{
			if (filterLevel >= 0)
				return;
			super.ignorableWhitespace(ch, start, length);
		}

		@Override
		public void processingInstruction(String target, String data) throws SAXException
		{
			if (filterLevel >= 0)
				return;
			super.processingInstruction(target, data);
		}

		@Override
		public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException
		{
			if (filterLevel >= 0)
				return;
			super.unparsedEntityDecl(name, publicId, systemId, notationName);
		}

		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
		{
			return super.resolveEntity(publicId, systemId);
		}

		@Override
		public void skippedEntity(String name) throws SAXException
		{
			if (filterLevel >= 0)
				return;
			super.skippedEntity(name);
		}

		@Override
		public void notationDecl(String name, String publicId, String systemId) throws SAXException
		{
			if (filterLevel >= 0)
				return;
			super.notationDecl(name, publicId, systemId);
		}

		@Override
		public void startPrefixMapping(String prefix, String uri) throws SAXException
		{
			if ("".equals(prefix))
				return; // don't map default prefix
			super.startPrefixMapping(prefix, uri);
		}

		@Override
		public void endPrefixMapping(String prefix) throws SAXException
		{
			if ("".equals(prefix))
				return; // don't map default prefix
			super.endPrefixMapping(prefix);
		}
	}

}
