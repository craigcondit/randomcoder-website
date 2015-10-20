package org.randomcoder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.XmlViewResolver;

import java.util.List;

@Configuration
@SuppressWarnings("javadoc")
@EnableTransactionManagement(proxyTargetClass = true)
@ComponentScan({ "org.randomcoder.mvc" })
@EnableWebMvc
public class DispatcherContext extends WebMvcConfigurerAdapter
{
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
	{
		PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
		pspc.setIgnoreUnresolvablePlaceholders(false);
		return pspc;
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers)
	{
	  PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
		argumentResolvers.add(resolver);
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry)
	{
		// define some static content that will bypass the dispatcher
		registry
				.addResourceHandler("/**/*.html", "/**/*.css", "/**/*.js", "/**/*.ico", "/**/*.jpg", "/**/*.png", "/**/*.gif", "/**/*.txt")
				.addResourceLocations("classpath:/webapp/");
	}

	@Bean
	public RequestMappingHandlerMapping handlerMapping()
	{
		RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();
		mapping.setAlwaysUseFullPath(true);
		return mapping;
	}

	@Bean
	public ViewResolver xmlViewResolver()
	{
		XmlViewResolver resolver = new XmlViewResolver();
		resolver.setOrder(0);
		resolver.setLocation(new ClassPathResource("/views.xml"));
		return resolver;
	}

	@Bean
	public ViewResolver jspViewResolver()
	{
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setOrder(1);
		resolver.setViewClass(JstlView.class);
		resolver.setPrefix("/WEB-INF/jsp/");
		resolver.setSuffix(".jsp");
		return resolver;
	}
}