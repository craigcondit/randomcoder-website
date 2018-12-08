package org.randomcoder.feed;

/**
 * Feed exception thrown when configuration is invalid.
 */
public class FeedConfigurationException extends FeedException {
  private static final long serialVersionUID = 7305739829048095127L;

  /**
   * Creates a new exception.
   */
  public FeedConfigurationException() {
    super();
  }

  /**
   * Creates a new exception with the given message.
   *
   * @param message error message
   */
  public FeedConfigurationException(String message) {
    super(message);
  }

  /**
   * Creates a new exception with the given cause.
   *
   * @param cause root cause
   */
  public FeedConfigurationException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates a new exception with the given message and cause.
   *
   * @param message error message
   * @param cause   root cause
   */
  public FeedConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
