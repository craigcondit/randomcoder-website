package org.randomcoder.download.http;

import java.io.Serializable;
import java.net.URL;
import java.util.*;

import org.springframework.beans.factory.annotation.Required;

/**
 * HTTP project definition. 
 * 
 * <pre>
 * Copyright (c) 2007, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
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
	 * @param projectName project name
	 */
	@Required
	public void setProjectName(String projectName)
	{
		this.projectName = projectName;
	}
	
	/**
	 * Gets the name of this project.
	 * @return project name
	 */
	public String getProjectName()
	{
		return projectName;
	}
	
	/**
	 * Sets the description of this project.
	 * @param projectDescription project description
	 */
	public void setProjectDescription(String projectDescription)
	{
		this.projectDescription = projectDescription;
	}
	
	/**
	 * Gets the description of this project.
	 * @return project description
	 */
	public String getProjectDescription()
	{
		return projectDescription;
	}
	
	/**
	 * Sets the base filename.
	 * @param baseName base filename
	 */
	public void setBaseName(String baseName)
	{
		this.baseName = baseName;
	}
	
	/**
	 * gets the base filename of artifacts found in this repository.
	 * @return base filename
	 */
	public String getBaseName()
	{
		return baseName;
	}
	
	/**
	 * Sets the base URL for this project (must be a directory).
	 * @param baseUrl base url
	 */
	@Required
	public void setBaseUrl(URL baseUrl)
	{
		this.baseUrl = baseUrl;
	}
	
	/**
	 * Gets the base URL for this project.
	 * @return base url
	 */
	public URL getBaseUrl()
	{
		return baseUrl;
	}
	
	/**
	 * Sets a list of versions to query.
	 * @param versions list of versions
	 */
	public void setVersions(List<String> versions)
	{
		this.versions = versions;
	}
	
	/**
	 * Gets a list of versions to query.
	 * @return list of versions
	 */
	public List<String> getVersions()
	{
		return versions;
	}
	
	/**
	 * Sets the mapping of file extensions to artifact types.
	 * @param extensionMappings extension map
	 */
	@Required
	public void setExtensionMappings(Map<String, String> extensionMappings)
	{
		this.extensionMappings = extensionMappings;
	}
	
	/**
	 * Gets the mapping of file extensions to artifact types. 
	 * @return extension map
	 */
	public Map<String, String> getExtensionMappings()
	{
		return extensionMappings;
	}		
}
