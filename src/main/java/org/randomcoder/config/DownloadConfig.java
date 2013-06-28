package org.randomcoder.config;

import java.net.URL;
import java.util.*;

import javax.inject.*;

import net.sf.ehcache.CacheManager;

import org.randomcoder.download.*;
import org.randomcoder.download.cache.CachingPackageListProducer;
import org.randomcoder.download.maven.*;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

@Configuration
@SuppressWarnings("javadoc")
public class DownloadConfig
{
	@Inject
	Environment env;

	@Bean
	public PackageListProducer mavenRepository() throws Exception
	{
		List<MavenProject> projects = new ArrayList<MavenProject>();
		projects.add(project("randomcoder-website", "Randomcoder.org web site", "org/randomcoder/randomcoder-website", ".jar", "-javadoc.jar", "-sources.jar"));
		projects.add(project("randomcoder-taglibs", "JSP tag libraries for common website functionality", "org/randomcoder/randomcoder-taglibs", ".jar", "-javadoc.jar", "-sources.jar", "-tlddoc.jar"));

		RemoteMavenRepository repo = new RemoteMavenRepository();
		repo.setUrl(new URL("https://randomcoder.org/nexus/content/repositories/releases/"));
		repo.setProjects(projects);

		return repo;
	}

	private MavenProject project(String name, String desc, String dir, String... extensions)
	{
		Map<String, String> extMap = new HashMap<String, String>();
		extMap.put(".jar", "jar");
		extMap.put("-sources.jar", "src");
		extMap.put("-src.tar.bz2", "src");
		extMap.put("-src.tar.gz", "src");
		extMap.put("-src.zip", "src");
		extMap.put("-javadoc.jar", "javadoc");
		extMap.put("-tlddoc.jar", "tlddoc");

		extMap.keySet().retainAll(Arrays.asList(extensions));
		
		MavenProject project = new MavenProject();
		project.setProjectName(name);
		project.setProjectDescription(desc);
		project.setDirectory(dir);
		project.setExtensionMappings(extMap);

		return project;
	}

	@Bean
	public AggregatePackageListProducer packageListProducer(
			@Named("cachingMavenRepository") final PackageListProducer cachingMavenRepository)
	{
		AggregatePackageListProducer prod = new AggregatePackageListProducer();
		prod.setProducers(Collections.singletonList(cachingMavenRepository));
		return prod;
	}

	@Bean
	public CachingPackageListProducer cachingMavenRepository(
			@Named("mavenRepository") final PackageListProducer mavenRepository)
	{
		CachingPackageListProducer prod = new CachingPackageListProducer();
		prod.setTarget(mavenRepository);
		prod.setCache(CacheManager.getInstance().getCache("org.randomcoder.MAVEN_REPOSITORY_CACHE"));
		prod.setCacheKey("mavenRepository");
		return prod;
	}
}
