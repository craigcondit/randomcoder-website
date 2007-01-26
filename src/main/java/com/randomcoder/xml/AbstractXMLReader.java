package com.randomcoder.xml;

import java.io.IOException;
import java.util.*;

import org.xml.sax.*;

/**
 * Abstract base class for {@link XMLReader} implementations.
 * 
 * <pre>
 * Copyright (c) 2006-2007, Craig Condit. All rights reserved.
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
	 * @throws IOException if resource cannot be read
	 * @throws SAXException if an error occurs
	 * @see XMLReader#parse(InputSource)
	 */
	abstract public void parse(InputSource input) throws IOException, SAXException;

	/**
	 * Parses a document using the given system id.
	 * @param systemId system id
	 * @throws IOException if resource cannot be read
	 * @throws SAXException if an error occurs
	 */
	public void parse(String systemId) throws IOException, SAXException
	{
		parse(new InputSource(systemId));
	}

	/**
	 * Gets the content handler defined for this reader.
	 * @return content handler
	 */
	public ContentHandler getContentHandler()
	{
		return contentHandler;
	}

	/**
	 * Sets the content handler for this reader.
	 * @param contentHandler content handler
	 */
	public void setContentHandler(ContentHandler contentHandler)
	{
		this.contentHandler = contentHandler;
	}

	/**
	 * Gets the DTD handler for this reader.
	 * @return DTD handler
	 */
	public DTDHandler getDTDHandler()
	{
		return dtdHandler;
	}

	/**
	 * Sets the DTD handler for this reader.
	 * @param dtdHandler DTD handler
	 */
	public void setDTDHandler(DTDHandler dtdHandler)
	{
		this.dtdHandler = dtdHandler;
	}

	/**
	 * Gets the entity resolver for this reader.
	 * @return entity resolver
	 */
	public EntityResolver getEntityResolver()
	{
		return entityResolver;
	}

	/**
	 * Sets the entity resolver for this reader.
	 * @param entityResolver entity resolver
	 */
	public void setEntityResolver(EntityResolver entityResolver)
	{
		this.entityResolver = entityResolver;
	}

	/**
	 * Gets the error handler for this reader.
	 * @return error handler
	 */
	public ErrorHandler getErrorHandler()
	{
		return errorHandler;
	}

	/**
	 * Sets the error handler for this reader.
	 * @param errorHandler error handler
	 */
	public void setErrorHandler(ErrorHandler errorHandler)
	{
		this.errorHandler = errorHandler;
	}

	/**
	 * Determines if this reader supports the given feature.
	 * @param name feature name
	 * @throws SAXNotRecognizedException if the feature is not recognized
	 * @throws SAXNotSupportedException if the method is not supported
	 * @return true if feature is available, false otherwise
	 */
	public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException
	{
		Boolean exists = features.get(name);
		if (exists == null)
			return false;
		return exists.booleanValue();
	}

	/**
	 * Sets the availability of the given feature.
	 * @param name feature name
	 * @param value true if enabled, false otherwise
	 * @throws SAXNotRecognizedException if the feature is not recognized
	 * @throws SAXNotSupportedException if the method is not supported
	 */
	public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException
	{
		features.put(name, value);
	}

	/**
	 * Gets the value of the named property.
	 * @param name property name
	 * @return property value
	 * @throws SAXNotRecognizedException if the property is not recognized
	 * @throws SAXNotSupportedException if the method is not supported
	 */
	public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException
	{
		return properties.get(name);
	}

	/**
	 * Sets the value o fthe named property.
	 * @param name property name
	 * @param value property value
	 * @throws SAXNotRecognizedException if the property is not recognized
	 * @throws SAXNotSupportedException if the method is not supported
	 */
	public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException
	{
		properties.put(name, value);
	}
}
