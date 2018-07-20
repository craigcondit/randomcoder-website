package org.randomcoder.security;

/**
 * Exception thrown when the current user is not authorized for the given
 * action.
 */
public class UnauthorizedException extends RuntimeException {
	private static final long serialVersionUID = -209505911501187042L;

	/**
	 * Default constructor.
	 */
	public UnauthorizedException() {
		super();
	}

	/**
	 * Constructor taking an optional message to display.
	 * 
	 * @param message
	 *            message to assoicate with this exception.
	 */
	public UnauthorizedException(String message) {
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
