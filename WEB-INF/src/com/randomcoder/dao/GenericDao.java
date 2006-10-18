package com.randomcoder.dao;

import java.io.Serializable;

/**
 * The basic GenericDao interface with CRUD methods Finders are added with
 * interface inheritance and AOP introductions for concrete implementations
 * 
 * Extended interfaces may declare methods starting with read... find... list...
 * or iterate... They will execute a preconfigured query that is looked up based
 * on the rest of the method name
 * 
 * @author Per Mellqvist (per@mellqvist.name), System architect, Freelance
 * @link http://www-128.ibm.com/developerworks/java/library/j-genericdao.html
 */
public interface GenericDao<T, PK extends Serializable>
{
	/**
	 * Create a new instance of <T>
	 * @param newInstance new class instance to save
	 * @return primary key
	 */
	public PK create(T newInstance);

	/**
	 * Load an instance of <T> by primary key
	 * @param id primary key
	 * @return object instance
	 */
	public T read(PK id);

	/**
	 * Updates an object
	 * @param transientObject object to update
	 */
	public void update(T transientObject);

	/**
	 * Deletes an object
	 * @param persistentObject object to delete
	 */
	public void delete(T persistentObject);
}