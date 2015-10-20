package org.randomcoder.mvc.controller;

import org.randomcoder.article.ArticleDecorator;
import org.randomcoder.article.ArticleNotFoundException;
import org.randomcoder.bo.ArticleBusiness;
import org.randomcoder.bo.TagBusiness;
import org.randomcoder.content.ContentFilter;
import org.randomcoder.content.ContentType;
import org.randomcoder.db.Article;
import org.randomcoder.mvc.command.ArticleAddCommand;
import org.randomcoder.mvc.command.ArticleEditCommand;
import org.randomcoder.mvc.command.CommentCommand;
import org.randomcoder.mvc.editor.EnumPropertyEditor;
import org.randomcoder.mvc.editor.TagListPropertyEditor;
import org.randomcoder.mvc.validator.ArticleAddValidator;
import org.randomcoder.mvc.validator.ArticleEditValidator;
import org.randomcoder.mvc.validator.CommentValidator;
import org.randomcoder.tag.TagList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UrlPathHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 * Controller class for managing articles.
 */
@Controller("articleController")
public class ArticleController
{
	private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

	private ArticleBusiness articleBusiness;
	private TagBusiness tagBusiness;
	private CommentValidator commentValidator;
	private ContentFilter contentFilter;
	private ArticleAddValidator articleAddValidator;
	private ArticleEditValidator articleEditValidator;

	/**
	 * Sets the ArticleBusiness implementation to use.
	 * 
	 * @param articleBusiness
	 *            ArticleBusiness implementation
	 */
	@Inject
	public void setArticleBusiness(ArticleBusiness articleBusiness)
	{
		this.articleBusiness = articleBusiness;
	}

	/**
	 * Sets the TagBusiness implementation to use.
	 * 
	 * @param tagBusiness
	 *            TagBusiness implementation
	 */
	@Inject
	public void setTagBusiness(TagBusiness tagBusiness)
	{
		this.tagBusiness = tagBusiness;
	}

	/**
	 * Sets the comment validator to use.
	 * 
	 * @param commentValidator
	 *            comment validator
	 */
	@Inject
	public void setCommentValidator(CommentValidator commentValidator)
	{
		this.commentValidator = commentValidator;
	}

	/**
	 * Sets the article add validator to use.
	 * 
	 * @param articleAddValidator
	 *            article add validator
	 */
	@Inject
	public void setArticleAddValidator(ArticleAddValidator articleAddValidator)
	{
		this.articleAddValidator = articleAddValidator;
	}

	/**
	 * Sets the article edit validator to use.
	 * 
	 * @param articleEditValidator
	 *            article edit validator
	 */
	@Inject
	public void setArticleEditValidator(ArticleEditValidator articleEditValidator)
	{
		this.articleEditValidator = articleEditValidator;
	}

	/**
	 * Sets the content filter to use.
	 * 
	 * @param contentFilter
	 *            content filter
	 */
	@Inject
	@Named("contentFilter")
	public void setContentFilter(ContentFilter contentFilter)
	{
		this.contentFilter = contentFilter;
	}

	/**
	 * Initializes data bindings.
	 * 
	 * @param binder
	 *            data binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder)
	{
		binder.registerCustomEditor(ContentType.class, new EnumPropertyEditor(ContentType.class));
		binder.registerCustomEditor(TagList.class, new TagListPropertyEditor(tagBusiness));

		Object target = binder.getTarget();
		if (target instanceof CommentCommand)
		{
			binder.setValidator(commentValidator);
		}
		else if (target instanceof ArticleEditCommand)
		{
			binder.setValidator(articleEditValidator);
		}
		else if (target instanceof ArticleAddCommand)
		{
			binder.setValidator(articleAddValidator);
		}
	}

	/**
	 * Displays an article by ID.
	 * 
	 * @param id
	 *            article ID
	 * @param user
	 *            current user
	 * @param model
	 *            MVC model
	 * @return article view
	 */
	@RequestMapping(value = "/articles/id/{id}", method = RequestMethod.GET)
	public String articleById(
			@PathVariable("id") long id,
			Principal user, Model model)
	{
		Article article = articleBusiness.readArticle(id);
		return viewArticle(user, model, article);
	}

