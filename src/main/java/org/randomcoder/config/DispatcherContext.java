package org.randomcoder.config;

import jakarta.inject.Inject;
import org.randomcoder.mvc.SuffixedBeanNameViewResolver;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UrlPathHelper;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.List;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@ComponentScan({"org.randomcoder.mvc"})
@EnableWebMvc
public class DispatcherContext
        implements WebMvcConfigurer, ApplicationContextAware {

    @Inject
    Environment env;

    ApplicationContext applicationContext;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer pspc =
                new PropertySourcesPlaceholderConfigurer();
        pspc.setIgnoreUnresolvablePlaceholders(false);
        return pspc;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void configureDefaultServletHandling(
            DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addArgumentResolvers(
            List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver resolver =
                new PageableHandlerMethodArgumentResolver();
        resolver.setPageParameterName("page");
        resolver.setSizeParameterName("size");
        resolver.setPrefix("page.");
        argumentResolvers.add(resolver);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // define some static content that will bypass the dispatcher
        registry
                .addResourceHandler("/**")
                .addResourceLocations("classpath:/webapp/");
    }

    @Bean
    public RequestMappingHandlerMapping handlerMapping() {
        RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();
        UrlPathHelper uph = new UrlPathHelper();
        uph.setAlwaysUseFullPath(true);
        mapping.setUrlPathHelper(uph);
        return mapping;
    }

    @Bean
    public ITemplateResolver templateResolver() {
        SpringResourceTemplateResolver resolver =
                new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        if (env.acceptsProfiles(Profiles.of("dev"))) {
            resolver.setCacheable(false);
        }
        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());
        engine.setEnableSpringELCompiler(true);
        engine.addDialect(new SpringSecurityDialect());
        return engine;
    }

    @Bean
    public ViewResolver beanNameViewResolver() {
        SuffixedBeanNameViewResolver resolver =
                new SuffixedBeanNameViewResolver("-view");
        resolver.setOrder(0);
        return resolver;
    }

    @Bean
    public ViewResolver thymeleafViewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setOrder(1);
        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding("UTF-8");
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
