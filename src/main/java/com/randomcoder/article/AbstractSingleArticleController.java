package com.randomcoder.article;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.*;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UrlPathHelper;

import com.randomcoder.content.ContentFilter;

/**
 * Abstract controller class which provides support for displaying a single article.
 * 
 * <pre>
 * Copyright (c) 2006-2007, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
abstract public class AbstractSingleArticleController extends SimpleFormController
{
	private static final Log logger = LogFactory.getLog(AbstractSingleArticleController.class);

	/**
	 * URL prefix.
	 */
	protected String urlPrefix;
	
	/**
	 * Article DAO.
	 */
	protected ArticleDao articleDao;
	
	/**
	 * Content filter.
	 */
	protected ContentFilter contentFilter;
	
	/**
	 * Article business methods.
	 */
	protected ArticleBusiness articleBusiness;

	/**
	 * Sets the url prefix to remove from the front of the url
	 * @param urlPrefix url prefix
	 */
	@Required
	public void setUrlPrefix(String urlPrefix)
	{ this.urlPrefix = urlPrefix; }

	/**
	 * Sets the ArticleDao implementation to use.
	 * @param articleDao ArticleDao implementation
	 */
	@Required
	public void setArticleDao(ArticleDao articleDao)
	{ this.articleDao = articleDao; }

	/**
	 * Sets the content filter to use.
	 * @param contentFilter content filter
	 */
	@Required
	public void setContentFilter(ContentFilter contentFilter)
	{ this.contentFilter = contentFilter; }

	/**
	 * Sets the ArticleBusiness implementation to use.
	 * @param articleBusiness ArticleBusiness implementation
	 */
	@Required
	public void setArticleBusiness(ArticleBusiness articleBusiness)
	{
		this.articleBusiness = articleBusiness;
	}
	
	/**
	 * Loads the article associated with this request.
	 * 
	 * @param request HTTP request
	 * @return Article to display
	 */
	abstract protected Article loadArticle(HttpServletRequest request);

	/**
	 * Creates a new {@link CommentCommand} bound to the requested article.
	 * @param request HTTP request
	 * @return CommentCommand instance
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception
	{
		logger.debug("formBackingObject(HttpServletRequest)");
		
		CommentCommand command = (CommentCommand) super.formBackingObject(request);
		
		Article article = loadArticle(request);		
		if (article == null) throw new ArticleNotFoundException();
		
		command.bind(article, request.getUserPrincipal() == null);
		
		return command;
	}

	/**
	 * Populates model with required data.
	 * @param request HTTP request
	 * @param command command object
	 * @param errors errors object
	 * @return Map containing data needed for view
	 */
	@Override
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception
	{
		logger.debug("referenceData()");
		
		CommentCommand form = (CommentCommand) command;
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		Article article = form.getArticle();		
		
		// wrap article list
		List<ArticleDecorator> wrappedArticles = new ArrayList<ArticleDecorator>(1);
		wrappedArticles.add(new ArticleDecorator(article, contentFilter));
			
		// populate reference data
		data.put("articles", wrappedArticles);
		data.put("pageSubTitle", article.getTitle());
		
		return data;
	}

	/**
	 * Creates a comment upon form submission.
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @param command command object
	 * @param errors errors object
	 * @return ModelAndView which redirects back to article page
	 */
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception
	{
		logger.debug("onSubmit()");
		
		CommentCommand form = (CommentCommand) command;
		
		Article article = form.getArticle();
		
		Principal principal = request.getUserPrincipal();		
		String userName = null;
		if (principal != null) userName = principal.getName();
		
		articleBusiness.createComment(form, article.getId(), userName);
				
		return new ModelAndView(new RedirectView(getAppPath(request), true));
	}
	
	/**
	 * Gets the path of the current request relative to the context path.
	 * @param request request
	 * @return app path
	 */
	protected final String getAppPath(HttpServletRequest request)
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
			logger.debug("appPath: " + appPath);
		
		return appPath;
	}
}
