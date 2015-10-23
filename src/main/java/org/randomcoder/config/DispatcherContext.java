package org.randomcoder.config;

import org.randomcoder.mvc.SuffixedBeanNameViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.extras.conditionalcomments.dialect.ConditionalCommentsDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import java.util.List;

import javax.inject.Inject;

@Configuration
@SuppressWarnings("javadoc")
@EnableTransactionManagement(proxyTargetClass = true)
@ComponentScan({ "org.randomcoder.mvc" })
@EnableWebMvc
public class DispatcherContext extends WebMvcConfigurerAdapter {

  @Inject
  Environment env;

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
    pspc.setIgnoreUnresolvablePlaceholders(false);
    return pspc;
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
    resolver.setPageParameterName("page");
    resolver.setSizeParameterName("size");
    resolver.setPrefix("page.");
    argumentResolvers.add(resolver);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // define some static content that will bypass the dispatcher
    registry
            .addResourceHandler("/**/*.html", "/**/*.css", "/**/*.js", "/**/*.ico", "/**/*.jpg", "/**/*.png", "/**/*.gif",
                    "/**/*.txt")
            .addResourceLocations("classpath:/webapp/");
  }

  @Bean
  public RequestMappingHandlerMapping handlerMapping() {
    RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();
    mapping.setAlwaysUseFullPath(true);
    return mapping;
  }

  @Bean
  public TemplateResolver templateResolver() {
    ServletContextTemplateResolver resolver = new ServletContextTemplateResolver();
    resolver.setPrefix("/WEB-INF/templates/");
    resolver.setSuffix(".html");
    resolver.setTemplateMode("HTML5");
    if (env.acceptsProfiles("dev")) {
      resolver.setCacheable(false);
    }
    return resolver;
  }

  @Bean
  public SpringTemplateEngine templateEngine() {
    SpringTemplateEngine engine = new SpringTemplateEngine();
    engine.setTemplateResolver(templateResolver());
    engine.addDialect(new ConditionalCommentsDialect());
    engine.addDialect(new SpringSecurityDialect());
    return engine;
  }

  @Bean
  public ViewResolver beanNameViewResolver() {
    SuffixedBeanNameViewResolver resolver = new SuffixedBeanNameViewResolver("-view");
    resolver.setOrder(0);
    return resolver;
  }

  @Bean
  public ViewResolver thymeleafViewResolver() {
    ThymeleafViewResolver resolver = new ThymeleafViewResolver();
    resolver.setOrder(1);
    resolver.setTemplateEngine(templateEngine());
    return resolver;
  }

  @Bean(name = "default-view")
  public View defaultView() {
    return new RedirectView("/", true);
  }

  @Bean(name = "user-list-redirect-view")
  public View userListRedirectView() {
    return new RedirectView("/user", true);
  }

  @Bean(name = "user-profile-redirect-view")
  public View userProfileRedirectView() {
    return new RedirectView("/user/profile", true);
  }

  @Bean(name = "tag-list-redirect-view")
  public View tagListRedirectView() {
    return new RedirectView("/tag", true);
  }
}