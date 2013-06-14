package org.randomcoder.config;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
import org.randomcoder.dao.finder.*;
import org.randomcoder.dao.hibernate.HibernateDao;
import org.randomcoder.db.*;
import org.randomcoder.db.Role;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.*;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@SuppressWarnings("javadoc")
public class LegacyDatabaseConfig
{
	@Inject
	Environment env;

	@Inject
	DataSource dataSource;

	@Bean
	public SessionFactory sessionFactory()
	{
		LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(dataSource);

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
	public PlatformTransactionManager hibernateTransactionManager(final SessionFactory sessionFactory)
	{
		HibernateTransactionManager tx = new HibernateTransactionManager();
		tx.setSessionFactory(sessionFactory);
		return tx;
	}

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
}