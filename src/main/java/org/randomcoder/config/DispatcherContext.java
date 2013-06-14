package org.randomcoder.config;

import java.util.Properties;

import javax.inject.*;

import org.randomcoder.article.*;
import org.randomcoder.article.comment.*;
import org.randomcoder.bo.*;
import org.randomcoder.content.ContentFilter;
import org.randomcoder.db.*;
import org.randomcoder.springmvc.IdCommand;
import org.randomcoder.tag.*;
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
@ComponentScan({ "org.randomcoder.controller", "org.randomcoder.validator" })
@EnableWebMvc
public class DispatcherContext extends WebMvcConfigurerAdapter
{
	@Inject
	Environment env;

	@Inject
	ArticleDao articleDao;

	@Inject
	TagDao tagDao;

	@Inject
	UserDao userDao;

	@Inject
	RoleDao roleDao;

	@Inject
	ContentFilter contentFilter;

	@Inject
	ArticleBusiness articleBusiness;

	@Inject
	TagBusiness tagBusiness;

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
		p.setProperty("/article/add", "articleAddController");
		p.setProperty("/article/edit", "articleEditController");
		p.setProperty("/article/delete", "articleDeleteController");
		p.setProperty("/articles/id/*", "articleIdController");
		p.setProperty("/articles/*", "articlePermalinkController");
		p.setProperty("/comment/delete", "commentDeleteController");
		p.setProperty("/comment/approve", "commentApproveController");
		p.setProperty("/comment/disapprove", "commentDisapproveController");
		p.setProperty("/tag", "tagListController");
		p.setProperty("/tag/add", "tagAddController");
		p.setProperty("/tag/edit", "tagEditController");
		p.setProperty("/tag/delete", "tagDeleteController");
		p.setProperty("/user", "userListController");
		p.setProperty("/user/add", "userAddController");
		p.setProperty("/user/edit", "userEditController");
		p.setProperty("/user/delete", "userDeleteController");
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
	public ArticleIdController articleIdController(
			@Named("commentValidator") final Validator commentValidator)
	{
		ArticleIdController c = new ArticleIdController();
		configure(c, commentValidator);
		c.setUrlPrefix("/articles/id/");
		return c;
	}

	@Bean
	public ArticlePermalinkController articlePermalinkController(
			@Named("commentValidator") final Validator commentValidator)
	{
		ArticlePermalinkController c = new ArticlePermalinkController();
		configure(c, commentValidator);
		c.setUrlPrefix("/articles/");
		return c;
	}

	@SuppressWarnings("deprecation")
	private void configure(AbstractSingleArticleController c, Validator commentValidator)
	{
		c.setFormView("article-view");
		c.setSuccessView("article-view");
		c.setArticleBusiness(articleBusiness);
		c.setContentFilter(contentFilter);
		c.setCommandClass(CommentCommand.class);
		c.setValidator(commentValidator);
	}

