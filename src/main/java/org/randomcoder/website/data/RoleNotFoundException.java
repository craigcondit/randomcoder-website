package org.randomcoder.website.data;

public class RoleNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 8212072324579650157L;

    public RoleNotFoundException() {
        super();
    }

    public RoleNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
