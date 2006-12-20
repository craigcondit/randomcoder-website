package com.randomcoder.test;

import java.io.*;
import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.dbunit.database.*;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.SessionFactory;
import org.junit.*;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate3.*;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.randomcoder.article.*;
import com.randomcoder.dao.finder.FinderIntroductionInterceptor;
import com.randomcoder.dao.hibernate.HibernateDao;
import com.randomcoder.tag.Tag;
import com.randomcoder.user.*;

abstract public class AbstractDaoTestCase
{
	private static DataSource adminDataSource;
	private static DataSource userDataSource;
	private SessionFactory sessionFactory;
	
	private static final String DATA_XML = "/test-data.xml";
	private static final String DATA_DTD = "/test-data.dtd";
	private static final String TEST_PROPS = "/test.properties";
	private static final String LOCAL_PROPS = "/local.test.properties";
	
	private static final Properties testProps;
	private static final Properties localProps;
	
	static
	{
		InputStream testStream = null;
		InputStream localStream = null;
		
		try
		{
			testProps = new Properties();
			testStream = AbstractDaoTestCase.class.getResourceAsStream(TEST_PROPS);
			if (testStream != null) testProps.load(testStream);
		 
			localProps = new Properties();
			localStream = AbstractDaoTestCase.class.getResourceAsStream(LOCAL_PROPS);
			if (localStream != null) localProps.load(localStream);
		}
		catch (IOException e)
		{
			throw new ExceptionInInitializerError(e);
		}
		finally
		{
			if (testStream != null) try { testStream.close(); } catch (Exception ignored) {}
			if (localStream != null) try { localStream.close(); } catch (Exception ignored) {}
		}
	}
	
	private static String getProperty(String key)
	{
		String value = System.getProperty(key);
		if (value != null && value.length() != 0) return value;
		
		value = localProps.getProperty(key);
		if (value != null && value.length() != 0) return value;
		
		return testProps.getProperty(key);
	}
	
	protected final void cleanDatabase() throws Exception
	{		
		Connection con = null;
		Reader xml = null;
		Reader dtd = null;
		
		try
		{
			con = adminDataSource.getConnection();
			
			xml = new InputStreamReader(getClass().getResourceAsStream(DATA_XML));
			dtd = new InputStreamReader(getClass().getResourceAsStream(DATA_DTD));
			
			IDatabaseConnection dbConnection = new DatabaseConnection(con);
			IDataSet dataSet = new FlatXmlDataSet(xml, dtd);
			DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
		}
		finally
		{
			con.close();
			xml.close();
			dtd.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected final Object createDao(Class entityClass, Class daoClass) throws Exception
	{
		HibernateDao daoTarget = new HibernateDao(entityClass);
		
		daoTarget.setSessionFactory(getSessionFactory());

		FinderIntroductionInterceptor interceptor = new FinderIntroductionInterceptor();
		
		DefaultIntroductionAdvisor advisor = new DefaultIntroductionAdvisor(interceptor);
		
		ProxyFactory proxyFactory = new ProxyFactory(new Class[] { daoClass });
		proxyFactory.setTarget(daoTarget);
		proxyFactory.addAdvisor(advisor);
		
		return proxyFactory.getProxy();		
	}

	protected final SessionFactory getSessionFactory() throws Exception
	{
		if (sessionFactory == null) sessionFactory = createSessionFactory();
		return sessionFactory;
	}
	
	private final SessionFactory createSessionFactory() throws Exception
	{
		Properties hibProps = new Properties();		
		hibProps.setProperty("hibernate.current_session_context_class", "thread");
		hibProps.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
		hibProps.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		hibProps.setProperty("hibernate.show_sql", "false");
		hibProps.setProperty("hibernate.max_fetch_depth", "2");
		hibProps.setProperty("hibernate.jdbc.fetch_size", "100");
		hibProps.setProperty("hibernate.jdbc.batch_size", "10");
		hibProps.setProperty("hibernate.cache.use_query_cache", "true");
		hibProps.setProperty("hibernate.cache.provider_class", "net.sf.ehcache.hibernate.SingletonEhCacheProvider");
		
		Properties ecProps = new Properties();
		ecProps.setProperty("com.randomcoder.article.Article", "read-write");
		ecProps.setProperty("com.randomcoder.article.Comment", "read-write");
		ecProps.setProperty("com.randomcoder.user.User", "read-write");
		ecProps.setProperty("com.randomcoder.user.Role", "read-only");
		ecProps.setProperty("com.randomcoder.user.CardSpaceToken", "read-write");
		ecProps.setProperty("com.randomcoder.tag.Tag", "read-write");
		
		Properties ccProps = new Properties();
		ccProps.setProperty("com.randomcoder.user.User.roles", "read-write");
		ccProps.setProperty("com.randomcoder.article.Article.tags", "read-write");
		ccProps.setProperty("com.randomcoder.article.Article.comments", "read-write");
		
		AnnotationSessionFactoryBean factory = new AnnotationSessionFactoryBean();
		factory.setDataSource(userDataSource);
		factory.setHibernateProperties(hibProps);		
		factory.setAnnotatedClasses(new Class[] { Article.class, Comment.class, User.class, Role.class, CardSpaceToken.class, Tag.class});
		factory.setEntityCacheStrategies(ecProps);
		factory.setCollectionCacheStrategies(ccProps);
		
		factory.afterPropertiesSet();
		
		HibernateTransactionManager txManager = new HibernateTransactionManager();

		SessionFactory sf = (SessionFactory) factory.getObject();
		
		txManager.setSessionFactory(sf);
		
		return sf;
	}
	
	@BeforeClass
	public static final void initDataSources() throws Exception
	{
		String driver = getProperty("test.database.driver");
		String type = getProperty("test.database.type");
		String host = getProperty("test.database.host");
		String name = getProperty("test.database.name");

		String adminUsername = getProperty("test.database.admin.username");
		String adminPassword = getProperty("test.database.admin.password");
		
		String userUsername = getProperty("test.database.user.username");
		String userPassword = getProperty("test.database.user.password");

		String url = "jdbc:" + type + "://" + host + "/" + name;
		
		adminDataSource = new DriverManagerDataSource(driver, url, adminUsername, adminPassword);
		userDataSource = new DriverManagerDataSource(driver, url, userUsername, userPassword);
	}
	
	@AfterClass
	public static final void destroyDataSources() throws Exception
	{
		adminDataSource = null;
		userDataSource = null;
	}	
	
	protected final void begin() throws Exception
	{
		getSessionFactory().getCurrentSession().getTransaction().begin();
	}
	
	protected final void commit() throws Exception
	{
		getSessionFactory().getCurrentSession().getTransaction().commit();
	}
	
	protected final void rollback() throws Exception
	{
		getSessionFactory().getCurrentSession().getTransaction().rollback();
	}

	protected final void clear() throws Exception
	{
		getSessionFactory().getCurrentSession().clear();
	}

	protected final void flush() throws Exception
	{
		getSessionFactory().getCurrentSession().flush();
	}
	
	protected final void unbindSession() throws Exception
	{
		TransactionSynchronizationManager.unbindResource(getSessionFactory());
	}
	
	protected final void bindSession() throws Exception
	{
		SessionFactory sf = getSessionFactory();
		TransactionSynchronizationManager.bindResource(sf, new SessionHolder(sf.openSession()));
	}
	
	protected final void rebindSession() throws Exception
	{
		unbindSession();
		bindSession();
	}
	
}
