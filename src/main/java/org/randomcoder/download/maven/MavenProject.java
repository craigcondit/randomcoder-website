package org.randomcoder.download.maven;

import java.io.Serializable;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

/**
 * Maven project definition.
 */
public class MavenProject implements Serializable
{
	private static final long serialVersionUID = -3839301096324651724L;

	private String projectName;
	private String projectDescription;
	private String directory;
	private Map<String, String> extensionMappings;

	/**
	 * Sets the name of the project.
	 * 
	 * @param projectName
	 *          project name
	 */
	@Required
	public void setProjectName(String projectName)
	{
		this.projectName = projectName;
	}

	/**
	 * Gets the name of the project.
	 * 
	 * @return project name
	 */
	public String getProjectName()
	{
		return projectName;
	}

	/**
	 * Sets the description of the project.
	 * 
	 * @param projectDescription
	 *          project description
	 */
	public void setProjectDescription(String projectDescription)
	{
		this.projectDescription = projectDescription;
	}

	/**
	 * Gets the description of the project.
	 * 
	 * @return project description
	 */
	public String getProjectDescription()
	{
		return projectDescription;
	}

	/**
	 * Sets the name of the directory within the repository where the project
	 * metadata can be found.
	 * 
	 * @param directory
	 *          project directory
	 */
	@Required
	public void setDirectory(String directory)
	{
		if (!directory.endsWith("/"))
			directory += "/";
		this.directory = directory;
	}

	/**
	 * Gets the name of the directory within the repository where the project
	 * metadata can be found.
	 * 
	 * @return project directory
	 */
	public String getDirectory()
	{
		return directory;
	}

	/**
	 * Sets the mapping of file extensions to artifact types.
	 * 
	 * @param extensionMappings
	 *          extension map
	 */
	@Required
	public void setExtensionMappings(Map<String, String> extensionMappings)
	{
		this.extensionMappings = extensionMappings;
	}

	/**
	 * Gets the mapping of file extensions to artifact types.
	 * 
	 * @return extension map
	 */
	public Map<String, String> getExtensionMappings()
	{
		return extensionMappings;
	}
}
