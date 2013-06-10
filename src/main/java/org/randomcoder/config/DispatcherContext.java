package org.randomcoder.config;

import java.net.URL;
import java.util.Properties;

import javax.inject.Inject;

import org.randomcoder.article.*;
import org.randomcoder.article.comment.*;
import org.randomcoder.content.ContentFilter;
import org.randomcoder.download.*;
import org.randomcoder.feed.*;
import org.randomcoder.security.*;
import org.randomcoder.springmvc.IdCommand;
import org.randomcoder.tag.*;
import org.randomcoder.user.*;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.*;

@Configuration
@SuppressWarnings("javadoc")
@EnableTransactionManagement
public class DispatcherContext extends WebMvcConfigurationSupport
{
	@Inject
	Environment env;

	@Inject
	PackageListProducer packageListProducer;

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

	@Inject
	AtomFeedGenerator atomFeedGenerator;

	@Inject
	Rss20FeedGenerator rss20FeedGenerator;

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
	public ParameterizableViewController aboutController()
	{
		ParameterizableViewController c = new ParameterizableViewController();
		c.setViewName("legal-about");
		return c;
	}

	@Bean
	public ParameterizableViewController licenseController()
	{
		ParameterizableViewController c = new ParameterizableViewController();
		c.setViewName("legal-license");
		return c;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public RedirectController redirectController()
	{
		RedirectController c = new RedirectController();
		c.setCommandClass(RedirectCommand.class);
		return c;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public DownloadController downloadController()
	{
		DownloadController c = new DownloadController();
		c.setViewName("download");
		c.setSupportedMethods(new String[] { "GET" });
		c.setPackageListProducer(packageListProducer);
		c.setCommandClass(DownloadCommand.class);
		return c;
	}

	@Bean
	public ArticleAddValidator articleAddValidator()
	{
		ArticleAddValidator validator = new ArticleAddValidator();
		validator.setArticleDao(articleDao);
		validator.setContentFilter(contentFilter);
		validator.setMaximumSummaryLength(1000);
		return validator;
	}

	@Bean
	public ArticleEditValidator articleEditValidator()
	{
		ArticleEditValidator validator = new ArticleEditValidator();
		validator.setArticleDao(articleDao);
		validator.setContentFilter(contentFilter);
		validator.setMaximumSummaryLength(1000);
		return validator;
	}

	@Bean
	public CommentValidator commentValidator()
	{
		CommentValidator validator = new CommentValidator();
		validator.setContentFilter(contentFilter);
		return validator;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public HomeController homeController()
	{
		HomeController c = new HomeController();
		configure(c);
		c.setViewName("home");
		c.setCommandClass(ArticlePageCommand.class);
		return c;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public ArticleTagListController articleTagListController()
	{
		ArticleTagListController c = new ArticleTagListController();
		configure(c);
		c.setViewName("article-tag-list");
		c.setCommandClass(ArticleTagPageCommand.class);
		c.setTagDao(tagDao);
		c.setUrlPrefix("/tags/");
		return c;
	}

	private void configure(AbstractArticleListController c)
	{
		c.setDefaultPageSize(10);
		c.setMaximumPageSize(50);
		c.setContentFilter(contentFilter);
		c.setTagBusiness(tagBusiness);
		c.setArticleDao(articleDao);
	}

	@Bean
	@SuppressWarnings("deprecation")
	public UserProfileController userProfileController()
	{
		UserProfileController c = new UserProfileController();
		c.setUserDao(userDao);
		c.setUserBusiness(userBusiness);
		c.setCommandClass(UserProfileCommand.class);
		c.setFormView("user-profile");
		c.setSuccessView("user-profile-redirect");
		c.setCancelView("default");
		c.setCancelParamKey("cancel");
		c.setBindOnNewForm(true);
		c.setValidator(userProfileValidator());
		return c;
	}

	@Bean
	public UserProfileValidator userProfileValidator()
	{
		return new UserProfileValidator();
	}

	@Bean
	public ArticleIdController articleIdController()
	{
		ArticleIdController c = new ArticleIdController();
		configure(c);
		c.setUrlPrefix("/articles/id/");
		return c;
	}

	@Bean
	public ArticlePermalinkController articlePermalinkController()
	{
		ArticlePermalinkController c = new ArticlePermalinkController();
		configure(c);
		c.setUrlPrefix("/articles/");
		return c;
	}

	@SuppressWarnings("deprecation")
	private void configure(AbstractSingleArticleController c)
	{
		c.setFormView("article-view");
		c.setSuccessView("article-view");
		c.setArticleDao(articleDao);
		c.setArticleBusiness(articleBusiness);
		c.setContentFilter(contentFilter);
		c.setCommandClass(CommentCommand.class);
		c.setValidator(commentValidator());
	}

	@Bean
	public ParameterizableViewController loginController()
	{
		ParameterizableViewController c = new ParameterizableViewController();
		c.setViewName("login");
		return c;
	}

	@Bean
	public ParameterizableViewController loginErrorController()
	{
		ParameterizableViewController c = new ParameterizableViewController();
		c.setViewName("login-error");
		return c;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public ArticleAddController articleAddController()
	{
		ArticleAddController c = new ArticleAddController();
		configure(c);
		c.setFormView("article-add");
		c.setCommandClass(ArticleAddCommand.class);
		c.setValidator(articleAddValidator());
		return c;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public ArticleEditController articleEditController()
	{
		ArticleEditController c = new ArticleEditController();
		configure(c);
		c.setFormView("article-edit");
		c.setCommandClass(ArticleEditCommand.class);
		c.setValidator(articleEditValidator());
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
		c.setTagDao(tagDao);
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
		v.setUserDao(userDao);
		return v;
	}

	@Bean
	public UserEditValidator userEditValidator()
	{
		UserEditValidator v = new UserEditValidator();
		v.setUserDao(userDao);
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
		c.setUserDao(userDao);
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
		c.setUserDao(userDao);
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
		v.setUserDao(userDao);
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
		c.setRoleDao(roleDao);
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
		v.setTagDao(tagDao);
		return v;
	}

	@Bean
	public TagEditValidator tagEditValidator()
	{
		TagEditValidator v = new TagEditValidator();
		v.setTagDao(tagDao);
		return v;
	}

	@Bean
	@SuppressWarnings("deprecation")
	public TagListController tagListController()
	{
		TagListController c = new TagListController();
		c.setCommandClass(TagListCommand.class);
		c.setViewName("tag-list");
		c.setTagDao(tagDao);
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

	@Bean
	public AllFeedController allAtomFeedController() throws Exception
	{
		AllFeedController c = new AllFeedController();
		c.setTitle("randomCoder");
		c.setSubtitle("// TODO build a better web");
		c.setUrlPrefix("/feeds/atom");
		c.setFeedGenerator(atomFeedGenerator);
		c.setFeedId("atom-all");
		c.setFeedUrl(new URL("https://randomcoder.org/feeds/atom/all"));
		c.setAltUrl(new URL("https://randomcoder.org/"));
		c.setLimit(20);
		c.setArticleDao(articleDao);
		return c;
	}

	@Bean
	public AllFeedController allRss20FeedController() throws Exception
	{
		AllFeedController c = new AllFeedController();
		c.setTitle("randomCoder");
		c.setSubtitle("// TODO build a better web");
		c.setUrlPrefix("/feeds/rss20");
		c.setFeedGenerator(rss20FeedGenerator);
		c.setFeedId("rss20-all");
		c.setFeedUrl(new URL("https://randomcoder.org/feeds/rss20/all"));
		c.setAltUrl(new URL("https://randomcoder.org/"));
		c.setLimit(20);
		c.setArticleDao(articleDao);
		return c;
	}

	@Bean
	public SimpleUrlHandlerMapping handlerMapping()
	{
		Properties p = new Properties();
		p.setProperty("/account/create", "accountCreateController");
		p.setProperty("/article/add", "articleAddController");
		p.setProperty("/article/edit", "articleEditController");
		p.setProperty("/article/delete", "articleDeleteController");
		p.setProperty("/articles/id/*", "articleIdController");
		p.setProperty("/articles/*", "articlePermalinkController");
		p.setProperty("/feeds/atom/all", "allAtomFeedController");
		p.setProperty("/feeds/rss20/all", "allRss20FeedController");
		p.setProperty("/comment/delete", "commentDeleteController");
		p.setProperty("/comment/approve", "commentApproveController");
		p.setProperty("/comment/disapprove", "commentDisapproveController");
		p.setProperty("/download", "downloadController");
		p.setProperty("", "homeController");
		p.setProperty("/legal/about", "aboutController");
		p.setProperty("/legal/license", "licenseController");
		p.setProperty("/login", "loginController");
		p.setProperty("/login-error", "loginErrorController");
		p.setProperty("/redirect", "redirectController");
		p.setProperty("/tag", "tagListController");
		p.setProperty("/tag/add", "tagAddController");
		p.setProperty("/tag/edit", "tagEditController");
		p.setProperty("/tag/delete", "tagDeleteController");
		p.setProperty("/tags/*", "articleTagListController");
		p.setProperty("/user", "userListController");
		p.setProperty("/user/add", "userAddController");
		p.setProperty("/user/edit", "userEditController");
		p.setProperty("/user/delete", "userDeleteController");
		p.setProperty("/user/profile", "userProfileController");
		p.setProperty("/user/profile/change-password", "changePasswordController");

		SimpleUrlHandlerMapping m = new SimpleUrlHandlerMapping();
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
		resolver.setViewClass(JstlView.class);
		resolver.setPrefix("/WEB-INF/jsp/");
		resolver.setSuffix(".jsp");
		return resolver;
	}
}