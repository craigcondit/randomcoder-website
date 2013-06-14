package org.randomcoder.mvc.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.*;

import javax.inject.*;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.*;
import org.randomcoder.article.*;
import org.randomcoder.bo.*;
import org.randomcoder.content.*;
import org.randomcoder.db.Article;
import org.randomcoder.mvc.command.CommentCommand;
import org.randomcoder.mvc.editor.*;
import org.randomcoder.mvc.validator.CommentValidator;
import org.randomcoder.tag.TagList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UrlPathHelper;

/**
 * Controller class for managing articles.
 */
@Controller("articleController")
public class ArticleController
{
	private static final Log logger = LogFactory.getLog(ArticleController.class);

	private ArticleBusiness articleBusiness;
	private TagBusiness tagBusiness;
	private CommentValidator commentValidator;
	private ContentFilter contentFilter;

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
	 * @param cmd
	 *            comment command
	 * @param result
	 *            validation result
	 * @return redirect to article
	 */
	@RequestMapping(value = "/articles/id/{id}", method = RequestMethod.POST)
	public String articleByIdSubmit(
			@PathVariable("id") long id,
			Principal user, Model model, HttpServletRequest request,
			@ModelAttribute("command") CommentCommand cmd, BindingResult result)
	{
		logger.debug("articleByIdSubmit()");

		Article article = articleBusiness.readArticle(id);
		return commentOnArticle(article, user, model, request, cmd, result);
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
	 * @param cmd
	 *            comment command
	 * @param result
	 *            validation result
	 * @return redirect to article
	 */
	@RequestMapping(value = "/articles/{permalink}", method = RequestMethod.POST)
	public String articleByPermalinkSubmit(
			@PathVariable("permalink") String permalink,
			Principal user, Model model, HttpServletRequest request,
			@ModelAttribute("command") CommentCommand cmd, BindingResult result)
	{
		logger.debug("articleByPermalinkSubmit()");

		Article article = articleBusiness.findArticleByPermalink(permalink);
		return commentOnArticle(article, user, model, request, cmd, result);
	}

	private String commentOnArticle(
			Article article, Principal user,
			Model model, HttpServletRequest request,
			CommentCommand cmd, BindingResult result)
	{
		if (article == null)
		{
			throw new ArticleNotFoundException();
		}

		populateArticleModel(model, article, cmd);

		cmd.bind(user == null);
		commentValidator.validate(cmd, result);
		if (result.hasErrors())
		{
			return "article-view";
		}

		String userName = user == null ? null : user.getName();

		String referrer = request.getHeader("Referer");
		String ipAddress = request.getRemoteAddr();
		String userAgent = request.getHeader("User-Agent");

		articleBusiness.createComment(cmd, article.getId(), userName, referrer, ipAddress, userAgent);

		return "redirect:" + getAppPath(request);
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
