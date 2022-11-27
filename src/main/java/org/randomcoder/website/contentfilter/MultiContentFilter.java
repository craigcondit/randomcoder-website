package org.randomcoder.website.contentfilter;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.transform.Templates;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

public class MultiContentFilter implements ContentFilter {

    private final Map<String, ContentFilter> filters;
    private ContentFilter defaultFilter;

    public MultiContentFilter(Map<String, ContentFilter> filters) {
        this.filters = filters;
    }

    public void setDefaultHandler(ContentFilter defaultFilter) {
        this.defaultFilter = defaultFilter;
    }

    @Override
    public void validate(String contentType, Reader content) throws InvalidContentException, InvalidContentTypeException, IOException {
        getFilterForContentType(contentType).validate(contentType, content);
    }

    @Override
    public XMLReader getXMLReader(URL baseUrl, String contentType)
            throws SAXException {
        return getFilterForContentType(contentType)
                .getXMLReader(baseUrl, contentType);
    }

    @Override
    public Templates getXSLTemplates(String contentType) {
        return getFilterForContentType(contentType).getXSLTemplates(contentType);
    }

    @Override
    public String getPrefix(String contentType) {
        return getFilterForContentType(contentType).getPrefix(contentType);
    }

    @Override
    public String getSuffix(String contentType) {
        return getFilterForContentType(contentType).getSuffix(contentType);
    }

    private ContentFilter getFilterForContentType(String contentType) throws InvalidContentTypeException {
        ContentFilter filter = filters.get(contentType);
        if (filter != null) {
            return filter;
        }

        if (defaultFilter == null) {
            throw new InvalidContentTypeException("Unknown content type " + contentType);
        }

        return defaultFilter;
    }

}
