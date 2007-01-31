package com.randomcoder.download;

import java.util.*;

public class DummyPackageListProducer implements PackageListProducer
{
	private static final String BASE_URL = "http://randomcoder.com/maven/repository/release/com/randomcoder";
	private static final String WEB_URL = BASE_URL + "/randomcoder-website/";
	private static final String TAGLIBS_URL = BASE_URL + "/randomcoder-taglibs/";

	public List<Package> getPackages()
	{
		List<Package> packages = new ArrayList<Package>();
		
		{
			Package pkg = new Package();
			pkg.setName("randomcoder-website");
			pkg.setDescription("Randomcoder Website");			
			addWebsiteFileset(pkg, "2.0.2");
			addWebsiteFileset(pkg, "2.0.1");
			addWebsiteFileset(pkg, "2.0.0");
			packages.add(pkg);
		}

		{
			Package pkg = new Package();
			pkg.setName("randomcoder-taglibs");
			pkg.setDescription("Randomcoder Taglibs");
			packages.add(pkg);
			addTaglibsFileset(pkg, "1.5.2");
		}
		
		return packages;
	}

	private void addWebsiteFileset(Package pkg, String version)
	{
		FileSet fs = new FileSet();
		fs.setVersion(version);
		addWar(pkg, fs, WEB_URL);
		addJavaDoc(pkg, fs, WEB_URL);
		pkg.getFileSets().add(fs);
	}

	private void addTaglibsFileset(Package pkg, String version)
	{
		FileSet fs = new FileSet();
		fs.setVersion(version);
		addJar(pkg, fs, TAGLIBS_URL);
		addSrcTgz(pkg, fs, TAGLIBS_URL);
		addSrcTbz2(pkg, fs, TAGLIBS_URL);
		addSrcZip(pkg, fs, TAGLIBS_URL);
		addJavaDoc(pkg, fs, TAGLIBS_URL);
		addTldDoc(pkg, fs, TAGLIBS_URL);
		pkg.getFileSets().add(fs);
	}

	private void addSrcZip(Package pkg, FileSet fs, String baseUrl)
	{
		FileSpec spec = new FileSpec();
		spec.setFileName(pkg.getName() + "-" + fs.getVersion() + "-src.zip");
		spec.setDownloadLink(baseUrl + fs.getVersion() + "/" + spec.getFileName());
		spec.setFileSize(223128);
		spec.setFileType("src");
		spec.setLastModified(new Date());
		spec.setMd5Link(spec.getDownloadLink() + ".md5");
		spec.setSha1Link(spec.getDownloadLink() + ".sha1");
		fs.getFiles().add(spec);
	}

	private void addSrcTgz(Package pkg, FileSet fs, String baseUrl)
	{
		FileSpec spec = new FileSpec();
		spec.setFileName(pkg.getName() + "-" + fs.getVersion() + "-src.tar.gz");
		spec.setDownloadLink(baseUrl + fs.getVersion() + "/" + spec.getFileName());
		spec.setFileSize(103857);
		spec.setFileType("src");
		spec.setLastModified(new Date());
		spec.setMd5Link(spec.getDownloadLink() + ".md5");
		spec.setSha1Link(spec.getDownloadLink() + ".sha1");
		fs.getFiles().add(spec);
	}

	private void addSrcTbz2(Package pkg, FileSet fs, String baseUrl)
	{
		FileSpec spec = new FileSpec();
		spec.setFileName(pkg.getName() + "-" + fs.getVersion() + "-src.tar.bz2");
		spec.setDownloadLink(baseUrl + fs.getVersion() + "/" + spec.getFileName());
		spec.setFileSize(80392);
		spec.setFileType("src");
		spec.setLastModified(new Date());
		spec.setMd5Link(spec.getDownloadLink() + ".md5");
		spec.setSha1Link(spec.getDownloadLink() + ".sha1");
		fs.getFiles().add(spec);
	}
	
	private void addJavaDoc(Package pkg, FileSet fs, String baseUrl)
	{
		FileSpec spec = new FileSpec();
		spec.setFileName(pkg.getName() + "-" + fs.getVersion() + "-javadoc.jar");
		spec.setDownloadLink(baseUrl + fs.getVersion() + "/" + spec.getFileName());
		spec.setFileSize(1145839);
		spec.setFileType("javadoc");
		spec.setLastModified(new Date());
		spec.setMd5Link(spec.getDownloadLink() + ".md5");
		spec.setSha1Link(spec.getDownloadLink() + ".sha1");
		fs.getFiles().add(spec);
	}

	private void addTldDoc(Package pkg, FileSet fs, String baseUrl)
	{
		FileSpec spec = new FileSpec();
		spec.setFileName(pkg.getName() + "-" + fs.getVersion() + "-tlddoc.jar");
		spec.setDownloadLink(baseUrl + fs.getVersion() + "/" + spec.getFileName());
		spec.setFileSize(74152);
		spec.setFileType("tlddoc");
		spec.setLastModified(new Date());
		spec.setMd5Link(spec.getDownloadLink() + ".md5");
		spec.setSha1Link(spec.getDownloadLink() + ".sha1");
		fs.getFiles().add(spec);
	}
	
	private void addWar(Package pkg, FileSet fs, String baseUrl)
	{
		FileSpec spec = new FileSpec();
		spec.setFileName(pkg.getName() + "-" + fs.getVersion() + ".war");
		spec.setDownloadLink(baseUrl + fs.getVersion() + "/" + spec.getFileName());
		spec.setFileSize(12345678);
		spec.setFileType("war");
		spec.setLastModified(new Date());
		spec.setMd5Link(spec.getDownloadLink() + ".md5");
		spec.setSha1Link(spec.getDownloadLink() + ".sha1");
		fs.getFiles().add(spec);
	}

	private void addJar(Package pkg, FileSet fs, String baseUrl)
	{
		FileSpec spec = new FileSpec();
		spec.setFileName(pkg.getName() + "-" + fs.getVersion() + ".jar");
		spec.setDownloadLink(baseUrl + fs.getVersion() + "/" + spec.getFileName());
		spec.setFileSize(67838);
		spec.setFileType("jar");
		spec.setLastModified(new Date());
		spec.setMd5Link(spec.getDownloadLink() + ".md5");
		spec.setSha1Link(spec.getDownloadLink() + ".sha1");
		fs.getFiles().add(spec);
	}
}