package com.randomcoder.download.maven;

import java.io.Serializable;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

/**
 * Maven project definition. 
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
public class MavenProject implements Serializable
{
	private static final long serialVersionUID = -3839301096324651724L;
	
	private String projectName;
	private String projectDescription;
	private String directory;
	private Map<String, String> extensionMappings;
	
	/**
	 * Sets the name of the project.
	 * @param projectName project name
	 */
	@Required
	public void setProjectName(String projectName)
	{
		this.projectName = projectName;
	}
		
	/**
	 * Gets the name of the project.
	 * @return project name
	 */
	public String getProjectName()
	{
		return projectName;
	}
	
	/**
	 * Sets the description of the project.
	 * @param projectDescription project description
	 */
	public void setProjectDescription(String projectDescription)
	{
		this.projectDescription = projectDescription;
	}
	
	/**
	 * Gets the description of the project.
	 * @return project description
	 */	
	public String getProjectDescription()
	{
		return projectDescription;
	}
	
	/**
	 * Sets the name of the directory within the repository where the project
	 * metadata can be found.
	 * @param directory project directory
	 */
	@Required
	public void setDirectory(String directory)
	{
		if (!directory.endsWith("/")) directory += "/";
		this.directory = directory;
	}
	
	/**
	 * Gets the name of the directory within the repository where the project
	 * metadata can be found.
	 * @return project directory
	 */
	public String getDirectory()
	{
		return directory;
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
