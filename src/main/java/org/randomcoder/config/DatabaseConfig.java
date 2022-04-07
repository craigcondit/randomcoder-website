package org.randomcoder.config;

import org.ehcache.jsr107.EhcacheCachingProvider;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.dialect.PostgreSQL95Dialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration @EnableJpaRepositories("org.randomcoder.db")
public class DatabaseConfig {
  @Inject Environment env;

  @Bean public DataSource dataSource() {
    DriverManagerDataSource ds = new DriverManagerDataSource();
    ds.setUrl(env.getRequiredProperty("database.url"));
    ds.setUsername(env.getRequiredProperty("database.username"));
    ds.setPassword(env.getRequiredProperty("database.password"));
    return ds;
  }

  @Bean public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean emfb =
        new LocalContainerEntityManagerFactoryBean();

    emfb.setDataSource(dataSource());
    emfb.setPackagesToScan(new String[] { "org.randomcoder.db" });
    emfb.setPersistenceProviderClass(HibernatePersistenceProvider.class);

    Properties props = new Properties();
    props.setProperty("hibernate.dialect", PostgreSQL95Dialect.class.getName());
    props.setProperty("hibernate.ejb.naming_strategy",
        ImprovedNamingStrategy.class.getName());
    props.setProperty("hibernate.format_sql", "false");
    props.setProperty("hibernate.show_sql", "false");
    props.setProperty("hibernate.max_fetch_depth", "2");
    props.setProperty("hibernate.jdbc.fetch_size", "100");
    props.setProperty("hibernate.jdbc.batch_size", "10");
    props.setProperty("hibernate.cache.use_query_cache", "true");
    props.setProperty("hibernate.cache.region.factory_class", "jcache");
    props.setProperty("hibernate.javax.cache.provider",
        EhcacheCachingProvider.class.getName());
    props.setProperty("hibernate.javax.cache.uri",
        getClass().getResource("/ehcache.xml").toExternalForm());
    props.setProperty("hibernate.javax.cache.missing_cache_strategy", "fail");
    emfb.setJpaProperties(props);
    return emfb;
  }

  @Bean public JpaTransactionManager transactionManager(
      EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory);
    return transactionManager;
  }
}
