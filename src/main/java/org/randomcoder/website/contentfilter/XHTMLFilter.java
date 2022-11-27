package org.randomcoder.website.contentfilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Set;

public class XHTMLFilter implements ContentFilter {
    public static final String PREFIX =
            "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>Untitled</title></head><body>";
    public static final String SUFFIX = "</body></html>";

    protected static final Logger logger = LoggerFactory.getLogger(XHTMLFilter.class);

    private static final String XSL_RESOURCE = "xhtml-to-xhtml.xsl";
    private static final String XSD_RESOURCE = "xhtml1-transitional.xsd";
    private static final String NS_RESOURCE = "namespace.xsd";
    private final Templates templates;
    private final Schema schema;
    private final Set<String> allowedClasses;

    public XHTMLFilter(Set<String> allowedClasses) throws TransformerConfigurationException, SAXException {

        this.allowedClasses = allowedClasses;

        // cache templates for later use
        TransformerFactory tFactory = TransformerFactory.newInstance();
        templates = tFactory.newTemplates(new SAXSource(new InputSource(getClass().getResourceAsStream(XSL_RESOURCE))));
        SchemaFactory sFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source xhtmlSource = new StreamSource(getClass().getResourceAsStream(XSD_RESOURCE));
        Source nsSource = new StreamSource(getClass().getResourceAsStream(NS_RESOURCE));
        schema = sFactory.newSchema(new Source[]{nsSource, xhtmlSource});
    }

    @Override
    public XMLReader getXMLReader(URL baseUrl, String contentType) throws SAXException {
        XMLReader xmlReader;
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            xmlReader = spf.newSAXParser().getXMLReader();
        } catch (ParserConfigurationException e) {
            throw new SAXException(e);
        }

        return new XHTMLReader(xmlReader, allowedClasses, baseUrl);
    }

    @Override
    public Templates getXSLTemplates(String contentType) {
        return templates;
    }

    @Override
    public String getPrefix(String contentType) {
        return PREFIX;
    }

    @Override
    public String getSuffix(String contentType) {
        return SUFFIX;
    }

    @Override
    public void validate(String contentType, Reader content) throws InvalidContentException, InvalidContentTypeException, IOException {
        Validator validator = schema.newValidator();
        XHTMLErrorHandler handler = new XHTMLErrorHandler();
        validator.setErrorHandler(handler);

        try {
            validator.validate(new StreamSource(content));
        } catch (SAXException e) {
            if (handler.getMessage() != null) {
                // we caught it
                throw new InvalidContentException(handler.getMessage(), handler.getLineNumber(), handler.getColumnNumber());
            }
            // something else bad happened
            throw new InvalidContentException(e.getMessage(), 1, 1);
        }
    }

}
