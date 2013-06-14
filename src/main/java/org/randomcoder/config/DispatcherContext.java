package org.randomcoder.config;

import java.util.Properties;

import javax.inject.*;

import org.randomcoder.bo.UserBusiness;
import org.randomcoder.mvc.command.*;
import org.randomcoder.user.*;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.*;

@Configuration
@SuppressWarnings("javadoc")
@EnableTransactionManagement(proxyTargetClass = true)
@ComponentScan({ "org.randomcoder.mvc" })
@EnableWebMvc
public class DispatcherContext extends WebMvcConfigurerAdapter
{
	@Inject
	Environment env;

	@Inject
	UserBusiness userBusiness;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
	{
		PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
		pspc.setIgnoreUnresolvablePlaceholders(false);
		return pspc;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry)
	{
		// define some static content that will bypass the dispatcher
		registry
				.addResourceHandler("/**/*.html", "/**/*.css", "/**/*.js", "/**/*.ico", "/**/*.jpg", "/**/*.png", "/**/*.gif")
				.addResourceLocations("classpath:/webapp/");
	}

	@Bean
	@Order(0)
	public RequestMappingHandlerMapping annotationMapping()
	{
		RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();
		mapping.setAlwaysUseFullPath(true);
		mapping.setOrder(0);
		return mapping;
	}

	@Bean
	@Order(1)
	public SimpleUrlHandlerMapping legayMapping()
	{
		Properties p = new Properties();
		p.setProperty("/account/create", "accountCreateController");
		p.setProperty("/user", "userListController");
		p.setProperty("/user/add", "userAddController");
		p.setProperty("/user/edit", "userEditController");
		p.setProperty("/user/profile", "userProfileController");
		p.setProperty("/user/profile/change-password", "changePasswordController");

		SimpleUrlHandlerMapping m = new SimpleUrlHandlerMapping();
		m.setOrder(1);
		m.setAlwaysUseFullPath(true);
		m.setMappings(p);
		return m;
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

	@Bean
	@SuppressWarnings("deprecation")
	public UserProfileController userProfileController(
			@Named("userProfileValidator") final Validator userProfileValidator)
	{
		UserProfileController c = new UserProfileController();
		c.setUserBusiness(userBusiness);
		c.setCommandClass(UserProfileCommand.class);
		c.setFormView("user-profile");
		c.setSuccessView("user-profile-redirect");
		c.setCancelView("default");
		c.setCancelParamKey("cancel");
		c.setBindOnNewForm(true);
		c.setValidator(userProfileValidator);
		return c;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public ChangePasswordController changePasswordController(
			@Named("changePasswordValidator") Validator changePasswordValidator)
	{
		ChangePasswordController c = new ChangePasswordController();
		c.setFormView("change-password");
		c.setSuccessView("user-profile-redirect");
		c.setCancelView("user-profile-redirect");
		c.setCancelParamKey("cancel");
		c.setBindOnNewForm(true);
		c.setCommandClass(ChangePasswordCommand.class);
		c.setValidator(changePasswordValidator);
		c.setUserBusiness(userBusiness);
		return c;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public UserListController userListController()
	{
		UserListController c = new UserListController();
		c.setCommandClass(UserListCommand.class);
		c.setViewName("user-list");
		c.setUserBusiness(userBusiness);
		return c;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public AccountCreateController accountCreateController(
			@Named("accountCreateValidator") final Validator accountCreateValidator)
	{
		AccountCreateController c = new AccountCreateController();
		c.setSuccessView("account-create-done");
		c.setCancelView("default");
		c.setCancelParamKey("cancel");
		c.setBindOnNewForm(true);
		c.setUserBusiness(userBusiness);
		c.setFormView("account-create");
		c.setCommandClass(AccountCreateCommand.class);
		c.setValidator(accountCreateValidator);
		return c;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public UserAddController userAddController(
			@Named("userAddValidator") final Validator userAddValidator)
	{
		UserAddController c = new UserAddController();
		configure(c);
		c.setFormView("user-add");
		c.setCommandClass(UserAddCommand.class);
		c.setValidator(userAddValidator);
		return c;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public UserEditController userEditController(
			@Named("userEditValidator") final Validator userEditValidator)
	{
		UserEditController c = new UserEditController();
		configure(c);
		c.setFormView("user-edit");
		c.setCommandClass(UserEditCommand.class);
		c.setValidator(userEditValidator);
		return c;
	}

	@SuppressWarnings("deprecation")
	private void configure(AbstractUserController c)
	{
		c.setSuccessView("user-list-redirect");
		c.setCancelView("user-list-redirect");
		c.setCancelParamKey("cancel");
		c.setBindOnNewForm(true);
		c.setUserBusiness(userBusiness);
	}
}