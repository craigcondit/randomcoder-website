package org.randomcoder.download;

import java.io.Serializable;
import java.util.*;

/**
 * JavaBean which holds details for an available file.
 */
public class FileSpec implements Serializable, Cloneable, Comparable<FileSpec>
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
	 * Gets the URL where this file may be retrieved.
	 * 
	 * @return download URL
	 */
	public String getDownloadLink()
	{
		return downloadLink;
	}

	/**
	 * Sets the URL where this file may be retrieved.
	 * 
	 * @param downloadLink
	 *          download URL
	 */
	public void setDownloadLink(String downloadLink)
	{
		this.downloadLink = downloadLink;
	}

	/**
	 * Gets the file size in bytes, or -1 if unknown.
	 * 
	 * @return file size
	 */
	public long getFileSize()
	{
		return fileSize;
	}

	/**
	 * Sets the file size in bytes, or -1 if unknown.
	 * 
	 * @param fileSize
	 *          file size
	 */
	public void setFileSize(long fileSize)
	{
		this.fileSize = fileSize;
	}

	/**
	 * Gets the URL where an MD5 checksum of this file may be located, or null if
	 * no MD5 checksum is available.
	 * 
	 * @return md5 checksum url
	 */
	public String getMd5Link()
	{
		return md5Link;
	}

	/**
	 * Gets the URL where an MD5 checksum of this file may be located, or null if
	 * no MD5 checksum is available.
	 * 
	 * @param md5Link
	 *          md5 checksum url
	 */
	public void setMd5Link(String md5Link)
	{
		this.md5Link = md5Link;
	}

	/**
	 * Gets the URL where an SHA-1 checksum of this file may be located, or null
	 * if no SHA-1 checksum is available.
	 * 
	 * @return sha1 checksum url
	 */
	public String getSha1Link()
	{
		return sha1Link;
	}

	/**
	 * Gets the URL where an SHA-1 checksum of this file may be located, or null
	 * if no SHA-1 checksum is available.
	 * 
	 * @param sha1Link
	 *          sha1 checksum url
	 */
	public void setSha1Link(String sha1Link)
	{
		this.sha1Link = sha1Link;
	}

	/**
	 * Gets the name of this file (without path information).
	 * 
	 * @return file name
	 */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * Sets the name of this file (without path information).
	 * 
	 * @param fileName
	 *          file name
	 */
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	/**
	 * Gets the last modified date of this file, or null if unknown.
	 * 
	 * @return last modified date
	 */
	public Date getLastModified()
	{
		return lastModified;
	}

	/**
	 * Sets the last modified date of this file, or null if unknown.
	 * 
	 * @param lastModified
	 *          last modified date
	 */
	public void setLastModified(Date lastModified)
	{
		this.lastModified = lastModified;
	}

	/**
	 * Gets the type of this file.
	 * 
	 * @return file type
	 */
	public String getFileType()
	{
		return fileType;
	}

	/**
	 * Sets the type of this file.
	 * 
	 * @param fileType
	 *          file type
	 */
	public void setFileType(String fileType)
	{
		this.fileType = fileType;
	}

	/**
	 * Compares this FileSpec to another FileSpec.
	 * 
	 * @param obj
	 *          FileSpec to compare
	 * @return negative, 0, or positive number depending on if this object is
	 *         before, equal, or after <code>obj</code>
	 */
	@Override
	public int compareTo(FileSpec obj)
	{
		if (obj == null)
		{
			return 1;
		}

		// we employ a hack here to make periods sort earlier than hyphens
		// this tends to bubble the important stuff (.jar, etc.) to the top
		return fileName.toLowerCase(Locale.US).replace('.', '*').compareTo(obj.fileName.toLowerCase(Locale.US).replace('.', '*'));
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
		FileSpec target = new FileSpec();
		target.downloadLink = downloadLink;
		target.fileName = fileName;
		target.fileSize = fileSize;
		target.fileType = fileType;
		if (lastModified != null)
		{
			target.lastModified = new Date(lastModified.getTime());
		}
		target.md5Link = md5Link;
		target.sha1Link = sha1Link;
		return target;
	}
}