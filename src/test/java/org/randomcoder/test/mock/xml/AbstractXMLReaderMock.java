package org.randomcoder.test.mock.xml;

import java.io.IOException;

import org.xml.sax.*;

import org.randomcoder.xml.AbstractXMLReader;

@SuppressWarnings("javadoc")
public class AbstractXMLReaderMock extends AbstractXMLReader
{
	private InputSource input;
	
	@Override
	public void parse(InputSource _input) throws IOException, SAXException
	{
		input = _input;
	}		
	
	public InputSource getInputSource() { return input; }
}
