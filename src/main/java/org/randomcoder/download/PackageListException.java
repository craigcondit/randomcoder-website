package org.randomcoder.download;

/**
 * Exception thrown if errors occur during package list generation.
 */
public class PackageListException extends Exception
{
	private static final long serialVersionUID = 4298370367619312048L;

	/**
	 * Creates a new exception.
	 */
	public PackageListException()
	{
		super();
	}

	/**
	 * Creates an exception with the given message.
	 * 
	 * @param message
	 *          message to associate with this exception
	 */
	public PackageListException(String message)
	{
		super(message);
	}

	/**
	 * Creates an exception with the given cause.
	 * 
	 * @param cause
	 *          root cause of this exception
	 */
	public PackageListException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Creates an exception with the given message and cause.
	 * 
	 * @param message
	 *          message to associate with this exception
	 * @param cause
	 *          root cause of this exception
	 */
	public PackageListException(String message, Throwable cause)
	{
		super(message, cause);
	}
}