	/**
	 * Displays an article by permalink.
	 * 
	 * @param permalink
	 *            article permalink
	 * @param user
	 *            current user
	 * @param model
	 *            MVC model
	 * @return article view
	 */
	@RequestMapping(value = "/articles/{permalink}", method = RequestMethod.GET)
	public String articleByPermalink(
			@PathVariable("permalink") String permalink,
			Principal user, Model model)
	{
		Article article = articleBusiness.findArticleByPermalink(permalink);
		return viewArticle(user, model, article);
	}

	private String viewArticle(Principal user, Model model, Article article)
	{
		if (article == null)
		{
			throw new ArticleNotFoundException();
		}

		CommentCommand command = new CommentCommand();
		command.bind(user == null);

		populateArticleModel(model, article, command);

		return "article-view";
	}

	private void populateArticleModel(Model model, Article article, CommentCommand command)
	{
		List<ArticleDecorator> wrappedArticles = new ArrayList<ArticleDecorator>(1);
		wrappedArticles.add(new ArticleDecorator(article, contentFilter));
		model.addAttribute("articles", wrappedArticles);
		model.addAttribute("pageSubTitle", article.getTitle());
		model.addAttribute("commentsEnabled", article.isCommentsEnabled());
		model.addAttribute("command", command);
		model.addAttribute("contentTypes", ContentType.values());
	}

	/**
	 * Creates a comment on an article.
	 * 
	 * @param id
	 *            article ID
	 * @param user
	 *            current user
	 * @param model
	 *            MVC model
	 * @param request
	 *            HTTP request
	 * @param command
	 *            comment command
	 * @param result
	 *            validation result
	 * @return redirect to article
	 */
	@RequestMapping(value = "/articles/id/{id}", method = RequestMethod.POST)
	public String articleByIdSubmit(
			@PathVariable("id") long id,
			Principal user, Model model, HttpServletRequest request,
			@ModelAttribute("command") CommentCommand command, BindingResult result)
	{
		logger.debug("articleByIdSubmit()");

		Article article = articleBusiness.readArticle(id);
		return commentOnArticle(article, user, model, request, command, result);
	}

	/**
	 * Creates a comment on an article.
	 * 
	 * @param permalink
	 *            article permalink
	 * @param user
	 *            current user
	 * @param model
	 *            MVC model
	 * @param request
	 *            HTTP request
	 * @param command
	 *            comment command
	 * @param result
	 *            validation result
	 * @return redirect to article
	 */
	@RequestMapping(value = "/articles/{permalink}", method = RequestMethod.POST)
	public String articleByPermalinkSubmit(
			@PathVariable("permalink") String permalink,
			Principal user, Model model, HttpServletRequest request,
			@ModelAttribute("command") CommentCommand command, BindingResult result)
	{
		logger.debug("articleByPermalinkSubmit()");

		Article article = articleBusiness.findArticleByPermalink(permalink);
		return commentOnArticle(article, user, model, request, command, result);
	}

	private String commentOnArticle(
			Article article, Principal user,
			Model model, HttpServletRequest request,
			CommentCommand command, BindingResult result)
	{
		if (article == null)
		{
			throw new ArticleNotFoundException();
		}

		populateArticleModel(model, article, command);

		command.bind(user == null);
		commentValidator.validate(command, result);
		if (result.hasErrors())
		{
			return "article-view";
		}

		String userName = user == null ? null : user.getName();

		String referrer = request.getHeader("Referer");
		String ipAddress = request.getRemoteAddr();
		String userAgent = request.getHeader("User-Agent");

		articleBusiness.createComment(command, article.getId(), userName, referrer, ipAddress, userAgent);

		return "redirect:" + getAppPath(request);
	}

