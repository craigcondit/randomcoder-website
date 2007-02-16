package com.randomcoder.download.http;

import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import junit.framework.TestCase;

import com.randomcoder.download.*;
import com.randomcoder.download.Package;
import com.randomcoder.test.TestHttpServer;

public class HttpRepositoryTest extends TestCase
{
	private HttpRepository repo;
	private MockHttpServer server;
	protected URL baseUrl;
	protected Date lastModified;
	private int port;
	
	@Override
	protected void setUp() throws Exception
	{
		lastModified = new Date();
		server = new MockHttpServer();
		port = server.getPort();
		
		repo = new HttpRepository();
		List<HttpProject> projects = new ArrayList<HttpProject>();
		
		baseUrl = new URL("http://localhost:" + port + "/");
		
		HttpProject project = new HttpProject();
		project.setProjectName("test");
		project.setProjectDescription("test-description");
		project.setBaseUrl(baseUrl);
		project.setBaseName("test");
		
		Map<String, String> extensionMappings = new HashMap<String, String>();
		extensionMappings.put(".jar", "jar");		
		extensionMappings.put("-javadoc.jar", "javadoc");		
		project.setExtensionMappings(extensionMappings);
		
		List<String> versions = new ArrayList<String>();
		versions.add("1.0.0");
		versions.add("1.0.1");
		versions.add("1.0.1-0-not-found"); // won't exist
		versions.add("1.0.1-0-not-found-again"); // won't exist
		project.setVersions(versions);
		
		projects.add(project);
		
		repo.setProjects(projects);
		repo.afterPropertiesSet();
	}

	@Override
	protected void tearDown() throws Exception
	{
		server.destroy();
		repo.destroy();
		
		lastModified = null;
		server = null;
		baseUrl = null;
		repo = null;
	}
	
	public void testGetPackages() throws Exception
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		List<Package> packages = repo.getPackages();
		assertNotNull("Null package list", packages);
		assertEquals("Wrong size", 1, packages.size());
		Package pkg = packages.get(0);
		assertNotNull("Null package", pkg);
		assertEquals("Wrong name", "test", pkg.getName());
		assertEquals("Wrong description", "test-description", pkg.getDescription());
		List<FileSet> fileSets = pkg.getFileSets();
		assertNotNull("Null filesets", fileSets);
		assertEquals("Wrong fileset size", 2, fileSets.size());
		{
			FileSet fs = fileSets.get(0);
			assertNotNull("Null fileset", fs);
			assertEquals("Wrong version", "1.0.1", fs.getVersion());
			List<FileSpec> files = fs.getFiles();
			assertNotNull("Null files", files);
			assertEquals("Wrong file count", 1, files.size());
			FileSpec file = files.get(0);
			assertNotNull("Null file", file);
			assertEquals("Wrong download link", "http://localhost:" + port + "/test-1.0.1.jar", file.getDownloadLink());
			assertEquals("Wrong filename", "test-1.0.1.jar", file.getFileName());
			assertEquals("Wrong file size", 23456789, file.getFileSize());
			assertEquals("Wrong file time", sdf.format(lastModified), sdf.format(file.getLastModified()));
			assertNull("MD5 link specified", file.getMd5Link());
			assertNull("SHA-1 link specified", file.getSha1Link());
			assertEquals("Wrong file type", "jar", file.getFileType());
		}
		{
			FileSet fs = fileSets.get(1);
			assertNotNull("Null fileset", fs);
			assertEquals("Wrong version", "1.0.0", fs.getVersion());
			List<FileSpec> files = fs.getFiles();
			assertNotNull("Null files", files);
			assertEquals("Wrong file count", 2, files.size());
			FileSpec file = files.get(0);
			assertNotNull("Null file", file);
			assertEquals("Wrong download link", "http://localhost:" + port + "/test-1.0.0.jar", file.getDownloadLink());
			assertEquals("Wrong filename", "test-1.0.0.jar", file.getFileName());
			assertEquals("Wrong file size", 12345678, file.getFileSize());
			assertEquals("Wrong file time", sdf.format(lastModified), sdf.format(file.getLastModified()));
			assertEquals("Wrong MD5 link", "http://localhost:" + port + "/test-1.0.0.jar.md5", file.getMd5Link());
			assertEquals("Wrong SHA-1 link", "http://localhost:" + port + "/test-1.0.0.jar.sha1", file.getSha1Link());
			assertEquals("Wrong file type", "jar", file.getFileType());		
		}		
	}
	
	private class MockHttpServer extends TestHttpServer
	{
		public MockHttpServer() throws IOException
		{
			super();
		}
		
		@Override
		protected void process(Socket connection, String verb, String uri, Map<String, String> headers)
		throws IOException
		{
			try
			{
				URL url = new URL(baseUrl, uri);
				String file = url.getFile();
				
				if ("head".equals(verb))
				{
					if ("/test-1.0.0.jar".equals(file))
					{
						Map<String, String> responseHeaders = getDefaultHeaders();
						responseHeaders.put("Last-Modified", formatDateHeader(lastModified));
						responseHeaders.put("Content-Length", "12345678");
						sendResponse(connection, "200 OK", responseHeaders, null);
						return;
					}
					else if ("/test-1.0.0-javadoc.jar".equals(file))
					{
						Map<String, String> responseHeaders = getDefaultHeaders();
						responseHeaders.put("Last-Modified", formatDateHeader(lastModified));
						responseHeaders.put("Content-Length", "12345678");
						sendResponse(connection, "200 OK", responseHeaders, null);
						return;
					}
					else  if ("/test-1.0.0.jar.md5".equals(file))
					{
						Map<String, String> responseHeaders = getDefaultHeaders();
						responseHeaders.put("Last-Modified", formatDateHeader(lastModified));
						responseHeaders.put("Content-Length", "40");
						sendResponse(connection, "200 OK", responseHeaders, null);
						return;						
					}
					else if ("/test-1.0.0.jar.sha1".equals(file))
					{
						Map<String, String> responseHeaders = getDefaultHeaders();
						responseHeaders.put("Last-Modified", formatDateHeader(lastModified));
						responseHeaders.put("Content-Length", "40");
						sendResponse(connection, "200 OK", responseHeaders, null);
						return;
					} else if ("/test-1.0.1.jar".equals(file))
					{
						Map<String, String> responseHeaders = getDefaultHeaders();
						responseHeaders.put("Last-Modified", formatDateHeader(lastModified));
						responseHeaders.put("Content-Length", "23456789");
						sendResponse(connection, "200 OK", responseHeaders, null);
						return;
					}
				}
				sendError(connection, "404 Not Found");
			}
			catch (MalformedURLException e)
			{
				sendError(connection, "400 Bad Request");
			}			
		}		
	}
}