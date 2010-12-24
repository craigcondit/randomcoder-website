package org.randomcoder.test.mock.content;

import java.io.*;
import java.net.URL;

import javax.xml.transform.Templates;

import org.xml.sax.*;

import org.randomcoder.content.*;

public class ContentFilterMock implements ContentFilter
{
	@Override
	public String getPrefix(String contentType)
	{
		return null;
	}

	@Override
	public String getSuffix(String contentType)
	{
		return null;
	}

	@Override
	public XMLReader getXMLReader(URL baseUrl, String contentType) throws SAXException
	{
		return new TextReader();
	}

	@Override
	public Templates getXSLTemplates(String contentType)
	{
		return null;
	}

	@Override
	public void validate(String contentType, Reader content) throws InvalidContentException, InvalidContentTypeException, IOException
	{
	}
}
