package org.randomcoder.mvc.controller;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import org.easymock.*;
import org.junit.*;
import org.randomcoder.download.*;
import org.randomcoder.download.Package;
import org.springframework.ui.Model;

@SuppressWarnings("javadoc")
public class DownloadControllerTest
{
	private static final String[] PACKAGE_NAMES = new String[] { "test1", "test2", "test3" };
	private static final String[] VERSIONS = new String[] { "1.0.2", "1.0.1", "1.0.0" };
	private static final String[] EXTENSIONS = new String[] { ".jar", "-javadoc.jar" };
	private static final String[] TYPES = new String[] { "jar", "javadoc" };
	
	private DownloadController controller;
	private IMocksControl control;
	private PackageListProducer p;
	private Model m;
	
	@Before
	public void setUp()
	{
		control = createControl();
		p = control.createMock(PackageListProducer.class);
		m = control.createMock(Model.class);
		controller = new DownloadController();
		controller.setMaximumVersionCount(1);
		controller.setPackageListProducer(p);
	}

	@After
	public void tearDown()
	{
		controller = null;
		m = null;
		p = null;
		control = null;
	}

	@Test
	public void testHandleDefault() throws Exception
	{
		Capture<List<Package>> pcap = new Capture<List<Package>>();
		
		List<Package> pkgs = createPackageList();
		expect(p.getPackages()).andReturn(pkgs);
		expect(m.addAttribute(eq("packages"), capture(pcap))).andReturn(m);
		expect(m.addAttribute("showAll", false)).andReturn(m);
		expect(m.addAttribute("maximumVersionCount", 1)).andReturn(m);
		control.replay();
		
		assertEquals("view", "download", controller.download(null, false, m));
		control.verify();
		
		assertEquals(pkgs.size(), pcap.getValue().size());
		for (int i = 0; i < pkgs.size(); i++)
		{
			assertSame(pkgs.get(i), pcap.getValue().get(i));
		}
	}

	@Test
	public void testHandlePackage() throws Exception
	{
		Capture<List<Package>> pcap = new Capture<List<Package>>();
		
		List<Package> pkgs = createPackageList();
		expect(p.getPackages()).andReturn(pkgs);
		expect(m.addAttribute(eq("packages"), capture(pcap))).andReturn(m);
		expect(m.addAttribute("packageName", "test2")).andReturn(m);
		expect(m.addAttribute("showAll", true)).andReturn(m);
		expect(m.addAttribute("maximumVersionCount", Integer.MAX_VALUE)).andReturn(m);
		control.replay();
		
		assertEquals("view", "download", controller.download("test2", true, m));
		control.verify();
		
		assertEquals("package count", 1, pcap.getValue().size());
		assertEquals("package name", "test2", pcap.getValue().get(0).getName());
	}

	private List<Package> createPackageList()
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