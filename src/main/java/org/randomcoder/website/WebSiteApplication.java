package org.randomcoder.website;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
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
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.randomcoder.website.bo.AkismetModerator;
import org.randomcoder.website.bo.AppInfoBusiness;
import org.randomcoder.website.bo.AppInfoBusinessImpl;
import org.randomcoder.website.bo.ArticleBusiness;
import org.randomcoder.website.bo.ArticleBusinessImpl;
import org.randomcoder.website.bo.Moderator;
import org.randomcoder.website.bo.TagBusiness;
import org.randomcoder.website.bo.TagBusinessImpl;
import org.randomcoder.website.contentfilter.ContentFilter;
import org.randomcoder.website.contentfilter.MultiContentFilter;
import org.randomcoder.website.contentfilter.TextFilter;
import org.randomcoder.website.contentfilter.XHTMLFilter;
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
import org.randomcoder.website.jaxrs.features.SecurityFeature;
import org.randomcoder.website.jaxrs.providers.CorsFilter;
import org.randomcoder.website.jaxrs.resources.StaticResource;
import org.randomcoder.website.thymeleaf.ThymeleafTemplateResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;

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
        @Override
        protected void configure() {
            try {
                bind(config.getString(Config.AKISMET_SITE_KEY)).named(Config.AKISMET_SITE_KEY).to(String.class);
                bind(config.getString(Config.AKISMET_SITE_URL)).named(Config.AKISMET_SITE_URL).to(String.class);
                bind(config.getLongOrDefault(Config.ARTICLE_PAGESIZE_MAX, 50)).named(Config.ARTICLE_PAGESIZE_MAX).to(Long.class);

                bind(templateEngine()).to(ITemplateEngine.class);
                bind(dataSource(config)).to(DataSource.class);
                bind(contentFilter()).to(ContentFilter.class);

                // controllers
                bind(HomeController.class).to(HomeController.class);
                bind(ArticleTagListController.class).to(ArticleTagListController.class);

                // business objects
                bind(AkismetModerator.class).in(Immediate.class).to(Moderator.class);
                bind(AppInfoBusinessImpl.class).to(AppInfoBusiness.class);
                bind(ArticleBusinessImpl.class).to(ArticleBusiness.class);
                bind(TagBusinessImpl.class).to(TagBusiness.class);

                // data access objects
                bind(ArticleDaoImpl.class).to(ArticleDao.class);
                bind(CommentDaoImpl.class).to(CommentDao.class);
                bind(RoleDaoImpl.class).to(RoleDao.class);
                bind(TagDaoImpl.class).to(TagDao.class);
                bind(UserDaoImpl.class).to(UserDao.class);
            } catch (Exception e) {
                logger.error("Error during initialization", e);
                throw new RuntimeException(e);
            }
        }
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
