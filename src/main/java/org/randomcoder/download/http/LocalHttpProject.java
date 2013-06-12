package org.randomcoder.download.http;

import java.io.File;

import org.springframework.beans.factory.annotation.Required;

/**
 * HTTP project definition read from local files.
 */
public class LocalHttpProject extends HttpProject
{
	private static final long serialVersionUID = 5043435243692100091L;
	private File baseDir;

	/**
	 * Sets the base directory of this repository.
	 * 
	 * @param baseDir
	 *          base directory to load files from
	 */
	@Required
	public void setBaseDir(File baseDir)
	{
		this.baseDir = baseDir;
	}

	/**
	 * Gets the base directory of this repository.
	 * 
	 * @return base directory to load files from
	 */
	public File getBaseDir()
	{
		return baseDir;
	}
}
