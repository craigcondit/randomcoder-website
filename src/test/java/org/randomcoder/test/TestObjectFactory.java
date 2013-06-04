package org.randomcoder.test;

import java.io.*;
import java.util.Properties;

import org.randomcoder.xml.XmlUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

@SuppressWarnings("javadoc")
public class TestObjectFactory
{
	private static final String RESOURCE_XMLSEC_PROPS = "/xml-security.properties";
	
	private static final String CERTIFICATE_PASSWORD = "certificate.password";
	private static final String CERTIFICATE_ALIAS = "certificate.alias";
	private static final String KEYSTORE_PASSWORD = "keystore.password";
	private static final String KEYSTORE_TYPE = "keystore.type";
	private static final String KEYSTORE_RESOURCE = "keystore.resource";
	private static final String CLIENT_PUBLICKEY_ENCODED = "client.publickey.encoded";

	public static String getEncodedClientPublicKey() throws IOException
	{
		Properties properties = new Properties();
		properties.load(TestObjectFactory.class.getResourceAsStream(RESOURCE_XMLSEC_PROPS));
		return properties.getProperty(CLIENT_PUBLICKEY_ENCODED);
	}
	
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