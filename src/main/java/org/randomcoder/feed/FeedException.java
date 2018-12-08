package org.randomcoder.feed;

/**
 * Superclass of exceptions thrown during feed generation.
 */
public class FeedException extends Exception {
  private static final long serialVersionUID = 7305739829048095127L;

  /**
   * Creates a new exception.
   */
  public FeedException() {
    super();
  }

  /**
   * Creates a new exception with the given message.
   *
   * @param message error message
   */
  public FeedException(String message) {
    super(message);
  }

  /**
   * Creates a new exception with the given cause.
   *
   * @param cause root cause
   */
  public FeedException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates a new exception with the given message and cause.
   *
   * @param message error message
   * @param cause   root cause
   */
  public FeedException(String message, Throwable cause) {
    super(message, cause);
  }
}
