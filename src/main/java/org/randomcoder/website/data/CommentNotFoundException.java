package org.randomcoder.website.data;

public class CommentNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 8212072324579650157L;

    public CommentNotFoundException() {
        super();
    }

    public CommentNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
