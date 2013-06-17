package org.randomcoder.download;

import java.io.Serializable;
import java.util.*;

/**
 * JavaBean which holds details for a group of downloadable files.
 */
public class FileSet implements Serializable, Cloneable
{
	private static final long serialVersionUID = 4901522219870010077L;
	private String version;
	private final List<FileSpec> files = new ArrayList<>();

	/**
	 * Gets the version for this group of files.
	 * 
	 * @return version
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * Sets the version for this group of files.
	 * 
	 * @param version
	 *          version
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}

	/**
	 * Gets the list of files associated with this file set.
	 * 
	 * @return list of FileSpec objects
	 */
	public List<FileSpec> getFiles()
	{
		return files;
	}

	/**
	 * Clones this object.
	 * 
	 * @throws CloneNotSupportedException
	 *           never
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		FileSet target = new FileSet();
		target.version = version;
		for (FileSpec spec : files)
		{
			target.files.add((FileSpec) spec.clone());
		}
		
		return target;
	}
}