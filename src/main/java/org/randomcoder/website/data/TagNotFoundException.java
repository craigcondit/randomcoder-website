package org.randomcoder.website.data;

public class TagNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 8212072324579650157L;

    public TagNotFoundException() {
        super();
    }

    public TagNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
