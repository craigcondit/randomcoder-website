package org.randomcoder.website.contentfilter;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.transform.Templates;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

public interface ContentFilter {

    void validate(String contentType, Reader content) throws InvalidContentException, InvalidContentTypeException, IOException;

    XMLReader getXMLReader(URL baseUrl, String contentType) throws SAXException;

    Templates getXSLTemplates(String contentType);

    String getPrefix(String contentType);

    String getSuffix(String contentType);

}
