package org.randomcoder.download.maven;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.randomcoder.download.*;
import org.randomcoder.download.Package;
import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Maven repository parser which reads local files.
 */
public class LocalMavenRepository implements PackageListProducer
{
	private static final Log logger = LogFactory.getLog(LocalMavenRepository.class);
	private static final String METADATA_FILENAME = "maven-metadata.xml";
	private static final String LOCAL_METADATA_FILENAME = "maven-metadata-local.xml";

	private URL url;
	private File dir;
	private List<MavenProject> projects;

	/**
	 * Sets the base url of this repository.
	 * 
	 * @param url
	 *          base url
	 */
	@Required
	public void setUrl(URL url)
	{
		this.url = url;
	}

	/**
	 * Sets the local directory which contains this repository.
	 * 
	 * @param dir
	 *          base dir
	 */
	@Required
	public void setDir(File dir)
	{
		this.dir = dir;
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

			for (MavenProject project : projects)
			{
				// process project
				Package pkg = processProject(project);
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

	private Package processProject(MavenProject project) throws PackageListException
	{
		URL projectUrl = getProjectUrl(project);

		File projectFile = getProjectFile(project);
		File metadataFile = getMetadataFile(projectFile);

		if (metadataFile == null)
		{
			return null;
		}

		try (InputStream is = new FileInputStream(metadataFile))
		{
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
				FileSet fs = processVersion(project, projectUrl, projectFile, handler.getArtifactId(), version);
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
		catch (IOException e)
		{
			throw new PackageListException("Unable to read repository metadata", e);
		}
		catch (SAXException e)
		{
			throw new PackageListException("Unable to parse repository metadata", e);
		}
	}

	private FileSet processVersion(MavenProject project, URL projectUrl, File projectFile, String artifactId, String version) throws PackageListException
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
		File versionFile = new File(projectFile, version);

		String baseName = artifactId + "-" + version;
		for (String extension : mappings.keySet())
		{
			FileSpec spec = processFile(versionUrl, versionFile, baseName + extension, mappings.get(extension));
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

	private FileSpec processFile(URL baseUrl, File baseFile, String fileName, String fileType) throws PackageListException
	{
		try
		{
			URL fileUrl = new URL(baseUrl, fileName);
			File file = new File(baseFile, fileName);
			FileSpec spec = new FileSpec();
			if (!statFile(spec, file))
			{
				return null;
			}

			spec.setFileName(fileName);
			spec.setFileType(fileType);
			spec.setDownloadLink(fileUrl.toExternalForm());

			URL md5Url = new URL(baseUrl, fileName + ".md5");
			File md5File = new File(baseFile, fileName + ".md5");
			if (statUrl(md5File))
			{
				spec.setMd5Link(md5Url.toExternalForm());
			}

			URL sha1Url = new URL(baseUrl, fileName + ".sha1");
			File sha1File = new File(baseFile, fileName + ".sha1");
			if (statUrl(sha1File))
			{
				spec.setSha1Link(sha1Url.toExternalForm());
			}

			return spec;
		}
		catch (MalformedURLException e)
		{
			throw new PackageListException("Invalid repository url", e);
		}
	}

	private boolean statUrl(File file)
	{
		return file.exists();
	}

	private boolean statFile(FileSpec spec, File file)
	{
		if (!file.exists())
			return false;

		spec.setFileSize(file.length());
		spec.setLastModified(new Date(file.lastModified()));

		return true;
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

	private File getProjectFile(MavenProject project)
	{
		return new File(dir, project.getDirectory());
	}

	private File getMetadataFile(File projectFile)
	{
		File meta = null;
		meta = new File(projectFile, METADATA_FILENAME);
		if (meta.exists() && meta.canRead())
		{
			return meta;
		}

		meta = new File(projectFile, LOCAL_METADATA_FILENAME);
		if (meta.exists() && meta.canRead())
		{
			return meta;
		}

		return null;
	}
}
