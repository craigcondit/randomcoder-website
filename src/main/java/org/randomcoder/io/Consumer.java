package org.randomcoder.io;

/**
 * Interface used to read state from arbitrary objects.
 * 
 * @param <T>
 *          target type
 */
public interface Consumer<T>
{
	/**
	 * Reads information from the given object.
	 * 
	 * @param target
	 *          target object to read
	 */
	public void consume(T target);
}
