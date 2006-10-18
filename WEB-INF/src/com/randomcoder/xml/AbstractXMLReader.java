package com.randomcoder.xml;

import java.io.IOException;
import java.util.*;

import org.xml.sax.*;

/**
 * Abstract base class for {@link XMLReader} implementations.
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
abstract public class AbstractXMLReader implements XMLReader
{
	private final Map<String, Boolean> features = new HashMap<String, Boolean>();
	private final Map<String, Object> properties = new HashMap<String, Object>();

	private ContentHandler contentHandler;
	private DTDHandler dtdHandler;
	private EntityResolver entityResolver;
	private ErrorHandler errorHandler;

	/**
	 * Parses the given input source.
	 * 
	 * <p> Subclasses must implement this method to handle XML parsing. </p>
	 * 
	 * @param input input source to parse
	 * @see XMLReader#parse(InputSource)
	 */
	abstract public void parse(InputSource input) throws IOException, SAXException;

	/**
	 * @see XMLReader#parse(String)
	 */
	public void parse(String systemId) throws IOException, SAXException
	{
		parse(new InputSource(systemId));
	}

	/**
	 * @see XMLReader#getContentHandler()
	 */
	public ContentHandler getContentHandler()
	{
		return contentHandler;
	}

	/**
	 * @see XMLReader#setContentHandler(ContentHandler)
	 */
	public void setContentHandler(ContentHandler contentHandler)
	{
		this.contentHandler = contentHandler;
	}

	/**
	 * @see XMLReader#getDTDHandler()
	 */
	public DTDHandler getDTDHandler()
	{
		return dtdHandler;
	}

	/**
	 * @see XMLReader#setDTDHandler(DTDHandler)
	 */
	public void setDTDHandler(DTDHandler dtdHandler)
	{
		this.dtdHandler = dtdHandler;
	}

	/**
	 * @see XMLReader#getEntityResolver()
	 */
	public EntityResolver getEntityResolver()
	{
		return entityResolver;
	}

	/**
	 * @see XMLReader#setEntityResolver(EntityResolver)
	 */
	public void setEntityResolver(EntityResolver entityResolver)
	{
		this.entityResolver = entityResolver;
	}

	/**
	 * @see XMLReader#getErrorHandler()
	 */
	public ErrorHandler getErrorHandler()
	{
		return errorHandler;
	}

	/**
	 * @see XMLReader#setErrorHandler(ErrorHandler)
	 */
	public void setErrorHandler(ErrorHandler errorHandler)
	{
		this.errorHandler = errorHandler;
	}

	/**
	 * @see XMLReader#getFeature(String)
	 */
	public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException
	{
		Boolean exists = features.get(name);
		if (exists == null)
			return false;
		return exists.booleanValue();
	}

	/**
	 * @see XMLReader#setFeature(String, boolean)
	 */
	public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException
	{
		features.put(name, value);
	}

	/**
	 * @see XMLReader#getProperty(String)
	 */
	public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException
	{
		return properties.get(name);
	}

	/**
	 * @see XMLReader#setProperty(String, Object)
	 */
	public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException
	{
		properties.put(name, value);
	}
}
