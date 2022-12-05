package org.randomcoder.website.contentfilter;

import org.randomcoder.website.xml.AbstractXMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class TextReader extends AbstractXMLReader {

    private static final Attributes NO_ATTRIBUTES = new AttributesImpl();

    @Override
    public void parse(InputSource input) throws IOException, SAXException {
        ContentHandler handler = getContentHandler();
        if (handler == null) {
            return;
        }

        // get a reader object
        BufferedReader reader = null;
        if (input.getCharacterStream() != null) {
            reader = new BufferedReader(input.getCharacterStream());
        } else if (input.getByteStream() != null) {
            reader = new BufferedReader(new InputStreamReader(input.getByteStream()));
        } else if (input.getSystemId() != null) {
            reader = new BufferedReader(new InputStreamReader(new URL(input.getSystemId()).openStream()));
        } else {
            throw new SAXException("Invalid InputSource");
        }

        // start document
        handler.startDocument();

        // root element
        handler.startElement("", "text", "text", NO_ATTRIBUTES);

        String line = null;
        while ((line = reader.readLine()) != null) {
            handler.startElement("", "line", "line", NO_ATTRIBUTES);
            char[] data = line.trim().toCharArray();
            handler.characters(data, 0, data.length);
            handler.endElement("", "line", "line");
        }

        // end root element
        handler.endElement("", "text", "text");

        // end document
        handler.endDocument();
    }

}
