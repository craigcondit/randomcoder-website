package org.randomcoder.config;

import java.util.Properties;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.*;

@Configuration
@SuppressWarnings("javadoc")
@EnableJpaRepositories("org.randomcoder.db")
public class DatabaseConfig
{
	@Inject
	Environment env;

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
	public LocalContainerEntityManagerFactoryBean entityManagerFactory()
	{
		LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();

		emfb.setDataSource(dataSource());
		emfb.setPackagesToScan(new String[] { "org.randomcoder.db" });
		emfb.setPersistenceProviderClass(HibernatePersistenceProvider.class);

		Properties props = new Properties();
		props.setProperty("hibernate.dialect", PostgreSQL82Dialect.class.getName());
		props.setProperty("hibernate.ejb.naming_strategy", ImprovedNamingStrategy.class.getName());
		props.setProperty("hibernate.format_sql", "false");
		props.setProperty("hibernate.show_sql", "false");
		props.setProperty("hibernate.max_fetch_depth", "2");
		props.setProperty("hibernate.jdbc.fetch_size", "100");
		props.setProperty("hibernate.jdbc.batch_size", "10");
		props.setProperty("hibernate.cache.use_query_cache", "true");
		props.setProperty("hibernate.cache.region.factory_class", SingletonEhCacheRegionFactory.class.getName());
		emfb.setJpaProperties(props);

		return emfb;
	}

	@Bean
	public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory)
	{
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}
}