package org.randomcoder.download.http;

import java.io.Serializable;
import java.net.URL;
import java.util.*;

import org.springframework.beans.factory.annotation.Required;

/**
 * HTTP project definition.
 */
public class HttpProject implements Serializable
{
	private static final long serialVersionUID = 8968671673710798968L;

	private String projectName;
	private String projectDescription;
	private String baseName;
	private URL baseUrl;
	private Map<String, String> extensionMappings;
	private List<String> versions;

	/**
	 * Sets the name of this project.
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
	 * Gets the name of this project.
	 * 
	 * @return project name
	 */
	public String getProjectName()
	{
		return projectName;
	}

	/**
	 * Sets the description of this project.
	 * 
	 * @param projectDescription
	 *          project description
	 */
	public void setProjectDescription(String projectDescription)
	{
		this.projectDescription = projectDescription;
	}

	/**
	 * Gets the description of this project.
	 * 
	 * @return project description
	 */
	public String getProjectDescription()
	{
		return projectDescription;
	}

	/**
	 * Sets the base filename.
	 * 
	 * @param baseName
	 *          base filename
	 */
	public void setBaseName(String baseName)
	{
		this.baseName = baseName;
	}

	/**
	 * gets the base filename of artifacts found in this repository.
	 * 
	 * @return base filename
	 */
	public String getBaseName()
	{
		return baseName;
	}

	/**
	 * Sets the base URL for this project (must be a directory).
	 * 
	 * @param baseUrl
	 *          base url
	 */
	@Required
	public void setBaseUrl(URL baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	/**
	 * Gets the base URL for this project.
	 * 
	 * @return base url
	 */
	public URL getBaseUrl()
	{
		return baseUrl;
	}

	/**
	 * Sets a list of versions to query.
	 * 
	 * @param versions
	 *          list of versions
	 */
	public void setVersions(List<String> versions)
	{
		this.versions = versions;
	}

	/**
	 * Gets a list of versions to query.
	 * 
	 * @return list of versions
	 */
	public List<String> getVersions()
	{
		return versions;
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
