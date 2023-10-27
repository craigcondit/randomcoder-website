package org.randomcoder.website;

import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.SecuredRedirectHandler;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.util.ArrayList;

public class WebSite {

    private static final Logger logger = LoggerFactory.getLogger(WebSite.class);

    private final Config config;
    private final Server server;

    public WebSite(Config config) throws Exception {
        logger.info("Starting web server on {}:{} using document root {}", config.getString(Config.HTTP_ADDRESS), config.getString(Config.HTTP_PORT), contentBase(this));
        this.config = config;
        this.server = createServer(this, config);
    }

    static Server createServer(Object owner, Config config) throws IOException {
        var server = new Server(threadPool(config));
        server.addBean(new ScheduledExecutorScheduler());

        var httpConfig = httpConfiguration(config);
        var handlers = new ContextHandlerCollection();
        var rootContext = rootContext(owner);

        configureForward(config, httpConfig);
        configureForceHttps(config, handlers);

        addJerseyContainer(rootContext);

        handlers.addHandler(rootContext);
        server.setHandler(handlers);

        addHttpConnector(config, httpConfig, server);

        return server;
    }

    static QueuedThreadPool threadPool(Config config) {
        return new QueuedThreadPool(config.getInt(Config.HTTP_THREADS), config.getInt(Config.HTTP_THREADS_MIN), 60_000);
    }

    static HttpConfiguration httpConfiguration(Config config) {
        var httpConfig = new HttpConfiguration();
        httpConfig.setSecurePort(config.getInt(Config.HTTP_SECURE_PORT));
        return httpConfig;
    }

    static ServletContextHandler rootContext(Object owner) throws IOException {
        var resourceFactory = ResourceFactory.root();
        var root = new ServletContextHandler();
        root.setContextPath("/");
        root.setBaseResource(resourceFactory.newResource(contentBase(owner)));
        root.setWelcomeFiles(new String[]{"index.html"});
        return root;
    }

    static void configureForward(Config config, HttpConfiguration httpConfig) {
        if (config.getBoolean(Config.HTTP_FORWARDED)) {
            httpConfig.addCustomizer(new ForwardedRequestCustomizer());
        }
    }

    static void configureForceHttps(Config config, ContextHandlerCollection handlers) {
        if (config.getBoolean(Config.HTTPS_FORCED)) {
            handlers.addHandler(new SecuredRedirectHandler());
        }
    }

    static void addJerseyContainer(ServletContextHandler rootContext) {
        var jerseyHolder = new ServletHolder(ServletContainer.class);
        jerseyHolder.setInitParameter(
                ServletProperties.JAXRS_APPLICATION_CLASS, WebSiteApplication.class.getName());
        jerseyHolder.setInitOrder(1);
        rootContext.addServlet(jerseyHolder, "/*");
    }

    static void addHttpConnector(Config config, HttpConfiguration httpConfig, Server server) {
        var connectionFactories = new ArrayList<>();

        // http/1.1 connector
        connectionFactories.add(new HttpConnectionFactory(httpConfig));

        // h2c connector
        var http2cFactory = new HTTP2CServerConnectionFactory(httpConfig);
        http2cFactory.setMaxConcurrentStreams(-1);
        http2cFactory.setInitialStreamRecvWindow(65535);
        connectionFactories.add(http2cFactory);

        var httpConnector = new ServerConnector(
                server, 1, -1, connectionFactories.toArray(new ConnectionFactory[]{}));
        var httpAddress = config.getString(Config.HTTP_ADDRESS);
        httpConnector.setHost("*".equals(httpAddress) ? "0.0.0.0" : httpAddress);
        httpConnector.setPort(config.getInt(Config.HTTP_PORT));

        server.addConnector(httpConnector);
    }

    static String contentBase(Object owner) {
        return owner.getClass().getResource("/org/randomcoder/website/content/robots.txt").toExternalForm().replaceAll("/content/robots\\.txt$", "/content/");
    }

    static void redirectJulLogging(java.util.logging.Level level) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        java.util.logging.Logger.getLogger("").setLevel(level);
    }

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        var config = Config.load();
        redirectJulLogging(java.util.logging.Level.FINE);

        var site = new WebSite(config);
        Runtime.getRuntime().addShutdownHook(new Thread(site::stop));
        site.start();

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        logger.info("Web site started in {}ms", elapsedTime);
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
