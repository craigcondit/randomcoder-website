package org.randomcoder.config;

import java.util.*;

import javax.inject.*;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
import org.randomcoder.article.Article;
import org.randomcoder.article.comment.*;
import org.randomcoder.article.moderation.*;
import org.randomcoder.bo.*;
import org.randomcoder.content.*;
import org.randomcoder.dao.finder.*;
import org.randomcoder.dao.hibernate.HibernateDao;
import org.randomcoder.db.*;
import org.randomcoder.feed.*;
import org.randomcoder.tag.Tag;
import org.randomcoder.user.*;
import org.randomcoder.user.Role;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.*;
import org.springframework.scheduling.timer.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@SuppressWarnings("javadoc")
@EnableTransactionManagement(proxyTargetClass = true)
@ComponentScan("org.randomcoder.bo")
@Import({ AcegiContext.class, DownloadContext.class })
public class RootContext
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

		//builder.setProperty("hibernate.current_session_context_class", "thread");
		builder.setProperty("hibernate.transaction.factory_class", JdbcTransactionFactory.class.getName());
		builder.setProperty("hibernate.dialect", PostgreSQL82Dialect.class.getName());
		builder.setProperty("hibernate.show_sql", "false");
		builder.setProperty("hibernate.max_fetch_depth", "2");
		builder.setProperty("hibernate.jdbc.fetch_size", "100");
		builder.setProperty("hibernate.jdbc.batch_size", "10");
		builder.setProperty("hibernate.cache.use_query_cache", "true");
		builder.setProperty("hibernate.cache.region.factory_class", SingletonEhCacheRegionFactory.class.getName());
		
		builder.addAnnotatedClasses(
				Article.class, Comment.class, CommentReferrer.class,
				CommentIp.class, CommentUserAgent.class, User.class,
				Role.class, Tag.class);

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

	@SuppressWarnings("unchecked")
	private <T> T createDaoProxy(final SessionFactory sessionFactory, Class<T> iface, Class<?> entity) throws Exception
	{
		HibernateDao dao = new HibernateDao(entity);
		dao.setSessionFactory(sessionFactory);

		return createDaoProxy(iface, dao);
	}

	@SuppressWarnings("unchecked")
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

	// TODO convert this to newer spring syntax
	@Bean
	@SuppressWarnings("deprecation")
	public TimerFactoryBean moderationTimer(
			@Named("moderationUpdateTask") final ScheduledTimerTask moderationUpdateTask)
	{
		TimerFactoryBean tfb = new TimerFactoryBean();
		tfb.setScheduledTimerTasks(new ScheduledTimerTask[] { moderationUpdateTask });
		return tfb;
	}

	// TODO convert this to newer spring syntax
	@Bean
	@SuppressWarnings("deprecation")
	public ScheduledTimerTask moderationUpdateTask(final ArticleBusiness articleBusiness)
	{
		ScheduledTimerTask tt = new ScheduledTimerTask();
		tt.setDelay(30000L); // 30 seconds
		tt.setPeriod(60000L); // 60 seconds
		tt.setFixedRate(false);

		ModeratorTimerTask task = new ModeratorTimerTask();
		task.setArticleBusiness(articleBusiness);
		task.setBatchSize(5);

		tt.setTimerTask(task);

		return tt;
	}
}