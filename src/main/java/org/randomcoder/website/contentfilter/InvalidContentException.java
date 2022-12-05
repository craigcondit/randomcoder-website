package org.randomcoder.website.contentfilter;

public class InvalidContentException extends Exception {

    private static final long serialVersionUID = 106795571729597774L;

    private final int lineNumber;
    private final int columnNumber;

    public InvalidContentException(String msg, int lineNumber, int columnNumber) {
        super(msg);
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    private String cleanMessage(String message) {
        if (message == null) {
            return null;
        }

        // do some pattern replacements
        message = message.replaceAll("^cvc[A-Za-z0-9\\-\\.]+:\\s*", "");
        message = message.replaceAll("One of '(.)*' is expected.$", "");

        return message;
    }

    @Override
    public String getMessage() {
        // try to clean it up
        return cleanMessage(super.getMessage());
    }

    @Override
    public String toString() {
        return String.format("Line %d, column %d: %s", getLineNumber(), getColumnNumber(), getMessage());
    }

}
