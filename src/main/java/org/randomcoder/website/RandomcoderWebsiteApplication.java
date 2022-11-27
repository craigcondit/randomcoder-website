package org.randomcoder.website;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.randomcoder.website.bo.AppInfoBusiness;
import org.randomcoder.website.bo.AppInfoBusinessImpl;
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

public class RandomcoderWebsiteApplication extends ResourceConfig {

    private static final Logger LOG = LoggerFactory.getLogger(RandomcoderWebsiteApplication.class);

    private final Config config;

    @Inject
    public RandomcoderWebsiteApplication(ServiceLocator locator) throws Exception {
        LOG.info("Starting application...");
        ServiceLocatorUtilities.enableImmediateScope(locator);
        this.config = Config.load();

        property(ServerProperties.WADL_FEATURE_DISABLE, true);

        register(new AppBinder());

        packages(AppInfoBusiness.class.getPackageName());
        packages(ArticleDao.class.getPackageName());
        packages(CorsFilter.class.getPackageName());
        packages(StaticResource.class.getPackageName());
    }

    @PostConstruct
    @Inject
    public void onStartup() {
        LOG.info("App started.");
    }

    private class AppBinder extends AbstractBinder {
        @Override
        protected void configure() {
            bind(templateEngine()).to(ITemplateEngine.class);
            bind(dataSource(config)).to(DataSource.class);

            // business objects
            bind(AppInfoBusinessImpl.class).to(AppInfoBusiness.class);

            // data access objects
            bind(ArticleDaoImpl.class).to(ArticleDao.class);
            bind(CommentDaoImpl.class).to(CommentDao.class);
            bind(RoleDaoImpl.class).to(RoleDao.class);
            bind(TagDaoImpl.class).to(TagDao.class);
            bind(UserDaoImpl.class).to(UserDao.class);
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

}
