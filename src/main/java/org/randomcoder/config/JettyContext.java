package org.randomcoder.config;

import jakarta.inject.Inject;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.SessionTrackingMode;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.ProxyConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.SecuredRedirectHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.randomcoder.security.DisableUrlSessionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Configuration
public class JettyContext {
    public static final String HTTP_PORT_PROP = "http.port";
    public static final String HTTP_ADDRESS_PROP = "http.address";
    public static final String HTTP_THREADS_PROP = "http.threads";
    public static final String HTTP_THREADS_MIN_PROP = "http.threads.min";
    public static final String HTTP_FORWARDED_PROP = "http.forwarded";
    public static final String HTTP_PROXIED_PROP = "http.proxied";
    public static final String HTTPS_FORCED_PROP = "https.forced";

    @Inject
    ConfigurableEnvironment env;

    @Bean(initMethod = "start", destroyMethod = "destroy")
    public Server jettyServer() throws Exception {
        HandlerCollection handlers = new HandlerCollection();

        if (env.getRequiredProperty(HTTPS_FORCED_PROP, Boolean.class)) {
            handlers.addHandler(new SecuredRedirectHandler());
        }

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setWelcomeFiles(new String[]{"index.html"});
        context.getMimeTypes().addMimeMapping("wsdl", "text/xml");

        // configure session handling
        context.getSessionHandler()
                .setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
        context.getSessionHandler().setMaxInactiveInterval(1800);

        // figure out where our content lives at runtime
        String resourceBase =
                getClass().getResource("/webapp/WEB-INF/templates/home.html").toURI()
                        .toString().replaceAll("WEB-INF/templates/home.html$", "");
        context.setResourceBase(resourceBase);

        // define a root spring context
        AnnotationConfigWebApplicationContext rootContext =
                new AnnotationConfigWebApplicationContext();
        rootContext.setEnvironment(env);
        rootContext.register(RootContext.class);

        context.addEventListener(new ContextLoaderListener(rootContext));

        // define disable url session filter
        FilterHolder disableUrlSession = new FilterHolder();
        disableUrlSession.setFilter(new DisableUrlSessionFilter());
        disableUrlSession.setName("disableUrlSessionFilter");
        context
                .addFilter(disableUrlSession, "/*", EnumSet.of(DispatcherType.REQUEST));

        // define spring security filter
        FilterHolder sec = new FilterHolder();
        sec.setFilter(new DelegatingFilterProxy());
        sec.setName("springSecurityFilterChain");
        context.addFilter(sec, "/*", EnumSet.allOf(DispatcherType.class));

        // define hidden method filter
        FilterHolder hiddenMethod = new FilterHolder();
        hiddenMethod.setFilter(new HiddenHttpMethodFilter());
        hiddenMethod.setName("hiddenMethod");
        context.addFilter(hiddenMethod, "/*", EnumSet.of(DispatcherType.REQUEST));

        // define etag filter to better cache static content
        FilterHolder etag = new FilterHolder();
        etag.setFilter(new ShallowEtagHeaderFilter());
        etag.setName("etag");
        context.addFilter(etag, "*.js", EnumSet.of(DispatcherType.REQUEST));
        context.addFilter(etag, "*.css", EnumSet.of(DispatcherType.REQUEST));
        context.addFilter(etag, "*.jpg", EnumSet.of(DispatcherType.REQUEST));
        context.addFilter(etag, "*.png", EnumSet.of(DispatcherType.REQUEST));
        context.addFilter(etag, "*.gif", EnumSet.of(DispatcherType.REQUEST));

        // define dispatcher servlet
        AnnotationConfigWebApplicationContext dispatcherContext =
                new AnnotationConfigWebApplicationContext();
        dispatcherContext.setParent(rootContext);
        dispatcherContext.setEnvironment(env);
        dispatcherContext.register(DispatcherContext.class);
        dispatcherContext.setEnvironment(env);

        context.addServlet(new ServletHolder("dispatcher",
                new DispatcherServlet(dispatcherContext)), "/*");

        handlers.addHandler(context);

        QueuedThreadPool threadPool = new QueuedThreadPool(
                env.getRequiredProperty(HTTP_THREADS_PROP, Integer.class),
                env.getRequiredProperty(HTTP_THREADS_MIN_PROP, Integer.class), 60000);

        Server server = new Server(threadPool);
        server.addBean(new ScheduledExecutorScheduler());

        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSecurePort(443);

        if (env.getRequiredProperty(HTTP_FORWARDED_PROP, Boolean.class)) {
            httpConfig.addCustomizer(new ForwardedRequestCustomizer());
        }

        List<ConnectionFactory> connectionFactories = new ArrayList<>();

        // proxy connector
        if (env.getRequiredProperty(HTTP_PROXIED_PROP, Boolean.class)) {
            connectionFactories.add(new ProxyConnectionFactory());
        }

        // http/1.1 connector
        connectionFactories.add(new HttpConnectionFactory(httpConfig));

        // h2c connector
        HTTP2CServerConnectionFactory http2cFactory =
                new HTTP2CServerConnectionFactory(httpConfig);
        http2cFactory.setMaxConcurrentStreams(-1);
        http2cFactory.setInitialStreamRecvWindow(65535);
        connectionFactories.add(http2cFactory);

        ServerConnector httpConnector = new ServerConnector(server, 1, -1,
                connectionFactories.toArray(new ConnectionFactory[]{}));
        httpConnector
                .setHost(env.getRequiredProperty(HTTP_ADDRESS_PROP, String.class));
        httpConnector
                .setPort(env.getRequiredProperty(HTTP_PORT_PROP, Integer.class));
        server.addConnector(httpConnector);

        server.setHandler(handlers);

        return server;
    }
}
