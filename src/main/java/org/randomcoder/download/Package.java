package org.randomcoder.download;

import java.io.Serializable;
import java.util.*;

/**
 * JavaBean which holds details for an available package.
 */
public class Package implements Serializable, Comparable<Package>
{
	private static final long serialVersionUID = -7453421374223486173L;

	private String name;
	private String description;
	private final List<FileSet> fileSets = new ArrayList<>();

	/**
	 * Gets the name of this package.
	 * 
	 * @return package name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this package.
	 * 
	 * @param name
	 *          package name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the description of this package.
	 * 
	 * @return package description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description of this package.
	 * 
	 * @param description
	 *          package description
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Gets the list of file sets for this package.
	 * 
	 * @return List of FileSet objects
	 */
	public List<FileSet> getFileSets()
	{
		return fileSets;
	}

	/**
	 * Compares this package to another package by name.
	 * 
	 * @param obj
	 *          package to compare
	 * @return a negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(Package obj)
	{
		if (obj == null)
		{
			return 1;
		}
		return name.compareTo(obj.name);
	}
}