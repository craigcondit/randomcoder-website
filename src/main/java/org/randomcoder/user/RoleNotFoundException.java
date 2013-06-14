package org.randomcoder.user;

/**
 * Exception thrown when a requested role cannot be found.
 */
public class RoleNotFoundException extends RuntimeException
{

	private static final long serialVersionUID = 8212072324579650157L;

	/**
	 * Default constructor.
	 */
	public RoleNotFoundException()
	{
		super();
	}

	/**
	 * Constructor taking an optional message to display.
	 * 
	 * @param message
	 *            message to assoicate with this exception.
	 */
	public RoleNotFoundException(String message)
	{
		super(message);
	}

	/**
	 * Gets the message (if any) associated with this exception.
	 * 
	 * @return message
	 */
	@Override
	public String getMessage()
	{
		return super.getMessage();
	}
}