	/**
	 * Adds a new article.
	 * 
	 * @param model
	 *            MVC model
	 * @param command
	 *            article add command
	 * @param result
	 *            validation result
	 * @return default view
	 */
	@RequestMapping(value = "/article/add", method = RequestMethod.GET)
	public String addArticle(
			Model model,
			@ModelAttribute("command") ArticleAddCommand command,
			BindingResult result)
	{
		populateArticleEditModel(model);
		return "article-add";
	}

	/**
	 * Cancels adding a new article.
	 * 
	 * @return default view
	 */
	@RequestMapping(value = "/article/add", method = RequestMethod.POST, params = "cancel")
	public String addArticleCancel()
	{
		return "default";
	}

	/**
	 * Submits a new article.
	 * 
	 * @param command
	 *            article add command
	 * @param result
	 *            validation result
	 * @param model
	 *            MVC model
	 * @param user
	 *            current user
	 * 
	 * @return default view
	 */
	@RequestMapping(value = "/article/add", method = RequestMethod.POST, params = "!cancel")
	public String addArticleSubmit(
			@ModelAttribute("command") @Validated ArticleAddCommand command,
			BindingResult result,
			Model model,
			Principal user)
	{
		if (result.hasErrors())
		{
			populateArticleEditModel(model);
			return "article-add";
		}

		articleBusiness.createArticle(command, user.getName());

		return "default";
	}

	/**
	 * Edits an article.
	 * 
	 * @param model
	 *            MVC model
	 * @param command
	 *            article edit command
	 * @param result
	 *            validation result
	 * @param user
	 *            current user
	 * @return default view
	 */
	@RequestMapping(value = "/article/edit", method = RequestMethod.GET)
	public String editArticle(
			Model model,
			@ModelAttribute("command") ArticleEditCommand command,
			BindingResult result,
			Principal user)
	{
		articleBusiness.loadArticleForEditing(command, command.getId(), user.getName());
		
		populateArticleEditModel(model);		
		return "article-edit";
	}

	/**
	 * Cancels editing an article.
	 * 
	 * @return default view
	 */
	@RequestMapping(value = "/article/edit", method = RequestMethod.POST, params = "cancel")
	public String editArticleCancel()
	{
		return "default";
	}
	
	/**
	 * Submits a modified article.
	 * 
	 * @param command
	 *            article edit command
	 * @param result
	 *            validation result
	 * @param model
	 *            MVC model
	 * @param user
	 *            current user
	 * 
	 * @return default view
	 */
	@RequestMapping(value = "/article/edit", method = RequestMethod.POST, params = "!cancel")
	public String editArticleSubmit(
			@ModelAttribute("command") @Validated ArticleEditCommand command,
			BindingResult result,
			Model model,
			Principal user)
	{
		if (result.hasErrors())
		{
			populateArticleEditModel(model);
			return "article-edit";
		}

		articleBusiness.updateArticle(command, command.getId(), user.getName());

		return "default";
	}
	
	private void populateArticleEditModel(Model model)
	{
		model.addAttribute("contentTypes", ContentType.values());
	}

	/**
	 * Deletes the selected article.
	 * 
	 * @param id
	 *            article ID
	 * @param user
	 *            current user
	 * @return default view
	 */
	@RequestMapping("/article/delete")
	public String deleteArticle(@RequestParam("id") long id, Principal user)
	{
		articleBusiness.deleteArticle(user.getName(), id);
		return "default";
	}

	/**
	 * Gets the path of the current request relative to the context path.
	 * 
	 * @param request
	 *            request
	 * @return app path
	 */
	private String getAppPath(HttpServletRequest request)
	{
		UrlPathHelper helper = new UrlPathHelper();

		String appPath = helper.getPathWithinApplication(request);
		try
		{
			appPath = URLDecoder.decode(appPath, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException("Unsupported encoding", e);
		}

		if (logger.isDebugEnabled())
		{
			logger.debug("appPath: " + appPath);
		}

		return appPath;
	}
}
