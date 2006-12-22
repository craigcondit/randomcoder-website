package com.randomcoder.content;

import java.io.*;
import java.net.URL;

import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;

import com.randomcoder.xml.AbstractXMLReader;

/**
 * Plain text to XML reader.
 * 
 * <p> This implementation does very little formatting - line breaks are
 * replaced with &lt;br /&gt; tags. </p>
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
public class TextReader extends AbstractXMLReader
{
	private static final Attributes NO_ATTRIBUTES = new AttributesImpl();

	/**
	 * Parses the given input source.
	 * @param input input source
	 * @throws IOException if an I/O error occurs
	 * @throws SAXException if XML parsing fails
	 */
	@Override
	public void parse(InputSource input) throws IOException, SAXException
	{
		ContentHandler handler = getContentHandler();
		if (handler == null)
			return;

		// get a reader object
		BufferedReader reader = null;
		if (input.getCharacterStream() != null)
		{
			reader = new BufferedReader(input.getCharacterStream());
		}
		else if (input.getByteStream() != null)
		{
			reader = new BufferedReader(new InputStreamReader(input.getByteStream()));
		}
		else if (input.getSystemId() != null)
		{
			reader = new BufferedReader(new InputStreamReader(new URL(input.getSystemId()).openStream()));
		}
		else
		{
			throw new SAXException("Invalid InputSource");
		}

		// start document
		handler.startDocument();

		// root element
		handler.startElement("", "text", "text", NO_ATTRIBUTES);

		String line = null;
		while ((line = reader.readLine()) != null)
		{
			handler.startElement("", "line", "line", NO_ATTRIBUTES);
			char[] data = line.trim().toCharArray();
			handler.characters(data, 0, data.length);
			handler.endElement("", "line", "line");
		}

		// end root element
		handler.endElement("", "text", "text");

		// end document
		handler.endDocument();
	}
}
