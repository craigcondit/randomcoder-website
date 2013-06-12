package org.randomcoder.download;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

@SuppressWarnings("javadoc")
public class AggregatePackageListProducerTest
{
	private AggregatePackageListProducer producer;
	
	@Before
	public void setUp()
	{
		producer = new AggregatePackageListProducer();
		List<PackageListProducer> producers = new ArrayList<PackageListProducer>();
		producers.add(new MockPackageListProducer("test1", "1.0.1", "test1-1.0.1.jar"));
		producers.add(new MockPackageListProducer("test1", "1.0.0", "test1-1.0.0.jar"));
		producers.add(new MockPackageListProducer("test2", "1.0.0", "test2-1.0.0.jar"));
		producer.setProducers(producers);
	}

	@After
	public void tearDown()
	{
		producer = null;
	}

	@Test
	public void testGetPackages() throws Exception
	{
		List<Package> packages = producer.getPackages();
		assertNotNull("Null package list", packages);
		assertEquals("Wrong package count", 2, packages.size());
		{
			Package pkg = packages.get(0);
			assertNotNull("Null package", pkg);
			assertEquals("Wrong name", "test1", pkg.getName());
			List<FileSet> filesets = pkg.getFileSets();
			assertNotNull("Null filesets", filesets);
			assertEquals("Wrong fileset size", 2, filesets.size());
			{
				FileSet fs = filesets.get(0);
				assertNotNull("Null fileset", fs);
				assertEquals("Wrong version", "1.0.1", fs.getVersion());
				List<FileSpec> files = fs.getFiles();
				assertNotNull("Null files", files);
				FileSpec file = files.get(0);
				assertNotNull("Null file", file);
				assertEquals("Wrong filename", "test1-1.0.1.jar", file.getFileName());
			}
			{
				FileSet fs = filesets.get(1);
				assertNotNull("Null fileset", fs);
				assertEquals("Wrong version", "1.0.0", fs.getVersion());
				List<FileSpec> files = fs.getFiles();
				assertNotNull("Null files", files);
				FileSpec file = files.get(0);
				assertNotNull("Null file", file);
				assertEquals("Wrong filename", "test1-1.0.0.jar", file.getFileName());
			}
		}
		{
			Package pkg = packages.get(1);
			assertNotNull("Null package", pkg);
			assertEquals("Wrong name", "test2", pkg.getName());
			List<FileSet> filesets = pkg.getFileSets();
			assertNotNull("Null filesets", filesets);
			assertEquals("Wrong fileset size", 1, filesets.size());
			{
				FileSet fs = filesets.get(0);
				assertNotNull("Null fileset", fs);
				assertEquals("Wrong version", "1.0.0", fs.getVersion());
				List<FileSpec> files = fs.getFiles();
				assertNotNull("Null files", files);
				FileSpec file = files.get(0);
				assertNotNull("Null file", file);
				assertEquals("Wrong filename", "test2-1.0.0.jar", file.getFileName());
			}
		}
	}
	
	private class MockPackageListProducer implements PackageListProducer
	{
		private String pkgName;
		private String version;
		private String filename;
		
		public MockPackageListProducer(String pkgName, String version, String filename)
		{
			this.pkgName = pkgName;
			this.version = version;
			this.filename = filename;
		}
		
		@Override
		public List<Package> getPackages()
		{
			List<Package> packages = new ArrayList<Package>();
			
			Package pkg = new Package();
			pkg.setName(pkgName);
			pkg.setDescription(pkgName + "-description");
			
			FileSet fs = new FileSet();
			fs.setVersion(version);
			
			FileSpec spec = new FileSpec();
			spec.setDownloadLink("http://localhost/test/" + filename);
			spec.setFileName(filename);
			spec.setFileSize(12345678);
			spec.setFileType("jar");
			spec.setLastModified(new Date());
			spec.setMd5Link(spec.getDownloadLink() + ".md5");
			spec.setSha1Link(spec.getDownloadLink() + ".sha1");
			fs.getFiles().add(spec);
			
			pkg.getFileSets().add(fs);			
			packages.add(pkg);
			return packages;
		}
	}	
}