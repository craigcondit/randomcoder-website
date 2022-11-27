package org.randomcoder.website.contentfilter;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XHTMLErrorHandler implements ErrorHandler {
    private int lineNumber = 1;
    private int columnNumber = 1;
    private String message = null;

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void warning(SAXParseException ex) throws SAXException {
        handle(ex);
        throw ex;
    }

    @Override
    public void error(SAXParseException ex) throws SAXException {
        handle(ex);
        throw ex;
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        handle(ex);
        throw ex;
    }

    private void handle(SAXParseException ex) {
        lineNumber = ex.getLineNumber();
        columnNumber = ex.getColumnNumber();
        message = ex.getMessage();

        // account for prefix
        if (lineNumber == 1) {
            columnNumber -= XHTMLFilter.PREFIX.length();
        }
    }

}
