package org.randomcoder.content;

import java.io.*;
import java.net.URL;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.StreamResult;

import org.randomcoder.io.SequenceReader;
import org.xml.sax.*;

/**
 * Utility methods for content formatting.
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
public class ContentUtils
{

	/**
	 * Format the given input source using the given filter into XHTML.
	 * @param mimeType mime type
	 * @param baseUrl base URL for links, or <code>null</code> to omit
	 * @param content original content
	 * @param filter filter instance
	 * @param output XSLT result object
	 * @throws TransformerException if transforming xml fails
	 * @throws IOException if an I/O error occurs
	 * @throws SAXException if xml parsing fails
	 *
	 */
	public static void format(String mimeType, URL baseUrl, InputSource content, ContentFilter filter, Result output)
	throws TransformerException, IOException, SAXException
	{
		// TODO needs unit testing
		
		TransformerFactory tFactory = TransformerFactory.newInstance();
		
		SAXTransformerFactory stFactory = (SAXTransformerFactory) tFactory;

		Templates templates = filter.getXSLTemplates(mimeType);

		TransformerHandler tHandler = null;
		if (templates == null)
			tHandler = stFactory.newTransformerHandler();
		else
			tHandler = stFactory.newTransformerHandler(templates);

		tHandler.setResult(output);

		XMLReader reader = filter.getXMLReader(baseUrl, mimeType);
		reader.setContentHandler(tHandler);
		reader.parse(content);
	}

	/**
	 * Format the given input source using the given filter into XHTML.
	 * @param mimeType mime type
	 * @param baseUrl base URL for links, or <code>null</code> to omit.
	 * @param content original content
	 * @param filter filter instance
	 * @return XHTML-transformed content
	 * @throws TransformerException if transforming xml fails
	 * @throws IOException if an I/O error occurs
	 * @throws SAXException if xml parsing fails
	 */
	public static String format(String mimeType, URL baseUrl, InputSource content, ContentFilter filter)
	throws TransformerException, IOException, SAXException
	{
		StringWriter out = new StringWriter();
		format(mimeType, baseUrl, content, filter, new StreamResult(out));
		return out.toString();
	}

	/**
	 * Format the given content to XHTML. 
	 * @param content content
	 * @param contentType content type
	 * @param filter content filter
	 * @param baseUrl base URL for links, or <code>null</code> to omit
	 * @return transformed output
	 * @throws TransformerException if transforming xml fails
	 * @throws IOException if an I/O error occurs
	 * @throws SAXException if xml parsing fails
	 */
	public static String formatText(String content, URL baseUrl, ContentType contentType, ContentFilter filter)
	throws TransformerException, IOException, SAXException
	{
		String mimeType = contentType.getMimeType();

		String prefix = filter.getPrefix(mimeType);
		String suffix = filter.getSuffix(mimeType);

		List<Reader> readers = new ArrayList<Reader>();
		if (prefix != null)
			readers.add(new StringReader(prefix));
		
		readers.add(new StringReader(content));
		
		if (suffix != null)
			readers.add(new StringReader(suffix));

		SequenceReader reader = new SequenceReader(readers);

		return format(mimeType, baseUrl, new InputSource(reader), filter);
	}
	
	/**
	 * Format the given content to XHTML. 
	 * @param content content
	 * @param baseUrl base URL for links, or <code>null</code> to omit
	 * @param contentType content type
	 * @param filter content filter
	 * @param result XSLT result
	 * @throws TransformerException if transforming xml fails
	 * @throws IOException if an I/O error occurs
	 * @throws SAXException if xml parsing fails
	 */
	public static void formatText(String content, URL baseUrl, ContentType contentType, ContentFilter filter, Result result)
	throws TransformerException, IOException, SAXException
	{
		String mimeType = contentType.getMimeType();

		String prefix = filter.getPrefix(mimeType);
		String suffix = filter.getSuffix(mimeType);

		List<Reader> readers = new ArrayList<Reader>();
		if (prefix != null)
			readers.add(new StringReader(prefix));
		
		readers.add(new StringReader(content));
		
		if (suffix != null)
			readers.add(new StringReader(suffix));

		SequenceReader reader = new SequenceReader(readers);

		format(mimeType, baseUrl, new InputSource(reader), filter, result);
	}		
}
