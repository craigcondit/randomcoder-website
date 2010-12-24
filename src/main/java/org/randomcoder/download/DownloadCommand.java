package org.randomcoder.download;

import java.io.Serializable;

/**
 * Command object which customizes the download page. 
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
public class DownloadCommand implements Serializable
{
	private static final long serialVersionUID = 7013166533979747193L;
	
	private String packageName;
	private boolean showAll;
	
	/**
	 * Sets the package name to restrict to.
	 * @param packageName package name
	 */
	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}
	
	/**
	 * Gets the package name to restrict to.
	 * @return package name
	 */
	public String getPackageName()
	{
		return packageName;
	}
	
	/**
	 * Sets whether all package versions should be shown.
	 * @param showAll true if all versions should be shown, false otherwise
	 */
	public void setShowAll(boolean showAll)
	{
		this.showAll = showAll;
	}
	
	/**
	 * Determines if all package versions should be shown.
	 * @return true if all versions should be shown, false otherwise
	 */
	public boolean isShowAll()
	{
		return showAll;
	}
	
}
