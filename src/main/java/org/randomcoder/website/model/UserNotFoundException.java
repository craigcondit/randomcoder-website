package org.randomcoder.website.model;

public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 8212072324579650157L;

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
