package org.randomcoder.config;

import java.util.*;
import java.util.concurrent.*;

import javax.inject.Inject;

import org.randomcoder.article.moderation.*;
import org.randomcoder.bo.AppInfoBusiness;
import org.randomcoder.content.*;
import org.randomcoder.feed.*;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.*;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.*;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@SuppressWarnings("javadoc")
@EnableTransactionManagement(proxyTargetClass = true)
@EnableScheduling
@ComponentScan({ "org.randomcoder.bo", "org.randomcoder.security.spring" })
@ImportResource({ "classpath:spring-security.xml" })
@Import({ DownloadConfig.class, DatabaseConfig.class })
public class RootContext implements SchedulingConfigurer
{
	@Inject
	Environment env;

	@Override
	public void configureTasks(ScheduledTaskRegistrar registrar)
	{
		registrar.setScheduler(taskScheduler());
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
	{
		PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
		pspc.setIgnoreUnresolvablePlaceholders(false);
		return pspc;
	}

	@Bean(destroyMethod = "shutdown")
	public Executor taskScheduler()
	{
		return Executors.newScheduledThreadPool(2);
	}

	@Bean
	public MessageSource messageSource()
	{
		ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
		ms.setBasename("ApplicationResources");
		return ms;
	}

	@Bean
	public MultiContentFilter contentFilter() throws Exception
	{
		MultiContentFilter mcf = new MultiContentFilter();

		Map<String, ContentFilter> filters = new HashMap<String, ContentFilter>();
		filters.put("text/plain", textFilter());
		filters.put("application/xhtml+xml", xhtmlFilter());
		mcf.setFilters(filters);

		mcf.setDefaultHandler(textFilter());
		return mcf;
	}

	@Bean
	public ContentFilter textFilter() throws Exception
	{
		return new TextFilter();
	}

	@Bean
	public ContentFilter xhtmlFilter() throws Exception
	{
		XHTMLFilter filter = new XHTMLFilter();

		Set<String> ac = new HashSet<String>();
		ac.add("lang-xml");
		ac.add("lang-js");
		ac.add("lang-css");
		ac.add("external");
		filter.setAllowedClasses(ac);

		return filter;
	}

	@Bean
	public FeedGenerator atomFeedGenerator(final AppInfoBusiness appInfo) throws Exception
	{
		AtomFeedGenerator gen = new AtomFeedGenerator();
		gen.setAppInfoBusiness(appInfo);
		gen.setBaseUrl("https://randomcoder.org/");
		gen.setUriPrefix("tag:randomcoder.org,2007:");
		gen.setContentFilter(contentFilter());
		return gen;
	}

	@Bean
	public FeedGenerator rss20FeedGenerator(final AppInfoBusiness appInfo) throws Exception
	{
		Rss20FeedGenerator gen = new Rss20FeedGenerator();
		gen.setAppInfoBusiness(appInfo);
		gen.setBaseUrl("https://randomcoder.org/");
		gen.setContentFilter(contentFilter());
		return gen;
	}

	@Bean
	public Moderator moderator(final AppInfoBusiness applicationInformation)
	{
		AkismetModerator mod = new AkismetModerator();
		mod.setAppInfoBusiness(applicationInformation);
		mod.setApiKey(env.getRequiredProperty("akismet.site.key"));
		mod.setSiteUrl(env.getRequiredProperty("akismet.site.url"));
		return mod;
	}
}