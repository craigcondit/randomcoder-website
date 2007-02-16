package com.randomcoder.test;

import java.io.*;
import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.dbunit.database.*;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.SessionFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate3.*;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.randomcoder.article.*;
import com.randomcoder.dao.finder.FinderIntroductionInterceptor;
import com.randomcoder.dao.hibernate.HibernateDao;
import com.randomcoder.security.cardspace.CardSpaceSeenToken;
import com.randomcoder.tag.Tag;
import com.randomcoder.user.*;

abstract public class AbstractDaoTestCase extends TestCase
{
	private static DataSource dataSource;
	private SessionFactory sessionFactory;
	
	private static final String DATA_XML = "/database-data.xml";
	private static final String DATA_DTD = "/database-schema.dtd";
	private static final String DB_PROPS = "/database.properties";
	
	private static final Properties dbProps;
	
	static
	{
		InputStream dbStream = null;
		
		try
		{
			dbProps = new Properties();
			dbStream = AbstractDaoTestCase.class.getResourceAsStream(DB_PROPS);
			if (dbStream != null) dbProps.load(dbStream);
		}
		catch (IOException e)
		{
			throw new ExceptionInInitializerError(e);
		}
		finally
		{
			if (dbStream != null) try { dbStream.close(); } catch (Exception ignored) {}
		}
	}
	
	private static String getProperty(String key)
	{
		String value = System.getProperty(key);
		if (value != null && value.length() != 0) return value;
		
		value = dbProps.getProperty(key);
		
		// hack to prevent ugly dir from being created 
		if("test.database.url".equals(key) && value != null && value.contains("${"))
			throw new IllegalArgumentException("Database URL is not set");
		
		return value;
	}
	
	protected final void cleanDatabase() throws Exception
	{		
		Connection con = null;
		Reader xml = null;
		Reader dtd = null;
		
		try
		{
			con = dataSource.getConnection();
			
			xml = new InputStreamReader(getClass().getResourceAsStream(DATA_XML));
			dtd = new InputStreamReader(getClass().getResourceAsStream(DATA_DTD));
			
			IDatabaseConnection dbConnection = new DatabaseConnection(con);
			IDataSet dataSet = new FlatXmlDataSet(xml, dtd);
			DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
		}
		finally
		{
			if (con != null) con.close();
			if (xml != null) xml.close();
			if (dtd != null) dtd.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected final Object createDao(Class entityClass, Class daoClass) throws Exception
	{
		return createDao(new HibernateDao(entityClass), daoClass);
	}

	@SuppressWarnings("unchecked")
	protected final Object createDao(HibernateDao daoTarget, Class daoClass) throws Exception
	{
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
		hibProps.setProperty("hibernate.dialect", getProperty("test.database.dialect"));
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
		factory.setDataSource(dataSource);
		factory.setHibernateProperties(hibProps);		
		factory.setAnnotatedClasses(new Class[] {
    	Article.class, Comment.class, User.class, Role.class,
    	CardSpaceToken.class, Tag.class, CardSpaceSeenToken.class });
		factory.setEntityCacheStrategies(ecProps);
		factory.setCollectionCacheStrategies(ccProps);
		
		factory.afterPropertiesSet();
		
		HibernateTransactionManager txManager = new HibernateTransactionManager();

		SessionFactory sf = (SessionFactory) factory.getObject();
		
		txManager.setSessionFactory(sf);
		
		return sf;
	}
	
	@Override
	public void setUp() throws Exception
	{
		String driver = getProperty("test.database.driver");
		String url = getProperty("test.database.url");
		String username = getProperty("test.database.username");
		String password = getProperty("test.database.password");
		
		Class.forName(driver);
		
		dataSource = new DriverManagerDataSource(driver, url, username, password);
	}
	
	@Override
	public void tearDown() throws Exception
	{
		dataSource = null;
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
