package com.randomcoder.download;

import java.io.Serializable;
import java.util.Date;

/**
 * JavaBean which holds details for a downloadable file. 
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
public class FileSpec implements Serializable, Cloneable
{
	private static final long serialVersionUID = 1667518184832349650L;
	
	private String downloadLink;
	private long fileSize = -1;
	private String md5Link = null;
	private String sha1Link = null;
	private String fileName;
	private Date lastModified = null;
	private String fileType;
	
	/**
	 * Gets the URL where this file may be downloaded.
	 * @return download url
	 */
	public String getDownloadLink()
	{
		return downloadLink;
	}
	
	/** Sets the URL where this file may be downloaded.
	 * @param downloadLink download url
	 */
	public void setDownloadLink(String downloadLink)
	{
		this.downloadLink = downloadLink;
	}
	
	/**
	 * Gets the file size in bytes, or -1 if unknown.
	 * @return file size
	 */
	public long getFileSize()
	{
		return fileSize;
	}
	
	/**
	 * Sets the file size in bytes, or -1 if unknown.
	 * @param fileSize 
	 */
	public void setFileSize(long fileSize)
	{
		this.fileSize = fileSize;
	}
	
	/**
	 * Gets the URL where an MD5 checksum of this file may be located,
	 * or null if no MD5 checksum is available.
	 * @return md5 checksum url
	 */
	public String getMd5Link()
	{
		return md5Link;
	}
	
	/**
	 * Gets the URL where an MD5 checksum of this file may be located,
	 * or null if no MD5 checksum is available.
	 * @param md5Link md5 checksum url
	 */
	public void setMd5Link(String md5Link)
	{
		this.md5Link = md5Link;
	}
	
	/**
	 * Gets the URL where an SHA-1 checksum of this file may be located,
	 * or null if no SHA-1 checksum is available.
	 * @return sha1 checksum url
	 */	
	public String getSha1Link()
	{
		return sha1Link;
	}
	
	/**
	 * Gets the URL where an SHA-1 checksum of this file may be located,
	 * or null if no SHA-1 checksum is available.
	 * @param sha1Link sha1 checksum url
	 */
	public void setSha1Link(String sha1Link)
	{
		this.sha1Link = sha1Link;
	}
	
	/**
	 * Gets the name of this file (without path information).
	 * @return file name
	 */
	public String getFileName()
	{
		return fileName;
	}
	
	/**
	 * Sets the name of this file (without path information).
	 * @param fileName file name
	 */
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	/**
	 * Gets the last modified date of this file, or null if unknown. 
	 * @return last modified date
	 */
	public Date getLastModified()
	{
		return lastModified;
	}
	
	/**
	 * Sets the last modified date of this file, or null if unknown.
	 * @param lastModified last modified date
	 */
	public void setLastModified(Date lastModified)
	{
		this.lastModified = lastModified;
	}
	
	
	/**
	 * Gets the type of this file.
	 * @return file type
	 */
	public String getFileType()
	{
		return fileType;
	}
	
	/**
	 * Sets the type of this file.
	 * @param fileType file type
	 */
	public void setFileType(String fileType)
	{
		this.fileType = fileType;
	}

	/**
	 * Clones this object.
	 * @throws CloneNotSupportedException never
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		FileSpec target = new FileSpec();
		target.downloadLink = downloadLink;
		target.fileName = fileName;
		target.fileSize = fileSize;
		target.fileType = fileType;
		if (lastModified != null)
			target.lastModified = new Date(lastModified.getTime());
		target.md5Link = md5Link;
		target.sha1Link = sha1Link;
		return target;
	}	
	
	
}