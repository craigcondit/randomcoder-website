package org.randomcoder.website;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.JvmAttributeGaugeSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.randomcoder.website.bo.AkismetModerator;
import org.randomcoder.website.bo.AppInfoBusiness;
import org.randomcoder.website.bo.AppInfoBusinessImpl;
import org.randomcoder.website.bo.ArticleBusiness;
import org.randomcoder.website.bo.ArticleBusinessImpl;
import org.randomcoder.website.bo.Moderator;
import org.randomcoder.website.bo.ResourceCache;
import org.randomcoder.website.bo.ResourceCacheImpl;
import org.randomcoder.website.bo.ScheduledTasks;
import org.randomcoder.website.bo.TagBusiness;
import org.randomcoder.website.bo.TagBusinessImpl;
import org.randomcoder.website.bo.UserBusiness;
import org.randomcoder.website.bo.UserBusinessImpl;
import org.randomcoder.website.cache.ArticleCache;
import org.randomcoder.website.cache.ArticleCacheImpl;
import org.randomcoder.website.cache.TagCache;
import org.randomcoder.website.cache.TagCacheImpl;
import org.randomcoder.website.contentfilter.ContentFilter;
import org.randomcoder.website.contentfilter.MultiContentFilter;
import org.randomcoder.website.contentfilter.TextFilter;
import org.randomcoder.website.contentfilter.XHTMLFilter;
import org.randomcoder.website.controller.ArticleController;
import org.randomcoder.website.controller.ArticleTagListController;
import org.randomcoder.website.controller.HomeController;
import org.randomcoder.website.dao.ArticleDao;
import org.randomcoder.website.dao.ArticleDaoImpl;
import org.randomcoder.website.dao.CommentDao;
import org.randomcoder.website.dao.CommentDaoImpl;
import org.randomcoder.website.dao.RoleDao;
import org.randomcoder.website.dao.RoleDaoImpl;
import org.randomcoder.website.dao.TagDao;
import org.randomcoder.website.dao.TagDaoImpl;
import org.randomcoder.website.dao.UserDao;
import org.randomcoder.website.dao.UserDaoImpl;
import org.randomcoder.website.feed.AtomFeedGenerator;
import org.randomcoder.website.feed.FeedGenerator;
import org.randomcoder.website.feed.Rss20FeedGenerator;
import org.randomcoder.website.jaxrs.features.SecurityFeature;
import org.randomcoder.website.jaxrs.providers.CorsFilter;
import org.randomcoder.website.jaxrs.resources.StaticResource;
import org.randomcoder.website.thymeleaf.ThymeleafTemplateResolver;
import org.randomcoder.website.validation.AccountCreateValidator;
import org.randomcoder.website.validation.ArticleAddValidator;
import org.randomcoder.website.validation.ArticleEditValidator;
import org.randomcoder.website.validation.ChangePasswordValidator;
import org.randomcoder.website.validation.CommentValidator;
import org.randomcoder.website.validation.TagAddValidator;
import org.randomcoder.website.validation.TagEditValidator;
import org.randomcoder.website.validation.UserAddValidator;
import org.randomcoder.website.validation.UserEditValidator;
import org.randomcoder.website.validation.UserProfileValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class WebSiteApplication extends ResourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebSiteApplication.class);

    private final Config config;

    @Inject
    public WebSiteApplication(ServiceLocator locator) throws Exception {
        logger.info("Starting application...");
        ServiceLocatorUtilities.enableImmediateScope(locator);
        this.config = Config.load();

        property(ServerProperties.WADL_FEATURE_DISABLE, true);

        register(SecurityFeature.class);
        register(new AppBinder());

        packages(
                AppInfoBusiness.class.getPackageName(),
                HomeController.class.getPackageName(),
                ArticleDao.class.getPackageName(),
                CorsFilter.class.getPackageName(),
                StaticResource.class.getPackageName());
    }

    @PostConstruct
    public void onStartup() {
        logger.info("Application started.");
    }

    private class AppBinder extends AbstractBinder {

        void singletons(Class<?>... classes) {
            for (Class<?> clazz : classes) {
                bind(clazz).to(clazz).in(Singleton.class);
            }
        }

        void singletons(Map<Class<?>, Class<?>> classMap) {
            for (var entry : classMap.entrySet()) {
                bind(entry.getValue()).to(entry.getKey()).in(Singleton.class);
            }
        }

        @Override
        protected void configure() {
            try {
                bind(config.getString(Config.AKISMET_SITE_KEY)).named(Config.AKISMET_SITE_KEY).to(String.class);
                bind(config.getString(Config.AKISMET_SITE_URL)).named(Config.AKISMET_SITE_URL).to(String.class);
                bind(config.getString(Config.REMEMBERME_KEY)).named(Config.REMEMBERME_KEY).to(String.class);
                bind(config.getLongOrDefault(Config.ARTICLE_PAGESIZE_MAX, 50)).named(Config.ARTICLE_PAGESIZE_MAX).to(Long.class);
                bind(config.getIntOrDefault(Config.PASSWORD_LENGTH_MINIMUM, 6)).named(Config.PASSWORD_LENGTH_MINIMUM).to(Integer.class);
                bind(config.getIntOrDefault(Config.ARTICLE_MAX_SUMMARY_LENGTH, 1000)).named(Config.ARTICLE_MAX_SUMMARY_LENGTH).to(Integer.class);
                bind(config.getIntOrDefault(Config.USER_PAGESIZE_MAX, 100)).named(Config.USER_PAGESIZE_MAX).to(Integer.class);
                bind(config.getIntOrDefault(Config.ARTICLE_PAGESIZE_MAX, 100)).named(Config.ARTICLE_PAGESIZE_MAX).to(Integer.class);
                bind(config.getIntOrDefault(Config.USERNAME_LENGTH_MINIMUM, 3)).named(Config.USERNAME_LENGTH_MINIMUM).to(Integer.class);
                bind(config.getIntOrDefault(Config.PASSWORD_LENGTH_MINIMUM, 6)).named(Config.PASSWORD_LENGTH_MINIMUM).to(Integer.class);
                bind(config.getIntOrDefault(Config.TAG_PAGESIZE_MAX, 100)).named(Config.TAG_PAGESIZE_MAX).to(Integer.class);
                bind(config.getIntOrDefault(Config.MODERATION_BATCH_SIZE, 5)).named(Config.MODERATION_BATCH_SIZE).to(Integer.class);

                URL feedBaseUrl = new URL(config.getString(Config.FEED_BASE_URL));
                bind(feedBaseUrl).named(Config.FEED_BASE_URL).to(URL.class);

                bind(metricRegistry()).to(MetricRegistry.class);
                bind(templateEngine()).to(ITemplateEngine.class);
                bind(dataSource(config)).to(DataSource.class);
                bind(contentFilter()).to(ContentFilter.class);

                // caches
                singletons(Map.of(
                        ArticleCache.class, ArticleCacheImpl.class,
                        TagCache.class, TagCacheImpl.class));

                // feeds
                bind(Rss20FeedGenerator.class).named("rss20FeedGenerator").to(FeedGenerator.class).in(Singleton.class);
                bind(AtomFeedGenerator.class).named("atomFeedGenerator").to(FeedGenerator.class).in(Singleton.class);

                // controllers
                singletons(
                        HomeController.class,
                        ArticleTagListController.class,
                        ArticleController.class);

                // validators
                singletons(
                        CommentValidator.class,
                        UserProfileValidator.class,
                        ArticleAddValidator.class,
                        ArticleEditValidator.class,
                        ChangePasswordValidator.class,
                        UserAddValidator.class,
                        UserEditValidator.class,
                        AccountCreateValidator.class,
                        TagAddValidator.class,
                        TagEditValidator.class);

                // business objects - immediate
                bind(AkismetModerator.class).to(Moderator.class).in(Immediate.class);
                bind(ScheduledTasks.class).to(ScheduledTasks.class).in(Immediate.class);

                // business objects
                singletons(Map.of(
                        AppInfoBusiness.class, AppInfoBusinessImpl.class,
                        ArticleBusiness.class, ArticleBusinessImpl.class,
                        TagBusiness.class, TagBusinessImpl.class,
                        UserBusiness.class, UserBusinessImpl.class,
                        ResourceCache.class, ResourceCacheImpl.class));

                // data access objects
                singletons(Map.of(
                        ArticleDao.class, ArticleDaoImpl.class,
                        CommentDao.class, CommentDaoImpl.class,
                        RoleDao.class, RoleDaoImpl.class,
                        TagDao.class, TagDaoImpl.class,
                        UserDao.class, UserDaoImpl.class));

            } catch (Exception e) {
                logger.error("Error during initialization", e);
                throw new RuntimeException(e);
            }
        }
    }

    static MetricRegistry metricRegistry() {
        var mbeanServer = ManagementFactory.getPlatformMBeanServer();
        var registry = new MetricRegistry();
        registry.registerAll("jvm.buffer", new BufferPoolMetricSet(mbeanServer));
        registry.registerAll("jvm.thread", new ThreadStatesGaugeSet());
        registry.registerAll("jvm.classloader", new ClassLoadingGaugeSet());
        registry.registerAll("jvm.gc", new GarbageCollectorMetricSet());
        registry.registerAll("jvm.attr", new JvmAttributeGaugeSet());
        registry.registerAll("jvm.memory", new MemoryUsageGaugeSet());

        return registry;
    }

    static ITemplateEngine templateEngine() {
        var engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver());
        return engine;
    }

    static ITemplateResolver templateResolver() {
        var resolver = new ThymeleafTemplateResolver();
        resolver.setPrefix("/org/randomcoder/website/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        return resolver;
    }

    static DataSource dataSource(Config config) {
        var cf = new DriverManagerConnectionFactory(
                config.getString(Config.DATABASE_URL),
                config.getString(Config.DATABASE_USERNAME),
                config.getString(Config.DATABASE_PASSWORD));

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
        cp.setMinEvictableIdle(Duration.ofSeconds(30));
        pcf.setPool(cp);

        var ds = new PoolingDataSource<>(cp);
        ds.setAccessToUnderlyingConnectionAllowed(false);
        return ds;
    }

    static MultiContentFilter contentFilter() throws Exception {
        var filters = new HashMap<String, ContentFilter>();
        filters.put("text/plain", textFilter());
        filters.put("application/xhtml+xml", xhtmlFilter());

        MultiContentFilter mcf = new MultiContentFilter(filters);
        mcf.setDefaultHandler(textFilter());
        return mcf;
    }

    static ContentFilter textFilter() throws Exception {
        return new TextFilter();
    }

    static ContentFilter xhtmlFilter() throws Exception {
        var ac = new HashSet<String>();
        ac.add("lang-xml");
        ac.add("lang-js");
        ac.add("lang-css");
        ac.add("external");

        return new XHTMLFilter(ac);
    }

}
