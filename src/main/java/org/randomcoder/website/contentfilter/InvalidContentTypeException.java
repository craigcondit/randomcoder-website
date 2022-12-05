package org.randomcoder.website.contentfilter;

public class InvalidContentTypeException extends RuntimeException {

    private static final long serialVersionUID = 4715267678027941575L;

    public InvalidContentTypeException(String msg) {
        super(msg);
    }

}
