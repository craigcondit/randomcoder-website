package org.randomcoder.config;

import jakarta.inject.Inject;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.randomcoder.article.moderation.AkismetModerator;
import org.randomcoder.article.moderation.Moderator;
import org.randomcoder.bo.AppInfoBusiness;
import org.randomcoder.content.ContentFilter;
import org.randomcoder.content.MultiContentFilter;
import org.randomcoder.content.TextFilter;
import org.randomcoder.content.XHTMLFilter;
import org.randomcoder.feed.AtomFeedGenerator;
import org.randomcoder.feed.FeedGenerator;
import org.randomcoder.feed.Rss20FeedGenerator;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
@ComponentScan({"org.randomcoder.dao", "org.randomcoder.bo", "org.randomcoder.security.spring"})
@ImportResource({"classpath:spring-security.xml"})
public class RootContext implements SchedulingConfigurer {

    @Inject
    Environment env;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        pspc.setIgnoreUnresolvablePlaceholders(false);
        return pspc;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        registrar.setScheduler(taskScheduler());
    }

    @Bean(destroyMethod = "shutdown")
    public Executor taskScheduler() {
        return Executors.newScheduledThreadPool(2);
    }

    @Bean
    public DataSource dataSource() {
        var cf = new DriverManagerConnectionFactory(
                env.getRequiredProperty("database.url"),
                env.getRequiredProperty("database.username"),
                env.getRequiredProperty("database.password"));

        var pcf = new PoolableConnectionFactory(cf, null);
        pcf.setDefaultAutoCommit(false);
        pcf.setDefaultReadOnly(false);
        pcf.setAutoCommitOnReturn(false);
        pcf.setRollbackOnReturn(true);
        pcf.setValidationQueryTimeout(5);
        pcf.setValidationQuery("SELECT 1");
        pcf.setFastFailValidation(true);

        var cp = new GenericObjectPool<>(pcf);
        cp.setMinIdle(2);
        cp.setMaxIdle(5);
        cp.setMaxTotal(20);
        cp.setMinEvictableIdleTime(Duration.ofSeconds(30));
        pcf.setPool(cp);

        var ds = new PoolingDataSource<>(cp);
        ds.setAccessToUnderlyingConnectionAllowed(false);
        return ds;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
        ms.setBasename("ApplicationResources");
        return ms;
    }

    @Bean
    public MultiContentFilter contentFilter() throws Exception {
        Map<String, ContentFilter> filters = new HashMap<String, ContentFilter>();
        filters.put("text/plain", textFilter());
        filters.put("application/xhtml+xml", xhtmlFilter());

        MultiContentFilter mcf = new MultiContentFilter(filters);
        mcf.setDefaultHandler(textFilter());
        return mcf;
    }

    @Bean
    public ContentFilter textFilter() throws Exception {
        return new TextFilter();
    }

    @Bean
    public ContentFilter xhtmlFilter() throws Exception {
        Set<String> ac = new HashSet<String>();
        ac.add("lang-xml");
        ac.add("lang-js");
        ac.add("lang-css");
        ac.add("external");

        return new XHTMLFilter(ac);
    }

    @Bean
    public FeedGenerator atomFeedGenerator(final AppInfoBusiness appInfo)
            throws Exception {
        return new AtomFeedGenerator(appInfo, "https://randomcoder.org/", "tag:randomcoder.org,2007:", contentFilter());
    }

    @Bean
    public FeedGenerator rss20FeedGenerator(final AppInfoBusiness appInfo)
            throws Exception {
        return new Rss20FeedGenerator("https://randomcoder.org/", contentFilter(), appInfo);
    }

    @Bean
    public Moderator moderator(final AppInfoBusiness applicationInformation) {
        return new AkismetModerator(
                env.getRequiredProperty("akismet.site.key"),
                env.getRequiredProperty("akismet.site.url"),
                applicationInformation);
    }

}
