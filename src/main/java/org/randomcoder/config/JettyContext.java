package org.randomcoder.config;

import java.util.EnumSet;

import javax.inject.Inject;
import javax.servlet.*;

import org.acegisecurity.util.FilterToBeanProxy;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.servlet.ServletContextHandler.JspConfig;
import org.eclipse.jetty.servlet.ServletContextHandler.JspPropertyGroup;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.randomcoder.log.JettyLog4jLog;
import org.springframework.context.annotation.*;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.orm.hibernate3.support.OpenSessionInViewFilter;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.*;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
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
		connector.setForwarded(true);

		server.addConnector(connector);

		HandlerCollection handlers = new HandlerCollection();

		WebAppContext context = new WebAppContext();
		context.setContextPath("/");
		context.setWelcomeFiles(new String[] { "index.jsp", "index.html" });
		context.getMimeTypes().addMimeMapping("wsdl", "text/xml");
		context.setInitParameter("javax.servlet.jsp.jstl.fmt.localizationContext", "ApplicationResources");
		
		// configure session handling
		context.getSessionHandler().getSessionManager().setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
		context.getSessionHandler().getSessionManager().setMaxInactiveInterval(1800);

		// configure JSP files
		JspPropertyGroup jsppg = new JspPropertyGroup();
		jsppg.addUrlPattern("*.jsp");
		jsppg.setElIgnored("false");
		jsppg.setPageEncoding("UTF-8");
		jsppg.setScriptingInvalid("true");
		JspConfig jspc = new JspConfig();
		jspc.addJspPropertyGroup(jsppg);
		context.getServletContext().setJspConfigDescriptor(jspc);
		
		// figure out where our content lives at runtime
		String resourceBase = getClass().getResource("/webapp/WEB-INF/web.xml").toURI().toString().replaceAll("WEB-INF/web.xml$", "");
		context.setResourceBase(resourceBase);

		// define a root spring context
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.setEnvironment(env);
		rootContext.register(RootContext.class);

		context.addEventListener(new ContextLoaderListener(rootContext));

		// open a single Hibernate session per request
		FilterHolder osiv = new FilterHolder();
		osiv.setName("OpenSessionInViewFilter");
		osiv.setFilter(new OpenSessionInViewFilter());		
		context.addFilter(osiv, "/*", EnumSet.of(DispatcherType.REQUEST));
		
		// define acegi security filter
		FilterHolder acegi = new FilterHolder();
		acegi.setName("AcegiFilterChain");		
		acegi.setFilter(new FilterToBeanProxy());
		acegi.setInitParameter("targetBean", "filterChainProxy");
		context.addFilter(acegi, "/*", EnumSet.of(DispatcherType.REQUEST));
		
		// define etag filter
		FilterHolder etag = new FilterHolder();
		etag.setFilter(new ShallowEtagHeaderFilter());
		etag.setName("etag");

		context.addFilter(etag, "*.js", EnumSet.of(DispatcherType.REQUEST));
		context.addFilter(etag, "*.css", EnumSet.of(DispatcherType.REQUEST));
		context.addFilter(etag, "*.jpg", EnumSet.of(DispatcherType.REQUEST));
		context.addFilter(etag, "*.png", EnumSet.of(DispatcherType.REQUEST));
		context.addFilter(etag, "*.gif", EnumSet.of(DispatcherType.REQUEST));
		
		// define dispatcher servlet
		XmlWebApplicationContext dispatcherContext = new XmlWebApplicationContext();
		dispatcherContext.setParent(rootContext);
		dispatcherContext.setConfigLocation("classpath:/webapp/WEB-INF/springmvc-servlet.xml");
		
		ServletHolder dispatcher = new ServletHolder("springmvc", new DispatcherServlet(dispatcherContext));
		
		context.addServlet(dispatcher, "");
		context.addServlet(dispatcher, "/account/*");
		context.addServlet(dispatcher, "/article/*");
		context.addServlet(dispatcher, "/articles/*");
		context.addServlet(dispatcher, "/comment/*");
		context.addServlet(dispatcher, "/download/*");
		context.addServlet(dispatcher, "/feeds/*");
		context.addServlet(dispatcher, "/legal/*");
		context.addServlet(dispatcher, "/login");
		context.addServlet(dispatcher, "/login-error");
		context.addServlet(dispatcher, "/redirect");
		context.addServlet(dispatcher, "/tag/*");
		context.addServlet(dispatcher, "/tags/*");
		context.addServlet(dispatcher, "/user/*");
		
		// context.addServlet(new ServletHolder("dispatcher", new
		// DispatcherServlet(dispatcherContext)), "/*");
		// context.addServlet(new ServletHolder("jsp", new JspServlet()),
		// "/WEB-INF/view/*");
		// context.addServlet(new ServletHolder("jsp", new JspServlet()),
		// "/WEB-INF/tiles/*");

		// TODO use JavaConfig instead of XML
		
		// // define a dispatcher context
		// AnnotationConfigWebApplicationContext dispatcherContext = new
		// AnnotationConfigWebApplicationContext();
		// dispatcherContext.setEnvironment(env);
		// dispatcherContext.register(DispatcherContext.class);
		//
		// ErrorPageErrorHandler eh = new ErrorPageErrorHandler();
		// eh.addErrorPage(400, 599, "/error/code");
		// eh.addErrorPage(Throwable.class, "/error/exception");
		// context.setErrorHandler(eh);
		
		handlers.addHandler(context);
		server.setHandler(handlers);

		return server;
	}
}