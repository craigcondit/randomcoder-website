package org.randomcoder.website.test.mock.content;

import org.randomcoder.website.contentfilter.ContentFilter;
import org.randomcoder.website.contentfilter.InvalidContentException;
import org.randomcoder.website.contentfilter.InvalidContentTypeException;
import org.randomcoder.website.contentfilter.TextReader;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.transform.Templates;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

public class ContentFilterMock implements ContentFilter {

    @Override
    public String getPrefix(String contentType) {
        return null;
    }

    @Override
    public String getSuffix(String contentType) {
        return null;
    }

    @Override
    public XMLReader getXMLReader(URL baseUrl, String contentType) throws SAXException {
        return new TextReader();
    }

    @Override
    public Templates getXSLTemplates(String contentType) {
        return null;
    }

    @Override
    public void validate(String contentType, Reader content) throws InvalidContentException, InvalidContentTypeException, IOException {
    }

}
