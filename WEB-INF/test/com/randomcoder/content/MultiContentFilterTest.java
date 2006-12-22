package com.randomcoder.content;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.junit.*;
import org.xml.sax.*;

import com.randomcoder.io.SequenceReader;
import com.randomcoder.test.mock.content.ContentFilterMock;

public class MultiContentFilterTest
{
	private MultiContentFilter filter;

	@Before
	public void setUp() throws Exception
	{		
		Map<String, ContentFilter> filters = new HashMap<String, ContentFilter>();
		filters.put("text/plain", new TextFilter());
		filters.put("application/xhtml+xml", new XHTMLFilter());
		
		filter = new MultiContentFilter();
		filter.setDefaultHandler(new ContentFilterMock());
		filter.setFilters(filters);
	}

	@After
	public void tearDown() throws Exception
	{
		filter = null;
	}

	@Test
	public void testValidate() throws Exception
	{
		filter.validate("text/plain", new StringReader("Testing"));
	}

	@Test(expected=InvalidContentException.class)
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
		}
		finally
		{
			if (reader != null) reader.close();
		}
	}

	@Test
	public void testGetXSLTemplates()
	{
		assertNotNull(filter.getXSLTemplates("text/plain"));
		assertNotNull(filter.getXSLTemplates("application/xhtml+xml"));
		assertNull(filter.getXSLTemplates("bogus"));
	}
	
	@Test
	public void testGetXMLReader() throws Exception
	{
		XMLReader reader = filter.getXMLReader("text/plain");		
		reader.parse(new InputSource(new StringReader("testing")));
	}
}
