package org.randomcoder.download.http;

import java.io.File;
import java.net.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.randomcoder.download.*;
import org.randomcoder.download.Package;

/**
 * HTTP repository parser which reads from local files.
 */
public class LocalHttpRepository implements PackageListProducer
{
	private static final Log logger = LogFactory.getLog(LocalHttpRepository.class);

	private List<LocalHttpProject> projects;

	/**
	 * Sets the list of projects to process.
	 * 
	 * @param projects
	 *          list of projects
	 */
	public void setProjects(List<LocalHttpProject> projects)
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
			List<Package> packages = new ArrayList<Package>();

			for (LocalHttpProject project : projects)
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

	private Package processProject(LocalHttpProject project) throws PackageListException
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
			FileSet fs = processVersion(project, version);
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

	private FileSet processVersion(LocalHttpProject project, String version) throws PackageListException
	{
		// get filenames
		Map<String, String> mappings = project.getExtensionMappings();

		FileSet fs = new FileSet();
		fs.setVersion(version);

		String baseName = project.getBaseName() + "-" + version;

		for (String extension : mappings.keySet())
		{
			FileSpec spec = processFile(project.getBaseDir(), project.getBaseUrl(), baseName + extension, mappings.get(extension));
			if (spec != null)
			{
				fs.getFiles().add(spec);
			}
		}

		if (fs.getFiles().isEmpty())
			return null;

		Collections.sort(fs.getFiles());
		return fs;
	}

	private FileSpec processFile(File baseDir, URL baseUrl, String fileName, String fileType) throws PackageListException
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

		File file = new File(baseDir, fileName);

		FileSpec spec = new FileSpec();
		if (!statFile(spec, file))
		{
			return null;
		}

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
		File md5File = new File(baseDir, fileName + ".md5");
		if (statUrl(md5File))
		{
			spec.setMd5Link(md5Url.toExternalForm());
		}

		URL sha1Url = null;
		try
		{
			sha1Url = new URL(baseUrl, fileName + ".sha1");
		}
		catch (MalformedURLException e)
		{
			throw new PackageListException("Invalid SHA-1 URL", e);
		}
		File sha1File = new File(baseDir, fileName + ".sha1");
		if (statUrl(sha1File))
		{
			spec.setSha1Link(sha1Url.toExternalForm());
		}

		return spec;
	}

	private boolean statUrl(File file)
	{
		return file.exists();
	}

	private boolean statFile(FileSpec spec, File file)
	{
		if (!file.exists())
		{
			return false;
		}

		spec.setFileSize(file.length());
		spec.setLastModified(new Date(file.lastModified()));

		return true;
	}
}
