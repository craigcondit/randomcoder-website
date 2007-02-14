package com.randomcoder.download;

import java.io.Serializable;
import java.util.*;

/**
 * JavaBean which holds details for a group of downloadable files. 
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
public class FileSet implements Serializable, Cloneable
{
	private static final long serialVersionUID = 4901522219870010077L;
	private String version;
	private final List<FileSpec> files = new ArrayList<FileSpec>();
	
	/**
	 * Gets the version for this group of files.
	 * @return version
	 */
	public String getVersion()
	{
		return version;
	}
	
	/**
	 * Sets the version for this group of files.
	 * @param version version 
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}
	
	/**
	 * Gets the list of files associated with this file set.
	 * @return list of FileSpec objects
	 */
	public List<FileSpec> getFiles()
	{
		return files;
	}

	/**
	 * Clones this object.
	 * @throws CloneNotSupportedException never
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		FileSet target = new FileSet();
		target.version = version;
		for (FileSpec spec : files)
			target.files.add((FileSpec) spec.clone());
		return target;
	}
}
