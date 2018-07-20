package org.randomcoder.user;

/**
 * Exception thrown when a requested user cannot be found.
 */
public class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 8212072324579650157L;

	/**
	 * Default constructor.
	 */
	public UserNotFoundException() {
		super();
	}

	/**
	 * Constructor taking an optional message to display.
	 * 
	 * @param message
	 *            message to assoicate with this exception.
	 */
	public UserNotFoundException(String message) {
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
