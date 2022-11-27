package org.randomcoder.website.contentfilter;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

public class TextFilter implements ContentFilter {

    private static final String XSL_RESOURCE = "text-to-xhtml.xsl";

    private final Templates templates;

    public TextFilter() throws TransformerConfigurationException {
        // cache templates for later use
        TransformerFactory tFactory = TransformerFactory.newInstance();
        templates = tFactory.newTemplates(new SAXSource(new InputSource(getClass().getResourceAsStream(XSL_RESOURCE))));
    }

    @Override
    public void validate(String contentType, Reader content) throws InvalidContentException, InvalidContentTypeException, IOException {
        // all input is legal here
    }

    @Override
    public XMLReader getXMLReader(URL baseUrl, String contentType) {
        return new TextReader();
    }

    @Override
    public Templates getXSLTemplates(String contentType) {
        return templates;
    }

    @Override
    public String getPrefix(String contentType) {
        return null;
    }

    @Override
    public String getSuffix(String contentType) {
        return null;
    }

}
