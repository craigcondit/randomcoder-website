package org.randomcoder.download.maven;

import static org.junit.Assert.*;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.junit.*;
import org.randomcoder.download.*;
import org.randomcoder.download.Package;
import org.randomcoder.test.TestHttpServer;

@SuppressWarnings("javadoc")
public class MavenRepositoryTest
{
	private MavenRepository repo;
	private MockHttpServer server;
	protected URL baseUrl;
	protected Date lastModified;
	private int port;
	
	private static final String METADATA =
		"<metadata>" + 
		"<groupId>test-group</groupId>" +
		"<artifactId>test-artifact</artifactId>" +
		"<version>1.0.0</version>" +
		"<versioning>" +
		"<release>1.0.2</release>" +
		"<versions>" +
		"<version>1.0.0</version>" +
		"<version>1.0.1</version>" +
		"<version>1.0.2</version>" +
		"</versions>" +
		"<lastUpdated>20070214225905</lastUpdated>" +
		"</versioning>" +
		"</metadata>";
	
	@Before
	public void setUp() throws Exception
	{
		lastModified = new Date();
		server = new MockHttpServer();
		port = server.getPort();
		
		repo = new MavenRepository();
		List<MavenProject> projects = new ArrayList<MavenProject>();
		
		baseUrl = new URL("http://localhost:" + port + "/");
		repo.setUrl(baseUrl);
	
		MavenProject project = new MavenProject();
		project.setProjectName("test");
		project.setProjectDescription("test-description");
		project.setDirectory("test");
	
		Map<String, String> extensionMappings = new HashMap<String, String>();
		extensionMappings.put(".jar", "jar");		
		extensionMappings.put("-javadoc.jar", "javadoc");		
		project.setExtensionMappings(extensionMappings);		
		projects.add(project);
		
		repo.setProjects(projects);
		repo.afterPropertiesSet();
	}

	@After
	public void tearDown() throws Exception
	{
		server.destroy();
		repo.destroy();
		
		lastModified = null;
		server = null;
		baseUrl = null;
		repo = null;
	}
	
	@Test
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
			assertEquals("Wrong download link", "http://localhost:" + port + "/test/1.0.1/test-artifact-1.0.1.jar", file.getDownloadLink());
			assertEquals("Wrong filename", "test-artifact-1.0.1.jar", file.getFileName());
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
			assertEquals("Wrong download link", "http://localhost:" + port + "/test/1.0.0/test-artifact-1.0.0.jar", file.getDownloadLink());
			assertEquals("Wrong filename", "test-artifact-1.0.0.jar", file.getFileName());
			assertEquals("Wrong file size", 12345678, file.getFileSize());
			assertEquals("Wrong file time", sdf.format(lastModified), sdf.format(file.getLastModified()));
			assertEquals("Wrong MD5 link", "http://localhost:" + port + "/test/1.0.0/test-artifact-1.0.0.jar.md5", file.getMd5Link());
			assertEquals("Wrong SHA-1 link", "http://localhost:" + port + "/test/1.0.0/test-artifact-1.0.0.jar.sha1", file.getSha1Link());
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
				
				if ("get".equals(verb))
				{
					if ("/test/maven-metadata.xml".equals(file))
					{
						Map<String, String> responseHeaders = getDefaultHeaders();
						sendResponse(connection, "200 OK", responseHeaders, new ByteArrayInputStream(METADATA.getBytes("UTF-8")));
						return;
					}					
				}
				else if ("head".equals(verb))
				{
					if ("/test/1.0.0/test-artifact-1.0.0.jar".equals(file))
					{
						Map<String, String> responseHeaders = getDefaultHeaders();
						responseHeaders.put("Last-Modified", formatDateHeader(lastModified));
						responseHeaders.put("Content-Length", "12345678");
						sendResponse(connection, "200 OK", responseHeaders, null);
						return;
					}
					else if ("/test/1.0.0/test-artifact-1.0.0-javadoc.jar".equals(file))
					{
						Map<String, String> responseHeaders = getDefaultHeaders();
						responseHeaders.put("Last-Modified", formatDateHeader(lastModified));
						responseHeaders.put("Content-Length", "12345678");
						sendResponse(connection, "200 OK", responseHeaders, null);
						return;
					}
					else  if ("/test/1.0.0/test-artifact-1.0.0.jar.md5".equals(file))
					{
						Map<String, String> responseHeaders = getDefaultHeaders();
						responseHeaders.put("Last-Modified", formatDateHeader(lastModified));
						responseHeaders.put("Content-Length", "40");
						sendResponse(connection, "200 OK", responseHeaders, null);
						return;						
					}
					else if ("/test/1.0.0/test-artifact-1.0.0.jar.sha1".equals(file))
					{
						Map<String, String> responseHeaders = getDefaultHeaders();
						responseHeaders.put("Last-Modified", formatDateHeader(lastModified));
						responseHeaders.put("Content-Length", "40");
						sendResponse(connection, "200 OK", responseHeaders, null);
						return;
					} else if ("/test/1.0.1/test-artifact-1.0.1.jar".equals(file))
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
