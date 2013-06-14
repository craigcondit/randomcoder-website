package org.randomcoder.test;

import java.io.*;

import org.randomcoder.xml.XmlUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

@SuppressWarnings("javadoc")
public class TestObjectFactory
{
	public static String getResourceAsString(String resource) throws IOException
	{
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(TestObjectFactory.class.getResourceAsStream(resource)));
			StringBuilder sbuf = new StringBuilder();
			char[] buf = new char[32768];
			int c;
			do
			{
				c = reader.read(buf);
				if (c >= 0) sbuf.append(buf, 0, c); 
			}
			while (c > 0);
			return sbuf.toString();
		}
		finally
		{
			reader.close();
		}
	}
	
	public static Document getXmlDocument(String resource) throws Exception
	{
		return XmlUtils.parseXml(new InputSource(TestObjectFactory.class.getResourceAsStream(resource)));
	}
}