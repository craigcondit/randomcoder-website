package org.randomcoder.content;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.io.SequenceReader;
import org.randomcoder.test.mock.content.ContentFilterMock;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MultiContentFilterTest {
    private MultiContentFilter filter;

    @Before
    public void setUp() throws Exception {
        Map<String, ContentFilter> filters = new HashMap<String, ContentFilter>();
        filters.put("text/plain", new TextFilter());
        filters.put("application/xhtml+xml", new XHTMLFilter(Collections.emptySet()));

        filter = new MultiContentFilter(filters);
        filter.setDefaultHandler(new ContentFilterMock());
    }

    @After
    public void tearDown() {
        filter = null;
    }

    @Test
    public void testValidate() throws Exception {
        filter.validate("text/plain", new StringReader("Testing"));
    }

    @Test(expected = InvalidContentException.class)
    public void testValidateFailure() throws Exception {
        String prefix = filter.getPrefix("application/xhtml+xml");
        String suffix = filter.getSuffix("application/xhtml+xml");

        assertEquals(XHTMLFilter.PREFIX, prefix);
        assertEquals(XHTMLFilter.SUFFIX, suffix);

        List<Reader> readers = new ArrayList<Reader>();
        if (prefix != null)
            readers.add(new StringReader(prefix));
        readers.add(new StringReader("<br>"));
        if (suffix != null)
            readers.add(new StringReader(suffix));

        try (Reader reader = new SequenceReader(readers)) {
            filter.validate("application/xhtml+xml", reader);
        }
    }

    @Test
    public void testGetXSLTemplates() {
        assertNotNull(filter.getXSLTemplates("text/plain"));
        assertNotNull(filter.getXSLTemplates("application/xhtml+xml"));
        assertNull(filter.getXSLTemplates("bogus"));
    }

    @Test
    public void testGetXMLReader() throws Exception {
        XMLReader reader = filter.getXMLReader(null, "text/plain");
        reader.parse(new InputSource(new StringReader("testing")));
    }

    @Test(expected = InvalidContentTypeException.class)
    public void testNoDefaultHandler() throws Exception {
        filter.setDefaultHandler(null);
        filter.getPrefix("bogus");
    }
}
