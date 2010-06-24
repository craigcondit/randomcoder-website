package com.randomcoder.download.maven;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.util.*;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import com.randomcoder.download.*;
import com.randomcoder.download.Package;

/**
 * Maven repository parser. 
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
public class MavenRepository implements PackageListProducer, InitializingBean, DisposableBean
{
	private static final Log logger = LogFactory.getLog(MavenRepository.class);
	private static final String METADATA_FILENAME = "maven-metadata.xml";
	
	private URL url;
	private List<MavenProject> projects;
	
	private MultiThreadedHttpConnectionManager connectionManager;
	
	/**
	 * Sets the base url of this repository.
	 * @param url base url
	 */
	@Required
	public void setUrl(URL url)
	{
		this.url = url;
	}
	
	/**
	 * Sets the list of projects to query.
	 * @param projects list of projects
	 */
	@Required
	public void setProjects(List<MavenProject> projects)
	{
		this.projects = projects;
	}

	/**
	 * Initializes the repository.
	 * @throws Exception if an error occurs
	 */
	@Override
	public void afterPropertiesSet() throws Exception
	{
		connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.getParams().setStaleCheckingEnabled(true);
	}
	
	/**
	 * Destroys the repository.
	 * @throws Exception if an error occurs
	 */
	@Override
	public void destroy() throws Exception
	{
		connectionManager.shutdown();
	}
	
	/**
	 * Generates a list of packages from a Maven repository.
	 * @throws PackageListException if an error occurs
	 * @return List of Package objects
	 */
	@Override
	public List<Package> getPackages() throws PackageListException
	{
		logger.debug("Loading package list");
		long startTime = logger.isDebugEnabled() ? System.nanoTime() : 0;
		
		try
		{
			List<Package> packages = new ArrayList<Package>();
			
			// create http client
			HttpClient client = new HttpClient(connectionManager);
			
			for (MavenProject project : projects)
			{
				// process project
				Package pkg = processProject(client, project);
				if (pkg != null) packages.add(pkg);
			}
			
			// sort by name
			Collections.sort(packages);
			
			return packages;
		}
		finally
		{
			if (logger.isDebugEnabled())
			{
				long endTime = System.nanoTime();
				double elapsedTime = ((double) (endTime - startTime)) / 1000000000;
				logger.debug("Package list completed in " + elapsedTime + " seconds");
			}			
		}
	}

	private Package processProject(HttpClient client, MavenProject project) throws PackageListException
	{
		URL projectUrl = getProjectUrl(project);
		URL metadataUrl = getMetadataUrl(projectUrl);
		
		GetMethod get = null;
		InputStream is = null;
		try
		{
			get = new GetMethod(metadataUrl.toExternalForm());
			get.setFollowRedirects(false);
			
			int status = client.executeMethod(get);			
			if (status != HttpStatus.SC_OK) return null;

			is = get.getResponseBodyAsStream();
			
			// parse XML
			XMLReader reader = XMLReaderFactory.createXMLReader();
			MavenMetadataHandler handler = new MavenMetadataHandler();
			reader.setContentHandler(handler);
			reader.setErrorHandler(handler);
			reader.parse(new InputSource(is));
			
			// get versions
			List<String> versions = handler.getVersions();
			
			// sort in descending version order
			Collections.sort(versions, Collections.reverseOrder(new VersionComparator()));
						
			Package pkg = new Package();
			pkg.setName(project.getProjectName());
			pkg.setDescription(project.getProjectDescription());
			
			for (String version : versions)
			{
				FileSet fs = processVersion(client, project, projectUrl, handler.getArtifactId(), version);
				if (fs != null) pkg.getFileSets().add(fs);
			}
			
			if (pkg.getFileSets().isEmpty()) return null; // no files for this project
			
			return pkg;
		}
		catch (IOException e)
		{
			throw new PackageListException("Unable to read repository metadata", e);
		}
		catch (SAXException e)
		{
			throw new PackageListException("Unable to parse repository metadata", e);
		}
		finally
		{
			if (is != null) try { is.close(); } catch (Exception ignored) {}
			if (get != null) try { get.releaseConnection(); } catch (Exception ignored) {}
		}
	}
	
	private FileSet processVersion(HttpClient client, MavenProject project, URL projectUrl, String artifactId, String version)
	throws PackageListException
	{
		// get filenames
		Map<String, String> mappings = project.getExtensionMappings();
		
		FileSet fs = new FileSet();
		fs.setVersion(version);
		
		URL versionUrl = null;
		try
		{
			versionUrl = new URL(projectUrl, version + "/");
		}
		catch (MalformedURLException e)
		{
			throw new PackageListException("Invalid version URL", e);
		}
		
		String baseName = artifactId + "-" + version;
		for (String extension : mappings.keySet())
		{
			FileSpec spec = processFile(client, versionUrl, baseName + extension, mappings.get(extension));
			if (spec != null) fs.getFiles().add(spec);
		}
		
		if (fs.getFiles().isEmpty()) return null;
		
		Collections.sort(fs.getFiles());
		return fs;
	}

	private FileSpec processFile(HttpClient client, URL baseUrl, String fileName, String fileType)
	throws PackageListException
	{		
		try
		{
			URL fileUrl = new URL(baseUrl, fileName);
			
			FileSpec spec = new FileSpec();
			if (!statFile(client, spec, fileUrl)) return null;
			
			spec.setFileName(fileName);
			spec.setFileType(fileType);
			spec.setDownloadLink(fileUrl.toExternalForm());
			
			URL md5Url = new URL(baseUrl, fileName + ".md5");
			if (statUrl(client, md5Url)) spec.setMd5Link(md5Url.toExternalForm());
			
			URL sha1Url = new URL(baseUrl, fileName + ".sha1");
			if (statUrl(client, sha1Url)) spec.setSha1Link(sha1Url.toExternalForm());
			return spec;
		}
		catch (MalformedURLException e)
		{
			throw new PackageListException("Invalid repository url", e);
		}
	}
	
	private boolean statUrl(HttpClient client, URL _url)
	throws PackageListException
	{
		HeadMethod head = null;
		try
		{
			// check for existence of file
			head = new HeadMethod(_url.toExternalForm());
			return client.executeMethod(head) == HttpStatus.SC_OK;
		} 
		catch (IOException e)
		{
			throw new PackageListException("Error while reading file information", e);
		}
		finally
		{
			if (head != null) try { head.releaseConnection(); } catch (Exception ignored) {}			
		}
	}

	private boolean statFile(HttpClient client, FileSpec spec, URL fileUrl)
	throws PackageListException
	{
		HeadMethod head = null;
		try
		{
			// check for existence of file
			head = new HeadMethod(fileUrl.toExternalForm());
			if (client.executeMethod(head) != HttpStatus.SC_OK) return false;
			
			// get metadata
			Header contentLength = head.getResponseHeader("Content-Length");
			Header lastModified = head.getResponseHeader("Last-modified");
			
			spec.setFileSize(-1);
			if (contentLength != null)
				spec.setFileSize(Long.valueOf(contentLength.getValue()));

			if (lastModified != null)
				spec.setLastModified(DateUtil.parseDate(lastModified.getValue()));
			
			return true;
		}
		catch (IOException e)
		{
			throw new PackageListException("Error while reading file information", e);
		}
		catch (NumberFormatException e)
		{
			throw new PackageListException("Unable to parse content length header", e);
		}
		catch (DateParseException e)
		{
			throw new PackageListException("Unable to parse last modified header", e);
		}
		finally
		{
			if (head != null) try { head.releaseConnection(); } catch (Exception ignored) {}
		}
	}
	
	private URL getProjectUrl(MavenProject project) throws PackageListException
	{
		try
		{
			return new URL(url, project.getDirectory());
		}
		catch (MalformedURLException e)
		{
			throw new PackageListException("Invalid project URL", e);
		}
	}
	
	private URL getMetadataUrl(URL projectUrl) throws PackageListException
	{
		try
		{
			return new URL(projectUrl, METADATA_FILENAME);
		}
		catch (MalformedURLException e)
		{
			throw new PackageListException("Invalid metadata URL", e);
		}
	}
}