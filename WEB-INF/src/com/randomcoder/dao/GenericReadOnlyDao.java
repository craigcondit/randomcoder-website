package com.randomcoder.dao;

import java.io.Serializable;

/**
 * Read-only DAO interface.
 */
public interface GenericReadOnlyDao<T, PK extends Serializable>
{
	/**
	 * Load an instance of &lt;T&gt; by primary key
	 * @param id primary key
	 * @return object instance
	 */
	public T read(PK id);
}