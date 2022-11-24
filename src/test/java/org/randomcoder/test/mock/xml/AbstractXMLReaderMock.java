package org.randomcoder.test.mock.xml;

import org.randomcoder.xml.AbstractXMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

public class AbstractXMLReaderMock extends AbstractXMLReader {
    private InputSource input;

    @Override
    public void parse(InputSource _input)
            throws IOException, SAXException {
        input = _input;
    }

    public InputSource getInputSource() {
        return input;
    }
}