	@Bean
	@SuppressWarnings("deprecation")
	public ArticleAddController articleAddController(
			@Named("articleAddValidator") final Validator articleAddValidator)
	{
		ArticleAddController c = new ArticleAddController();
		configure(c);
		c.setFormView("article-add");
		c.setCommandClass(ArticleAddCommand.class);
		c.setValidator(articleAddValidator);
		return c;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public ArticleEditController articleEditController(
			@Named("articleEditValidator") final Validator articleEditValidator)
	{
		ArticleEditController c = new ArticleEditController();
		configure(c);
		c.setFormView("article-edit");
		c.setCommandClass(ArticleEditCommand.class);
		c.setValidator(articleEditValidator);
		return c;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public ArticleDeleteController articleDeleteController()
	{
		ArticleDeleteController c = new ArticleDeleteController();
		c.setViewName("default");
		c.setCommandClass(IdCommand.class);
		c.setArticleBusiness(articleBusiness);
		return c;
	}

	@SuppressWarnings("deprecation")
	private void configure(AbstractArticleController c)
	{
		c.setSuccessView("default");
		c.setCancelView("default");
		c.setCancelParamKey("cancel");
		c.setBindOnNewForm(true);
		c.setArticleBusiness(articleBusiness);
		c.setTagBusiness(tagBusiness);
	}

	@Bean
	@SuppressWarnings("deprecation")
	public CommentDeleteController commentDeleteController()
	{
		CommentDeleteController c = new CommentDeleteController();
		c.setCommandClass(IdCommand.class);
		c.setArticleBusiness(articleBusiness);
		return c;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public CommentApproveController commentApproveController()
	{
		CommentApproveController c = new CommentApproveController();
		c.setCommandClass(IdCommand.class);
		c.setArticleBusiness(articleBusiness);
		return c;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public CommentDisapproveController commentDisapproveController()
	{
		CommentDisapproveController c = new CommentDisapproveController();
		c.setCommandClass(IdCommand.class);
		c.setArticleBusiness(articleBusiness);
		return c;
	}

	@Bean
	public UserAddValidator userAddValidator()
	{
		UserAddValidator v = new UserAddValidator();
		v.setUserBusiness(userBusiness);
		return v;
	}

	@Bean
	public UserEditValidator userEditValidator()
	{
		UserEditValidator v = new UserEditValidator();
		v.setUserBusiness(userBusiness);
		return v;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public ChangePasswordController changePasswordController()
	{
		ChangePasswordController c = new ChangePasswordController();
		c.setFormView("change-password");
		c.setSuccessView("user-profile-redirect");
		c.setCancelView("user-profile-redirect");
		c.setCancelParamKey("cancel");
		c.setBindOnNewForm(true);
		c.setCommandClass(ChangePasswordCommand.class);
		c.setValidator(changePasswordValidator());
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
	public ChangePasswordValidator changePasswordValidator()
	{
		ChangePasswordValidator v = new ChangePasswordValidator();
		v.setMinimumPasswordLength(6);
		return v;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public AccountCreateController accountCreateController()
	{
		AccountCreateController c = new AccountCreateController();
		c.setSuccessView("account-create-done");
		c.setCancelView("default");
		c.setCancelParamKey("cancel");
		c.setBindOnNewForm(true);
		c.setUserBusiness(userBusiness);
		c.setFormView("account-create");
		c.setCommandClass(AccountCreateCommand.class);
		c.setValidator(accountCreateValidator());
		return c;
	}

	@Bean
	public AccountCreateValidator accountCreateValidator()
	{
		AccountCreateValidator v = new AccountCreateValidator();
		v.setUserBusiness(userBusiness);
		return v;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public UserAddController userAddController()
	{
		UserAddController c = new UserAddController();
		configure(c);
		c.setFormView("user-add");
		c.setCommandClass(UserAddCommand.class);
		c.setValidator(userAddValidator());
		return c;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public UserEditController userEditController()
	{
		UserEditController c = new UserEditController();
		configure(c);
		c.setFormView("user-edit");
		c.setCommandClass(UserEditCommand.class);
		c.setValidator(userEditValidator());
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

	@Bean
	@SuppressWarnings("deprecation")
	public UserDeleteController userDeleteController()
	{
		UserDeleteController c = new UserDeleteController();
		c.setViewName("user-list-redirect");
		c.setUserBusiness(userBusiness);
		c.setCommandClass(IdCommand.class);
		c.setUserBusiness(userBusiness);
		return c;
	}

	@Bean
	public TagAddValidator tagAddValidator()
	{
		TagAddValidator v = new TagAddValidator();
		v.setTagBusiness(tagBusiness);
		return v;
	}

	@Bean
	public TagEditValidator tagEditValidator()
	{
		TagEditValidator v = new TagEditValidator();
		v.setTagBusiness(tagBusiness);
		return v;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public TagListController tagListController()
	{
		TagListController c = new TagListController();
		c.setCommandClass(TagListCommand.class);
		c.setViewName("tag-list");
		c.setTagBusiness(tagBusiness);
		return c;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public TagAddController tagAddController()
	{
		TagAddController c = new TagAddController();
		configure(c);
		c.setFormView("tag-add");
		c.setCommandClass(TagAddCommand.class);
		c.setValidator(tagAddValidator());
		return c;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public TagEditController tagEditController()
	{
		TagEditController c = new TagEditController();
		configure(c);
		c.setFormView("tag-edit");
		c.setCommandClass(TagEditCommand.class);
		c.setValidator(tagEditValidator());
		return c;
	}

	@SuppressWarnings("deprecation")
	private void configure(AbstractTagController c)
	{
		c.setSuccessView("tag-list-redirect");
		c.setCancelView("tag-list-redirect");
		c.setCancelParamKey("cancel");
		c.setBindOnNewForm(true);
		c.setTagBusiness(tagBusiness);
	}

	@Bean
	@SuppressWarnings("deprecation")
	public TagDeleteController tagDeleteController()
	{
		TagDeleteController c = new TagDeleteController();
		c.setViewName("tag-list-redirect");
		c.setCommandClass(IdCommand.class);
		c.setTagBusiness(tagBusiness);
		return c;
	}
}