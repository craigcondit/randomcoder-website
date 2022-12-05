package org.randomcoder.website.xml;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.website.test.mock.xml.AbstractXMLReaderMock;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AbsXMLReaderTest {

    private AbstractXMLReaderMock reader;

    @Before
    public void setUp() {
        reader = new AbstractXMLReaderMock();
    }

    @After
    public void tearDown() {
        reader = null;
    }

    @Test
    public void testParse() throws SAXException, IOException {
        reader.parse("test.xml");
        assertEquals("test.xml", reader.getInputSource().getSystemId());
    }

    @Test
    public void testGetContentHandler() {
        DefaultHandler handler = new DefaultHandler();
        reader.setContentHandler(handler);
        assertEquals(handler, reader.getContentHandler());
    }

    @Test
    public void testGetDTDHandler() {
        DefaultHandler handler = new DefaultHandler();
        reader.setDTDHandler(handler);
        assertEquals(handler, reader.getDTDHandler());
    }

    @Test
    public void testGetEntityResolver() {
        DefaultHandler handler = new DefaultHandler();
        reader.setEntityResolver(handler);
        assertEquals(handler, reader.getEntityResolver());
    }

    @Test
    public void testGetErrorHandler() {
        DefaultHandler handler = new DefaultHandler();
        reader.setErrorHandler(handler);
        assertEquals(handler, reader.getErrorHandler());
    }

    @Test
    public void testGetFeature() throws SAXException {
        reader.setFeature("test-feature", true);
        assertTrue(reader.getFeature("test-feature"));
        assertFalse(reader.getFeature("bogus-feature"));
    }

    @Test
    public void testGetProperty() throws SAXException {
        reader.setProperty("test-property", "test-value");
        assertEquals("test-value", reader.getProperty("test-property"));
        assertNull(reader.getProperty("bogus-property"));
    }

}
