package org.randomcoder.website.data;

public class ArticleNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 8212072324579650157L;

    public ArticleNotFoundException() {
        super();
    }

    public ArticleNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
