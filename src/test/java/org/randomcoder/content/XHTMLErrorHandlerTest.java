package org.randomcoder.content;

import junit.framework.TestCase;

import org.xml.sax.*;

@SuppressWarnings("javadoc")
public class XHTMLErrorHandlerTest extends TestCase
{
	private XHTMLErrorHandler handler;
	
	@Override
	protected void setUp() throws Exception
	{
		handler = new XHTMLErrorHandler();
	}

	@Override
	protected void tearDown() throws Exception
	{
		handler = null;
	}

	public void testWarning() throws Exception
	{
		SAXParseException ex = null;
		try
		{
			throw new SAXParseException("warning", "public-id", null, 2, 1);
		}
		catch (SAXParseException e)
		{
			ex = e;
		}
		try
		{
			handler.warning(ex);
		}
		catch (SAXException e) {}
		
		assertEquals("Wrong message", "warning", handler.getMessage());
		assertEquals("Wrong line number", 2, handler.getLineNumber());
		assertEquals("Wrong column number", 1, handler.getColumnNumber());
	}

	public void testWarningLine1() throws Exception
	{
		SAXParseException ex = null;
		try
		{
			throw new SAXParseException("warning", "public-id", null, 1, 100);
		}
		catch (SAXParseException e)
		{
			ex = e;
		}
		try
		{
			handler.warning(ex);
		}
		catch (SAXException e) {}
		
		assertEquals("Wrong message", "warning", handler.getMessage());
		assertEquals("Wrong line number", 1, handler.getLineNumber());
		assertEquals("Wrong column number", 100 - XHTMLFilter.PREFIX.length(), handler.getColumnNumber());
	}
	
	public void testError() throws Exception
	{
		SAXParseException ex = null;
		try
		{
			throw new SAXParseException("error", "public-id", null, 2, 1);
		}
		catch (SAXParseException e)
		{
			ex = e;
		}
		
		try
		{
			handler.error(ex);
		}
		catch (SAXException e) {}
		
		assertEquals("Wrong message", "error", handler.getMessage());
		assertEquals("Wrong line number", 2, handler.getLineNumber());
		assertEquals("Wrong column number", 1, handler.getColumnNumber());
	}

	public void testFatalError() throws Exception
	{
		SAXParseException ex = null;
		try
		{
			throw new SAXParseException("fatalerror", "public-id", null, 2, 1);
		}
		catch (SAXParseException e)
		{
			ex = e;
		}
		try
		{
			handler.fatalError(ex);
		}
		catch (SAXException e) {}
		
		assertEquals("Wrong message", "fatalerror", handler.getMessage());
		assertEquals("Wrong line number", 2, handler.getLineNumber());
		assertEquals("Wrong column number", 1, handler.getColumnNumber());
	}

}
