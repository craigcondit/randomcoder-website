package org.randomcoder.config;

import javax.inject.Inject;

import org.springframework.context.annotation.*;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles2.*;

@Configuration
//@ComponentScan(basePackages = {"org.randomcoder.craigandanne.controller", "org.randomcoder.craigandanne.validator" })
@SuppressWarnings("javadoc")
@EnableTransactionManagement
public class DispatcherContext extends WebMvcConfigurationSupport
{
	@Inject
	Environment env;
	
	@Bean
	public boolean exposeExceptionDetails()
	{
		return env.getRequiredProperty("expose.exception.details", Boolean.class);
	}
	
	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry)
	{
		// define some static content that will bypass the dispatcher		
		registry
			.addResourceHandler("/**/*.html", "/**/*.css", "/**/*.js", "/**/*.ico", "/**/*.jpg", "/**/*.png", "/**/*.gif")
			.addResourceLocations("classpath:/webapp/");
	}
	
	@Bean
	public ViewResolver viewResolver()
	{
		UrlBasedViewResolver resolver = new UrlBasedViewResolver();
		resolver.setViewClass(TilesView.class);
		return resolver;
	}
	
	@Bean
	public TilesConfigurer tilesConfigurer()
	{
		TilesConfigurer tc = new TilesConfigurer();
		tc.setDefinitions(new String[] { "/WEB-INF/tiles.xml" });
		return tc;
	}
	
	@Bean
	public ResourceBundleMessageSource messageSource()
	{
		ResourceBundleMessageSource src = new ResourceBundleMessageSource();
		src.setBasename("errors");
		return src;
	}
}