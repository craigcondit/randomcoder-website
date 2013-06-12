package org.randomcoder.config;

import java.io.File;
import java.net.URL;
import java.util.*;

import javax.inject.*;

import net.sf.ehcache.CacheManager;

import org.randomcoder.download.*;
import org.randomcoder.download.cache.CachingPackageListProducer;
import org.randomcoder.download.maven.*;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.timer.*;

@Configuration
@SuppressWarnings("javadoc")
public class DownloadContext
{
	@Inject
	Environment env;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
	{
		PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
		pspc.setIgnoreUnresolvablePlaceholders(false);
		return pspc;
	}
	
	@Bean
	public LocalMavenRepository mavenRepository() throws Exception
	{
		List<MavenProject> projects = new ArrayList<MavenProject>();
		projects.add(project("randomcoder-website", "Randomcoder.org web site", "org/randomcoder/randomcoder-website"));
		projects.add(project("randomcoder-website-old", "Randomcoder.com web site (old version)", "com/randomcoder/randomcoder-website"));
		projects.add(project("randomcoder-taglibs", "JSP tag libraries for common website functionality", "org/randomcoder/randomcoder-taglibs"));
		projects.add(project("randomcoder-taglibs-old", "JSP tag libraries for common website functionality (old version)", "com/randomcoder/randomcoder-taglibs"));
		projects.add(project("randomcoder-citadel", "Java security framework (deprecated)", "com/randomcoder/randomcoder-citadel"));
		
		LocalMavenRepository repo = new LocalMavenRepository();
		repo.setUrl(new URL("https://nexus.randomcoder.org/content/repositories/releases/"));
		repo.setDir(new File(env.getRequiredProperty("maven.repository.dir")));
		repo.setProjects(projects);
		
		return repo;
	}
	
	private MavenProject project(String name, String desc, String dir)
	{
		Map<String, String> extMap = new HashMap<String, String>();
		extMap.put(".jar", "jar");
		extMap.put("-sources.jar", "src");
		extMap.put("-src.tar.bz2", "src");
		extMap.put("-src.tar.gz", "src");
		extMap.put("-src.zip", "src");
		extMap.put("-javadoc.jar", "javadoc");
		extMap.put("-tlddoc.jar", "tlddoc");
		
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
	public PackageListProducer cachingMavenRepository(
			@Named("mavenRepository") final LocalMavenRepository mavenRepository) 
	{
		CachingPackageListProducer prod = new CachingPackageListProducer();
		prod.setTarget(mavenRepository);
		prod.setCache(CacheManager.getInstance().getCache("org.randomcoder.MAVEN_REPOSITORY_CACHE"));
		prod.setCacheKey("mavenRepository");
		return prod;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public TimerFactoryBean repositoryRefreshTimer(
			@Named("mavenRepositoryRefreshTask") final ScheduledTimerTask mavenRepositoryRefreshTask)
	{
		TimerFactoryBean fb = new TimerFactoryBean();
		fb.setScheduledTimerTasks(new ScheduledTimerTask[] { mavenRepositoryRefreshTask });
		return fb;
	}
	
	@Bean
	@SuppressWarnings("deprecation")
	public ScheduledTimerTask mavenRepositoryRefreshTask(final TimerTask mavenRepositoryTimerTask)
	{
		ScheduledTimerTask task = new ScheduledTimerTask();
		task.setDelay(30000L); // 30 seconds
		task.setPeriod(1800000L); // 30 minutes
		task.setFixedRate(false);
		task.setTimerTask(mavenRepositoryTimerTask);
		return task;
	}
	
	@Bean
	@SuppressWarnings("deprecation")
	public MethodInvokingTimerTaskFactoryBean mavenRepositoryTimerTask(
			@Named("cachingMavenRepository") final PackageListProducer cachingMavenRepository)
	{
		MethodInvokingTimerTaskFactoryBean fb = new MethodInvokingTimerTaskFactoryBean();
		fb.setTargetObject(cachingMavenRepository);
		fb.setTargetMethod("refresh");
		return fb;		
	}	
}