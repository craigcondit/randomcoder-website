package org.randomcoder.content;

import java.io.*;
import java.util.*;

import junit.framework.TestCase;

import org.randomcoder.io.SequenceReader;
import org.randomcoder.test.mock.content.ContentFilterMock;
import org.xml.sax.*;

@SuppressWarnings("javadoc")
public class MultiContentFilterTest extends TestCase
{
	private MultiContentFilter filter;

	@Override
	public void setUp() throws Exception
	{		
		Map<String, ContentFilter> filters = new HashMap<String, ContentFilter>();
		filters.put("text/plain", new TextFilter());
		filters.put("application/xhtml+xml", new XHTMLFilter());
		
		filter = new MultiContentFilter();
		filter.setDefaultHandler(new ContentFilterMock());
		filter.setFilters(filters);
	}

	@Override
	public void tearDown() throws Exception
	{
		filter = null;
	}

	public void testValidate() throws Exception
	{
		filter.validate("text/plain", new StringReader("Testing"));
	}

	public void testValidateFailure() throws Exception
	{		
		String prefix = filter.getPrefix("application/xhtml+xml");
		String suffix = filter.getSuffix("application/xhtml+xml");
		
		assertEquals(XHTMLFilter.PREFIX, prefix);
		assertEquals(XHTMLFilter.SUFFIX, suffix);
		
		List<Reader> readers = new ArrayList<Reader>();
		if (prefix != null) readers.add(new StringReader(prefix));
		readers.add(new StringReader("<br>"));
		if (suffix != null) readers.add(new StringReader(suffix));
		
		Reader reader = null;
		try
		{
			reader = new SequenceReader(readers);		
			filter.validate("application/xhtml+xml", reader);
			fail("InvalidContentException expected");
		}
		catch (InvalidContentException e)
		{
			// pass
		}
		finally
		{
			if (reader != null) reader.close();
		}
	}

	public void testGetXSLTemplates()
	{
		assertNotNull(filter.getXSLTemplates("text/plain"));
		assertNotNull(filter.getXSLTemplates("application/xhtml+xml"));
		assertNull(filter.getXSLTemplates("bogus"));
	}
	
	public void testGetXMLReader() throws Exception
	{
		XMLReader reader = filter.getXMLReader(null, "text/plain");		
		reader.parse(new InputSource(new StringReader("testing")));
	}
	
	public void testNoDefaultHandler() throws Exception
	{
		try
		{
			filter.setDefaultHandler(null);		
			filter.getPrefix("bogus");
			fail("InvalidContentTypeException expected");
		}
		catch (InvalidContentTypeException e)
		{
			// pass
		}
	}
}
