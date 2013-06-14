package org.randomcoder.mvc;

import java.io.Serializable;

/**
 * Command class used whenever only an id is required.
 */
public class IdCommand implements Serializable
{
	private static final long serialVersionUID = 7923885684126995244L;

	private Long id;

	/**
	 * Sets the object id.
	 * 
	 * @param id
	 *          object id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the object id.
	 * 
	 * @return object id
	 */
	public Long getId()
	{
		return id;
	}
}
