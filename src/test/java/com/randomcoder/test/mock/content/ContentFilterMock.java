package com.randomcoder.test.mock.content;

import java.io.*;
import java.net.URL;

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

	public XMLReader getXMLReader(URL baseUrl, String contentType) throws SAXException
	{
		return new TextReader();
	}

	public Templates getXSLTemplates(String contentType)
	{
		return null;
	}

	public void validate(String contentType, Reader content) throws InvalidContentException, InvalidContentTypeException, IOException
	{
	}
}
