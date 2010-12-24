package com.randomcoder.download;

import java.util.*;

import junit.framework.TestCase;

import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class DownloadControllerTest extends TestCase
{
	private DownloadController controller;
	private MockPackageListProducer producer;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private BindException errors; 
	
	@Override
	protected void setUp() throws Exception
	{
		producer = new MockPackageListProducer();
		controller = new DownloadController();
		controller.setViewName("test");
		controller.setMaximumVersionCount(1);
		controller.setPackageListProducer(producer);
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		errors = new BindException(new Object(), "test");		
	}

	@Override
	protected void tearDown() throws Exception
	{
		controller = null;
		producer = null;
		request = null;
		response = null;
		errors = null;
	}

	@SuppressWarnings("unchecked")
	public void testHandleDefault() throws Exception
	{
		DownloadCommand command = new DownloadCommand();
		command.setPackageName(null);
		command.setShowAll(false);
		ModelAndView mav = controller.handle(request, response, command, errors);
		assertNotNull("Null MAV", mav);
		assertEquals("Wrong view name", "test", mav.getViewName());
		Object packagesObject = mav.getModel().get("packages");
		assertNotNull("Null packages", packagesObject);
		List<Package> packages = (List) packagesObject;
		assertEquals("Wrong package count", 3, packages.size());
		Package pkg = packages.get(0);
		assertNotNull("Null package", pkg);
		assertEquals("Wrong pkg name", "test1", pkg.getName());
		assertNull("packageName specified", mav.getModel().get("packageName"));
		assertEquals("showAll is true", Boolean.FALSE, mav.getModel().get("showAll"));
		assertEquals("maximumVersionCount is wrong", new Integer(1), mav.getModel().get("maximumVersionCount"));
	}

	@SuppressWarnings("unchecked")
	public void testHandlePackage() throws Exception
	{
		DownloadCommand command = new DownloadCommand();
		command.setPackageName("test2");
		command.setShowAll(true);
		ModelAndView mav = controller.handle(request, response, command, errors);
		assertNotNull("Null MAV", mav);
		assertEquals("Wrong view name", "test", mav.getViewName());
		Object packagesObject = mav.getModel().get("packages");
		assertNotNull("Null packages", packagesObject);
		List<Package> packages = (List) packagesObject;
		assertEquals("Wrong package count", 1, packages.size());
		Package pkg = packages.get(0);
		assertNotNull("Null package", pkg);
		assertEquals("Wrong pkg name", "test2", pkg.getName());
		assertEquals("Wrong packageName specified", "test2",  mav.getModel().get("packageName"));
		assertEquals("showAll is false", Boolean.TRUE, mav.getModel().get("showAll"));
		assertEquals("maximumVersionCount is wrong", new Integer(Integer.MAX_VALUE), mav.getModel().get("maximumVersionCount"));
	}

	static class MockPackageListProducer implements PackageListProducer
	{
		private static final String[] PACKAGE_NAMES = new String[] { "test1", "test2", "test3" };
		private static final String[] VERSIONS = new String[] { "1.0.2", "1.0.1", "1.0.0" };
		private static final String[] EXTENSIONS = new String[] { ".jar", "-javadoc.jar" };
		private static final String[] TYPES = new String[] { "jar", "javadoc" };
		
		@Override
		public List<Package> getPackages()
		{
			List<Package> packages = new ArrayList<Package>();
			
			for (String name : PACKAGE_NAMES)
			{
				Package pkg = new Package();
				pkg.setName(name);
				pkg.setDescription(name + "-description");
				
				for (String version : VERSIONS)
				{
					FileSet fs = new FileSet();
					fs.setVersion(version);
					
					for (int i = 0; i < EXTENSIONS.length; i++)
					{
						FileSpec spec = new FileSpec();
						spec.setDownloadLink("http://localhost/" + name + "/" + name + "-" + version + EXTENSIONS[i]);
						spec.setFileName(name + "-" + version + EXTENSIONS[i]);
						spec.setFileSize(12345678);
						spec.setFileType(TYPES[i]);
						spec.setLastModified(new Date());
						spec.setMd5Link(spec.getDownloadLink() + ".md5");
						spec.setSha1Link(spec.getDownloadLink() + ".sha1");
						fs.getFiles().add(spec);					
					}
					pkg.getFileSets().add(fs);
				}
				packages.add(pkg);
			}
			return packages;
		}
	}	
}
