package com.randomcoder.download;

import java.io.Serializable;
import java.util.*;

/**
 * JavaBean which holds details for a downloadable package. 
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
public class Package implements Serializable
{
	private static final long serialVersionUID = -7453421374223486173L;
	
	private String name;
	private String description;
	private final List<FileSet> fileSets = new ArrayList<FileSet>();
	
	/**
	 * Gets the name of this package.
	 * @return package name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Sets the name of this package.
	 * @param name package name
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the description of this package.
	 * @return package description
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * Sets the description of this package.
	 * @param description package description
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * Gets the list of file sets for this package.
	 * @return List of FileSet objects
	 */
	public List<FileSet> getFileSets()
	{
		return fileSets;
	}
	
}
