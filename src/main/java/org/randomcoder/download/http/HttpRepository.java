package org.randomcoder.download.http;

import java.io.IOException;
import java.net.*;
import java.util.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.util.*;
import org.apache.commons.logging.*;
import org.randomcoder.download.*;
import org.randomcoder.download.Package;
import org.springframework.beans.factory.*;

/**
 * HTTP repository parser.
 */
public class HttpRepository implements PackageListProducer, InitializingBean, DisposableBean
{
	private static final Log logger = LogFactory.getLog(HttpRepository.class);

	private MultiThreadedHttpConnectionManager connectionManager;
	private List<HttpProject> projects;

	/**
	 * Sets the list of projects to process.
	 * 
	 * @param projects
	 *          list of projects
	 */
	public void setProjects(List<HttpProject> projects)
	{
		this.projects = projects;
	}

	/**
	 * Initializes the repository.
	 * 
	 * @throws Exception
	 *           if an error occurs
	 */
	@Override
	public void afterPropertiesSet() throws Exception
	{
		connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.getParams().setStaleCheckingEnabled(true);
	}

	/**
	 * Destroys the repository.
	 * 
	 * @throws Exception
	 *           if an error occurs
	 */
	@Override
	public void destroy() throws Exception
	{
		connectionManager.shutdown();
	}

	/**
	 * Generates a list of packages from a Maven repository.
	 * 
	 * @throws PackageListException
	 *           if an error occurs
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

			for (HttpProject project : projects)
			{
				// process project
				Package pkg = processProject(client, project);
				if (pkg != null)
					packages.add(pkg);
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

	private Package processProject(HttpClient client, HttpProject project) throws PackageListException
	{
		// get versions
		List<String> versions = project.getVersions();

		// sort in descending version order
		Collections.sort(versions, Collections.reverseOrder(new VersionComparator()));

		Package pkg = new Package();
		pkg.setName(project.getProjectName());
		pkg.setDescription(project.getProjectDescription());

		for (String version : versions)
		{
			FileSet fs = processVersion(client, project, version);
			if (fs != null)
				pkg.getFileSets().add(fs);
		}

		if (pkg.getFileSets().isEmpty())
			return null; // no files for this project

		return pkg;
	}

	private FileSet processVersion(HttpClient client, HttpProject project, String version) throws PackageListException
	{
		// get filenames
		Map<String, String> mappings = project.getExtensionMappings();

		FileSet fs = new FileSet();
		fs.setVersion(version);

		String baseName = project.getBaseName() + "-" + version;
		for (String extension : mappings.keySet())
		{
			FileSpec spec = processFile(client, project.getBaseUrl(), baseName + extension, mappings.get(extension));
			if (spec != null)
				fs.getFiles().add(spec);
		}

		if (fs.getFiles().isEmpty())
			return null;

		Collections.sort(fs.getFiles());
		return fs;
	}

	private FileSpec processFile(HttpClient client, URL baseUrl, String fileName, String fileType) throws PackageListException
	{
		URL fileUrl = null;
		try
		{
			fileUrl = new URL(baseUrl, fileName);
		}
		catch (MalformedURLException e)
		{
			throw new PackageListException("Invalid file URL", e);
		}

		FileSpec spec = new FileSpec();
		if (!statFile(client, spec, fileUrl))
			return null;

		spec.setFileName(fileName);
		spec.setFileType(fileType);
		spec.setDownloadLink(fileUrl.toExternalForm());

		URL md5Url = null;
		try
		{
			md5Url = new URL(baseUrl, fileName + ".md5");
		}
		catch (MalformedURLException e)
		{
			throw new PackageListException("Invalid MD5 URL", e);
		}
		if (statUrl(client, md5Url))
			spec.setMd5Link(md5Url.toExternalForm());

		URL sha1Url = null;
		try
		{
			sha1Url = new URL(baseUrl, fileName + ".sha1");
		}
		catch (MalformedURLException e)
		{
			throw new PackageListException("Invalid SHA-1 URL", e);
		}
		if (statUrl(client, sha1Url))
			spec.setSha1Link(sha1Url.toExternalForm());

		return spec;
	}

	private boolean statUrl(HttpClient client, URL url) throws PackageListException
	{
		HeadMethod head = null;
		try
		{
			// check for existence of file
			head = new HeadMethod(url.toExternalForm());
			int status;
			try
			{
				status = client.executeMethod(head);
			}
			catch (IOException e)
			{
				throw new PackageListException("Error while reading file information", e);
			}
			return status == HttpStatus.SC_OK;
		}
		finally
		{
			if (head != null)
				try
				{
					head.releaseConnection();
				}
				catch (Exception ignored)
				{}
		}
	}

	private boolean statFile(HttpClient client, FileSpec spec, URL fileUrl) throws PackageListException
	{
		HeadMethod head = null;
		try
		{
			// check for existence of file
			head = new HeadMethod(fileUrl.toExternalForm());
			int status;
			try
			{
				status = client.executeMethod(head);
			}
			catch (IOException e)
			{
				throw new PackageListException("Error while reading file information", e);
			}
			if (status != HttpStatus.SC_OK)
				return false;

			// get metadata
			Header contentLength = head.getResponseHeader("Content-Length");
			Header lastModified = head.getResponseHeader("Last-modified");

			spec.setFileSize(-1);
			if (contentLength != null)
			{
				try
				{
					spec.setFileSize(Long.valueOf(contentLength.getValue()));
				}
				catch (NumberFormatException e)
				{
					throw new PackageListException("Unable to parse content length header", e);
				}
			}
			if (lastModified != null)
			{
				try
				{
					spec.setLastModified(DateUtil.parseDate(lastModified.getValue()));
				}
				catch (DateParseException e)
				{
					throw new PackageListException("Unable to parse last modified header", e);
				}
			}
			return true;
		}
		finally
		{
			if (head != null)
				try
				{
					head.releaseConnection();
				}
				catch (Exception ignored)
				{}
		}
	}
}
