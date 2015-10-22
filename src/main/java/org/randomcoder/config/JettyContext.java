package org.randomcoder.config;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.randomcoder.log.JettyLog4jLog;
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

import java.util.EnumSet;

import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.SessionTrackingMode;

@Configuration
@SuppressWarnings("javadoc")
public class JettyContext {
  public static final String HTTP_PORT_PROP = "http.port";
  public static final String HTTP_ADDRESS_PROP = "http.address";
  public static final String HTTP_THREADS_PROP = "http.threads";

  static {
    // make sure jetty doesn't try to use slf4j
    Log.setLog(new JettyLog4jLog());
  }

  @Inject
  ConfigurableEnvironment env;

  @Bean(initMethod = "start", destroyMethod = "destroy")
  public Server jettyServer() throws Exception {
    Server server = new Server();

    SelectChannelConnector connector = new SelectChannelConnector();
    connector.setHost(env.getRequiredProperty(HTTP_ADDRESS_PROP, String.class));
    connector.setPort(env.getRequiredProperty(HTTP_PORT_PROP, Integer.class));
    connector.setThreadPool(new QueuedThreadPool(env.getRequiredProperty(HTTP_THREADS_PROP, Integer.class)));
    connector.setName("admin");
    connector.setForwarded(true);

    server.addConnector(connector);

    HandlerCollection handlers = new HandlerCollection();

    WebAppContext context = new WebAppContext();
    context.setContextPath("/");
    context.setWelcomeFiles(new String[] { "index.html" });
    context.getMimeTypes().addMimeMapping("wsdl", "text/xml");

    // configure session handling
    context.getSessionHandler().getSessionManager().setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
    context.getSessionHandler().getSessionManager().setMaxInactiveInterval(1800);

    // figure out where our content lives at runtime
    String resourceBase =
            getClass().getResource("/webapp/WEB-INF/web.xml").toURI().toString().replaceAll("WEB-INF/web.xml$", "");
    context.setResourceBase(resourceBase);

    // define a root spring context
    AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
    rootContext.setEnvironment(env);
    rootContext.register(RootContext.class);

    context.addEventListener(new ContextLoaderListener(rootContext));

    // define disable url session filter
    FilterHolder disableUrlSession = new FilterHolder();
    disableUrlSession.setFilter(new DisableUrlSessionFilter());
    disableUrlSession.setName("disableUrlSessionFilter");
    context.addFilter(disableUrlSession, "/*", EnumSet.of(DispatcherType.REQUEST));

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
    AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
    dispatcherContext.setParent(rootContext);
    dispatcherContext.setEnvironment(env);
    dispatcherContext.register(DispatcherContext.class);
    dispatcherContext.setEnvironment(env);

    context.addServlet(new ServletHolder("dispatcher", new DispatcherServlet(dispatcherContext)), "/*");

    handlers.addHandler(context);
    server.setHandler(handlers);

    return server;
  }
}