package com.randomcoder.content;

import java.io.*;

import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.StreamResult;

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
	 * @param contentType content type
	 * @param content original content
	 * @param filter filter instance
	 * @return XHTML-transformed content
	 * @throws TransformerConfigurationException if transformer config fails
	 * @throws TransformerException if transforming xml fails
	 * @throws IOException if an I/O error occurs
	 * @throws SAXException if xml parsing fails
	 */
	public static String format(String contentType, InputSource content, ContentFilter filter) throws TransformerConfigurationException, TransformerException,
			IOException, SAXException
	{
		TransformerFactory tFactory = TransformerFactory.newInstance();
		if (!tFactory.getFeature(SAXTransformerFactory.FEATURE))
			throw new TransformerConfigurationException("SAXTransformerFactory is not supported");

		SAXTransformerFactory stFactory = (SAXTransformerFactory) tFactory;

		Templates templates = filter.getXSLTemplates(contentType);

		TransformerHandler tHandler = null;
		if (templates == null)
			tHandler = stFactory.newTransformerHandler();
		else
			tHandler = stFactory.newTransformerHandler(templates);

		StringWriter out = new StringWriter();
		tHandler.setResult(new StreamResult(out));

		XMLReader reader = filter.getXMLReader(contentType);
		reader.setContentHandler(tHandler);
		reader.parse(content);

		return out.toString();
	}
}
