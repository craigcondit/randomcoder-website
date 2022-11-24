package org.randomcoder.article.comment;

/**
 * Exception thrown when a requested comment cannot be found.
 */
public class CommentNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 8212072324579650157L;

    /**
     * Default constructor.
     */
    public CommentNotFoundException() {
        super();
    }

    /**
     * Constructor taking an optional message to display.
     *
     * @param message message to assoicate with this exception.
     */
    public CommentNotFoundException(String message) {
        super(message);
    }

    /**
     * Gets the message (if any) associated with this exception.
     *
     * @return message
     */
    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
