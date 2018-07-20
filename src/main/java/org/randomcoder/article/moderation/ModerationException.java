package org.randomcoder.article.moderation;

/**
 * Exception thrown if moderation fails.
 */
public class ModerationException extends Exception {
	private static final long serialVersionUID = -4988739845210718490L;

	/**
	 * Creates a new ModerationException.
	 */
	public ModerationException() {
		super();
	}

	/**
	 * Creates a new ModerationException using the given message and cause.
	 * 
	 * @param message
	 *            message
	 * @param cause
	 *            root cause
	 */
	public ModerationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a new ModerationException using the given message.
	 * 
	 * @param message
	 *            message
	 */
	public ModerationException(String message) {
		super(message);
	}

	/**
	 * Creates a new ModerationException using the given cause.
	 * 
	 * @param cause
	 *            root cause
	 */
	public ModerationException(Throwable cause) {
		super(cause);
	}
}
