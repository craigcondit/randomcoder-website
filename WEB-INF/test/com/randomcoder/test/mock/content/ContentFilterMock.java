package com.randomcoder.test.mock.content;

import java.io.*;

import javax.xml.transform.Templates;

import org.xml.sax.*;

import com.randomcoder.content.*;

public class ContentFilterMock implements ContentFilter
{
	public String getPrefix(String contentType)
	{
		return null;
	}

	public String getSuffix(String contentType)
	{
		return null;
	}

	public XMLReader getXMLReader(String contentType) throws SAXException
	{
		return null;
	}

	public Templates getXSLTemplates(String contentType)
	{
		return null;
	}

	public void validate(String contentType, Reader content) throws InvalidContentException, InvalidContentTypeException, IOException
	{
	}
}
