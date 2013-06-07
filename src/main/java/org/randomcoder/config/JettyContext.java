package org.randomcoder.config;

import java.util.EnumSet;

import javax.inject.Inject;
import javax.servlet.DispatcherType;

import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.randomcoder.log.JettyLog4jLog;
import org.springframework.context.annotation.*;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.*;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
@SuppressWarnings("javadoc")
public class JettyContext
{
	public static final String HTTP_PORT_PROP = "http.port";
	public static final String HTTP_ADDRESS_PROP = "http.address";
	public static final String HTTP_THREADS_PROP = "http.threads";
	
	static
	{
		// make sure jetty doesn't try to use slf4j
		Log.setLog(new JettyLog4jLog());
	}
	
	@Inject
	ConfigurableEnvironment env;
	
	@Bean(initMethod = "start", destroyMethod = "destroy")
	public Server jettyServer() throws Exception
	{		
		Server server = new Server();

		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setHost(env.getRequiredProperty(HTTP_ADDRESS_PROP, String.class));
		connector.setPort(env.getRequiredProperty(HTTP_PORT_PROP, Integer.class));
		connector.setThreadPool(new QueuedThreadPool(env.getRequiredProperty(HTTP_THREADS_PROP, Integer.class)));
		connector.setName("admin");
		server.addConnector(connector);

		HandlerCollection handlers = new HandlerCollection();

		// figure out where our content lives at runtime
		String resourceBase = getClass().getResource("/webapp/WEB-INF/web.xml").toURI().toString().replaceAll("WEB-INF/web.xml$", "");

		WebAppContext context = new WebAppContext();
		context.setContextPath("/");
		context.setResourceBase(resourceBase);		

//		// define a root spring context
//		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
//		rootContext.setEnvironment(env);
//		rootContext.register(RootContext.class);
//		context.addEventListener(new ContextLoaderListener(rootContext));
//		
//		// define a dispatcher context
//		AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
//		dispatcherContext.setEnvironment(env);
//		dispatcherContext.register(DispatcherContext.class);
//
//		FilterHolder sec = new FilterHolder();
//		sec.setFilter(new DelegatingFilterProxy());
//		sec.setName("springSecurityFilterChain");
//		
//		context.addFilter(sec, "/*", EnumSet.allOf(DispatcherType.class));
		
//		FilterHolder etag = new FilterHolder();
//		etag.setFilter(new ShallowEtagHeaderFilter());
//		etag.setName("etag");
//		
//		context.addFilter(etag, "*.js", EnumSet.of(DispatcherType.REQUEST));
//		context.addFilter(etag, "*.css", EnumSet.of(DispatcherType.REQUEST));
//		context.addFilter(etag, "*.jpg", EnumSet.of(DispatcherType.REQUEST));
//		context.addFilter(etag, "*.png", EnumSet.of(DispatcherType.REQUEST));
//		context.addFilter(etag, "*.gif", EnumSet.of(DispatcherType.REQUEST));
//		
//		ErrorPageErrorHandler eh = new ErrorPageErrorHandler();
//		eh.addErrorPage(400, 599, "/error/code");
//		eh.addErrorPage(Throwable.class, "/error/exception");		
//		context.setErrorHandler(eh);
//
//		context.addServlet(new ServletHolder("dispatcher", new DispatcherServlet(dispatcherContext)), "/*");
//		context.addServlet(new ServletHolder("jsp", new JspServlet()), "/WEB-INF/view/*");
//		context.addServlet(new ServletHolder("jsp", new JspServlet()), "/WEB-INF/tiles/*");

		handlers.addHandler(context);
		server.setHandler(handlers);

		return server;
	}
}