package org.randomcoder.test;

import java.io.*;
import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.dbunit.database.*;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.SessionFactory;
import org.randomcoder.article.Article;
import org.randomcoder.article.comment.*;
import org.randomcoder.dao.finder.FinderIntroductionInterceptor;
import org.randomcoder.dao.hibernate.HibernateDao;
import org.randomcoder.tag.Tag;
import org.randomcoder.user.*;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate3.*;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.*;

@SuppressWarnings("javadoc")
abstract public class AbstractDaoTestCase extends TestCase
{
	private static DataSource dataSource;
	private SessionFactory sessionFactory;
	private HibernateTransactionManager txManager;
	
	private static final String DATA_XML = "/database-data.xml";
	private static final String DATA_DTD = "/database-schema.dtd";
	private static final String DB_PROPS = "/database.properties";
	
	private static final Properties dbProps;
	private TransactionStatus tx;
	
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
			
			IDataSet dataSet = new FlatXmlDataSetBuilder()
				.setMetaDataSetFromDtd(dtd)
				.build(xml);
			
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
		hibProps.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
		hibProps.setProperty("hibernate.dialect", getProperty("test.database.dialect"));
		hibProps.setProperty("hibernate.show_sql", "false");
		hibProps.setProperty("hibernate.max_fetch_depth", "2");
		hibProps.setProperty("hibernate.jdbc.fetch_size", "100");
		hibProps.setProperty("hibernate.jdbc.batch_size", "10");
		hibProps.setProperty("hibernate.cache.use_query_cache", "false");
		hibProps.setProperty("hibernate.cache.use_second_level_cache", "false");
		
		AnnotationSessionFactoryBean factory = new AnnotationSessionFactoryBean();
		factory.setDataSource(dataSource);
		factory.setHibernateProperties(hibProps);		
		factory.setAnnotatedClasses(new Class[] {
	    	Article.class, Comment.class, CommentReferrer.class, CommentIp.class,
	    	CommentUserAgent.class, User.class, Role.class, Tag.class });
		factory.afterPropertiesSet();
		
		txManager = new HibernateTransactionManager();
		SessionFactory sf = (SessionFactory) factory.getObject();
		txManager.setSessionFactory(sf);
		return sf;
	}
	
	@Override
	public void setUp() throws Exception
	{
		String url = getProperty("test.database.url");
		String username = getProperty("test.database.username");
		String password = getProperty("test.database.password");
		
		dataSource = new DriverManagerDataSource(url, username, password);
	}
	
	@Override
	public void tearDown() throws Exception
	{
		dataSource = null;
	}	
	
	protected final void begin() throws Exception
	{
		assertNull("Transaction active", tx);
		tx = txManager.getTransaction(new DefaultTransactionDefinition());
	}
	
	protected final void commit() throws Exception
	{
		assertNotNull("No transaction active", tx);
		txManager.commit(tx);
		tx = null;
	}
	
	protected final void rollback() throws Exception
	{
		assertNotNull("No transaction active", tx);
		txManager.rollback(tx);
		tx = null;
	}

	protected final void clear() throws Exception
	{
		SessionFactoryUtils.getSession(sessionFactory, false).clear();
	}

	protected final void flush() throws Exception
	{
		SessionFactoryUtils.getSession(sessionFactory, false).flush();
	}
}