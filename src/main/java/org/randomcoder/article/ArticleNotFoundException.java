package org.randomcoder.article;

/**
 * Exception thrown when a requested article cannot be found.
 */
public class ArticleNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 8212072324579650157L;

  /**
   * Default constructor.
   */
  public ArticleNotFoundException() {
    super();
  }

  /**
   * Constructor taking an optional message to display.
   *
   * @param message message to assoicate with this exception.
   */
  public ArticleNotFoundException(String message) {
    super(message);
  }

  /**
   * Gets the message (if any) associated with this exception.
   *
   * @return message
   */
  @Override public String getMessage() {
    return super.getMessage();
  }
}
