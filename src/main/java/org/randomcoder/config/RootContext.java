package org.randomcoder.config;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

import javax.inject.*;
import javax.sql.DataSource;

import net.sf.ehcache.CacheManager;

import org.hibernate.SessionFactory;
import org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
import org.randomcoder.article.moderation.*;
import org.randomcoder.bo.AppInfoBusiness;
import org.randomcoder.content.*;
import org.randomcoder.dao.finder.*;
import org.randomcoder.dao.hibernate.HibernateDao;
import org.randomcoder.db.*;
import org.randomcoder.db.Role;
import org.randomcoder.download.*;
import org.randomcoder.download.cache.CachingPackageListProducer;
import org.randomcoder.download.maven.*;
import org.randomcoder.feed.*;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.timer.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@SuppressWarnings("javadoc")
@EnableTransactionManagement(proxyTargetClass = true)
@EnableScheduling
@ComponentScan({ "org.randomcoder.bo", "org.randomcoder.security.spring" })
@ImportResource({ "classpath:spring-security.xml" })
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
	public DataSource dataSource()
	{
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setUrl(env.getRequiredProperty("database.url"));
		ds.setUsername(env.getRequiredProperty("database.username"));
		ds.setPassword(env.getRequiredProperty("database.password"));
		return ds;
	}

	@Bean
	public MessageSource messageSource()
	{
		ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
		ms.setBasename("ApplicationResources");
		return ms;
	}

	@Bean
	public SessionFactory sessionFactory()
	{
		LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(dataSource());

		// builder.setProperty("hibernate.current_session_context_class",
		// "thread");
		builder.setProperty("hibernate.transaction.factory_class", JdbcTransactionFactory.class.getName());
		builder.setProperty("hibernate.dialect", PostgreSQL82Dialect.class.getName());
		builder.setProperty("hibernate.show_sql", "false");
		builder.setProperty("hibernate.max_fetch_depth", "2");
		builder.setProperty("hibernate.jdbc.fetch_size", "100");
		builder.setProperty("hibernate.jdbc.batch_size", "10");
		builder.setProperty("hibernate.cache.use_query_cache", "true");
		builder.setProperty("hibernate.cache.region.factory_class", SingletonEhCacheRegionFactory.class.getName());
		builder.scanPackages("org.randomcoder.db");

		builder.setCacheConcurrencyStrategy(Article.class.getName(), "read-write");
		builder.setCacheConcurrencyStrategy(Comment.class.getName(), "read-write");
		builder.setCacheConcurrencyStrategy(User.class.getName(), "read-write");
		builder.setCacheConcurrencyStrategy(Role.class.getName(), "read-only");
		builder.setCacheConcurrencyStrategy(Tag.class.getName(), "read-write");

		builder.setCollectionCacheConcurrencyStrategy(User.class.getName() + ".roles", "read-write");
		builder.setCollectionCacheConcurrencyStrategy(Article.class.getName() + ".tags", "read-write");
		builder.setCollectionCacheConcurrencyStrategy(Article.class.getName() + ".comments", "read-write");

		return builder.buildSessionFactory();
	}

	@Bean
	public PlatformTransactionManager transactionManager(final SessionFactory sessionFactory)
	{
		HibernateTransactionManager tx = new HibernateTransactionManager();
		tx.setSessionFactory(sessionFactory);
		return tx;
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

	// TODO convert database objects to Spring data

	@Bean
	public FinderIntroductionInterceptor finderIntroductionInterceptor()
	{
		return new FinderIntroductionInterceptor();
	}

	@Bean
	public UserDao userDao(final SessionFactory sessionFactory) throws Exception
	{
		return createDaoProxy(sessionFactory, UserDao.class, User.class);
	}

	@Bean
	public RoleDao roleDao(final SessionFactory sessionFactory) throws Exception
	{
		return createDaoProxy(sessionFactory, RoleDao.class, Role.class);
	}

	@Bean
	public ArticleDao articleDao(final SessionFactory sessionFactory) throws Exception
	{
		return createDaoProxy(sessionFactory, ArticleDao.class, Article.class);
	}

	@Bean
	public CommentDao commentDao(final SessionFactory sessionFactory) throws Exception
	{
		return createDaoProxy(sessionFactory, CommentDao.class, Comment.class);
	}

	@Bean
	public CommentReferrerDao commentReferrerDao(final SessionFactory sessionFactory) throws Exception
	{
		return createDaoProxy(sessionFactory, CommentReferrerDao.class, CommentReferrer.class);
	}

	@Bean
	public CommentIpDao commentIpDao(final SessionFactory sessionFactory) throws Exception
	{
		return createDaoProxy(sessionFactory, CommentIpDao.class, CommentIp.class);
	}

	@Bean
	public CommentUserAgentDao commentUserAgentDao(final SessionFactory sessionFactory) throws Exception
	{
		return createDaoProxy(sessionFactory, CommentUserAgentDao.class, CommentUserAgent.class);
	}

	@Bean
	public TagDao tagDao(final SessionFactory sessionFactory) throws Exception
	{
		TagDaoImpl dao = new TagDaoImpl();
		dao.setSessionFactory(sessionFactory);

		return createDaoProxy(TagDao.class, dao);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> T createDaoProxy(final SessionFactory sessionFactory, Class<T> iface, Class<?> entity) throws Exception
	{
		HibernateDao dao = new HibernateDao(entity);
		dao.setSessionFactory(sessionFactory);

		return createDaoProxy(iface, dao);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> T createDaoProxy(Class<T> iface, FinderExecutor target) throws Exception
	{
		ProxyFactory pf = new ProxyFactory(new Class[] { iface });
		pf.addAdvisor(new DefaultIntroductionAdvisor(finderIntroductionInterceptor()));
		pf.setTarget(target);
		return (T) pf.getProxy();
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

	// download

	@Bean
	public LocalMavenRepository mavenRepository() throws Exception
	{
		List<MavenProject> projects = new ArrayList<MavenProject>();
		projects.add(project("randomcoder-website", "Randomcoder.org web site", "org/randomcoder/randomcoder-website"));
		projects.add(project("randomcoder-website-old", "Randomcoder.com web site (old version)", "com/randomcoder/randomcoder-website"));
		projects.add(project("randomcoder-taglibs", "JSP tag libraries for common website functionality", "org/randomcoder/randomcoder-taglibs"));
		projects.add(project("randomcoder-taglibs-old", "JSP tag libraries for common website functionality (old version)",
				"com/randomcoder/randomcoder-taglibs"));
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