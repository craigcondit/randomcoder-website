package org.randomcoder.io;

/**
 * Interface used to write state to arbitrary objects.
 * 
 * @param <T>
 *          target type
 */
public interface Producer<T>
{
	/**
	 * Writes information to the given object.
	 * 
	 * @param target
	 *          target object to write
	 */
	public void produce(T target);
}
