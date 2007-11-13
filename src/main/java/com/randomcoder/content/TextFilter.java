package com.randomcoder.content;

import java.io.*;
import java.net.URL;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.*;

/**
 * Plain text (text/plain) filter.
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
public class TextFilter implements ContentFilter
{
	private static final String XSL_RESOURCE = "text-to-xhtml.xsl";

	private final Templates templates;

	/**
	 * Creates a new text filter.
	 * @throws TransformerConfigurationException if transformer factory fails
	 */
	public TextFilter() throws TransformerConfigurationException
	{
		// cache templates for later use
		TransformerFactory tFactory = TransformerFactory.newInstance();
		templates = tFactory.newTemplates(new SAXSource(new InputSource(getClass().getResourceAsStream(XSL_RESOURCE))));
	}

	public void validate(String contentType, Reader content) throws InvalidContentException, InvalidContentTypeException, IOException
	{
	// all input is legal here
	}

	public XMLReader getXMLReader(URL baseUrl, String contentType)
	{
		return new TextReader();
	}

	public Templates getXSLTemplates(String contentType)
	{
		return templates;
	}

	public String getPrefix(String contentType)
	{
		return null;
	}

	public String getSuffix(String contentType)
	{
		return null;
	}
}
