package org.randomcoder.content;

import static org.junit.Assert.*;

import org.junit.*;
import org.xml.sax.*;

@SuppressWarnings("javadoc")
public class XHTMLErrorHandlerTest
{
	private XHTMLErrorHandler handler;

	@Before
	public void setUp()
	{
		handler = new XHTMLErrorHandler();
	}

	@After
	public void tearDown()
	{
		handler = null;
	}

	@Test
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
		catch (SAXException e)
		{
		}

		assertEquals("Wrong message", "warning", handler.getMessage());
		assertEquals("Wrong line number", 2, handler.getLineNumber());
		assertEquals("Wrong column number", 1, handler.getColumnNumber());
	}

	@Test
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
		catch (SAXException e)
		{
		}

		assertEquals("Wrong message", "warning", handler.getMessage());
		assertEquals("Wrong line number", 1, handler.getLineNumber());
		assertEquals("Wrong column number", 100 - XHTMLFilter.PREFIX.length(), handler.getColumnNumber());
	}

	@Test
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
		catch (SAXException e)
		{
		}

		assertEquals("Wrong message", "error", handler.getMessage());
		assertEquals("Wrong line number", 2, handler.getLineNumber());
		assertEquals("Wrong column number", 1, handler.getColumnNumber());
	}

	@Test
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
		catch (SAXException e)
		{
		}

		assertEquals("Wrong message", "fatalerror", handler.getMessage());
		assertEquals("Wrong line number", 2, handler.getLineNumber());
		assertEquals("Wrong column number", 1, handler.getColumnNumber());
	}
}
