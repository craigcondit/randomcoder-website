package org.randomcoder.download.maven;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.util.*;
import org.apache.commons.logging.*;
import org.randomcoder.download.*;
import org.randomcoder.download.Package;
import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Maven repository parser which reads a remote HTTP repository.
 */
public class RemoteMavenRepository implements PackageListProducer
{
	private static final Log logger = LogFactory.getLog(RemoteMavenRepository.class);
	private static final String METADATA_FILENAME = "maven-metadata.xml";

	private URL url;
	private List<MavenProject> projects;

	/**
	 * Sets the base URL of this repository.
	 * 
	 * @param url
	 *          base URL
	 */
	@Required
	public void setUrl(URL url)
	{
		this.url = url;
	}

	/**
	 * Sets the list of projects to query.
	 * 
	 * @param projects
	 *          list of projects
	 */
	@Required
	public void setProjects(List<MavenProject> projects)
	{
		this.projects = projects;
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
			List<Package> packages = new ArrayList<>();

			HttpClient hc = new HttpClient();
			for (MavenProject project : projects)
			{
				// process project
				Package pkg = processProject(project, hc);
				if (pkg != null)
				{
					packages.add(pkg);
				}
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

	private Package processProject(MavenProject project, HttpClient hc) throws PackageListException
	{
		logger.debug("processProject(" + project.getProjectName() + ")");
		URL projectUrl = getProjectUrl(project);
		URL metadataUrl = getMetadataUrl(projectUrl);

		if (!statUrl(metadataUrl, hc))
		{
			return null;
		}

		MavenMetadataHandler handler = null;
		GetMethod m = null;
		try
		{
			m = new GetMethod(metadataUrl.toExternalForm());
			int result = hc.executeMethod(m);
			if (result < 200 || result > 299)
			{
				throw new PackageListException("Unable to read repository metadata. Got HTTP result " + result);
			}
			
			try (InputStream is = m.getResponseBodyAsStream())
			{
				// parse XML
				XMLReader reader = XMLReaderFactory.createXMLReader();
				handler = new MavenMetadataHandler();
				reader.setContentHandler(handler);
				reader.setErrorHandler(handler);
				reader.parse(new InputSource(is));
			}
		}
		catch (IOException | SAXException e)
		{
			throw new PackageListException("Unable to read repository metadata", e);
		}
		finally
		{
			if (m != null)
			{
				m.releaseConnection();
			}
		}
		
		// get versions
		List<String> versions = handler.getVersions();

		// sort in descending version order
		Collections.sort(versions, Collections.reverseOrder(new VersionComparator()));

		Package pkg = new Package();
		pkg.setName(project.getProjectName());
		pkg.setDescription(project.getProjectDescription());
		pkg.setBaseUrl(projectUrl);

		// do latest version only
		if (versions.size() > 1)
		{
			versions = Collections.singletonList(versions.get(0));
		}
		
		for (String version : versions)
		{
			FileSet fs = processVersion(project, projectUrl, handler.getArtifactId(), version, hc);
			if (fs != null)
			{
				pkg.getFileSets().add(fs);
			}
		}

		if (pkg.getFileSets().isEmpty())
		{
			return null; // no files for this project
		}

		return pkg;
	}

	private FileSet processVersion(MavenProject project, URL projectUrl, String artifactId, String version, HttpClient hc) throws PackageListException
	{
		logger.debug("processVersion(" + project.getProjectName() + "," + projectUrl.toExternalForm() + "," + artifactId + "," + version + ")");
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
			FileSpec spec = processFile(versionUrl, baseName + extension, mappings.get(extension), hc);
			if (spec != null)
			{
				fs.getFiles().add(spec);
			}
		}

		if (fs.getFiles().isEmpty())
		{
			return null;
		}

		Collections.sort(fs.getFiles());
		return fs;
	}

	private FileSpec processFile(URL baseUrl, String fileName, String fileType, HttpClient hc) throws PackageListException
	{
		logger.debug("processFile(" + baseUrl.toExternalForm() + "," + fileName + "," + fileType + ")");
		try
		{
			URL fileUrl = new URL(baseUrl, fileName);
			FileSpec spec = new FileSpec();
			if (!statUrl(spec, fileUrl, hc))
			{
				return null;
			}

			spec.setFileName(fileName);
			spec.setFileType(fileType);
			spec.setDownloadLink(fileUrl.toExternalForm());

			URL md5Url = new URL(baseUrl, fileName + ".md5");
			spec.setMd5Link(md5Url.toExternalForm());

			URL sha1Url = new URL(baseUrl, fileName + ".sha1");
			spec.setSha1Link(sha1Url.toExternalForm());

			return spec;
		}
		catch (MalformedURLException e)
		{
			throw new PackageListException("Invalid repository url", e);
		}
	}

	private boolean statUrl(URL remoteUrl, HttpClient hc)
	{
		return statUrl(null, remoteUrl, hc);
	}
	
	private boolean statUrl(FileSpec spec, URL remoteUrl, HttpClient hc)
	{
		HeadMethod m = null;
		try
		{
			m = new HeadMethod(remoteUrl.toExternalForm());
			int result = hc.executeMethod(m);
			if (result < 200 || result > 299)
			{
				return false;
			}
			
			if (spec != null)
			{
				Header[] lm = m.getResponseHeaders("Last-Modified");
				if (lm.length > 0)
				{
					try
					{
						spec.setLastModified(DateUtil.parseDate(lm[0].getValue()));
					}
					catch (DateParseException e)
					{
						logger.warn("Invalid Last-Modified header on URL " + remoteUrl);
					}
				}
				
				Header[] cl = m.getResponseHeaders("Content-Length");
				if (cl.length > 0)
				{
					try
					{
						spec.setFileSize(Long.parseLong(cl[0].getValue()));
					}
					catch (NumberFormatException e)
					{
						logger.warn("Invalid Content-Length header on URL " + remoteUrl);
					}
				}
			}
			
			return true;
		}
		catch (IOException e)
		{
			logger.warn("HTTP exception reading URL " + remoteUrl.toExternalForm());
			return false;
		}
		finally
		{
			if (m != null)
			{
				m.releaseConnection();
			}
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
			throw new PackageListException("Invald metadata URL", e);
		}
	}
}
