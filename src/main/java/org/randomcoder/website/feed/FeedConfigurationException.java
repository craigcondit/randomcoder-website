package org.randomcoder.website.feed;

public class FeedConfigurationException extends FeedException {

    private static final long serialVersionUID = 7305739829048095127L;

    public FeedConfigurationException() {
        super();
    }

    public FeedConfigurationException(String message) {
        super(message);
    }

    public FeedConfigurationException(Throwable cause) {
        super(cause);
    }

    public FeedConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
