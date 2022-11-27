package org.randomcoder.website.data;

public class ModerationException extends Exception {
    private static final long serialVersionUID = -4988739845210718490L;

    public ModerationException() {
        super();
    }

    public ModerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModerationException(String message) {
        super(message);
    }

    public ModerationException(Throwable cause) {
        super(cause);
    }

}
