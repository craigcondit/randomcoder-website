package org.randomcoder.website.feed;

public class FeedException extends Exception {

    private static final long serialVersionUID = 7305739829048095127L;

    public FeedException() {
        super();
    }

    public FeedException(String message) {
        super(message);
    }

    public FeedException(Throwable cause) {
        super(cause);
    }

    public FeedException(String message, Throwable cause) {
        super(message, cause);
    }

}
