package com.randomcoder.article;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;

import com.randomcoder.content.ContentFilter;

/**
 * Abstract controller class which provides support for displaying a single article.
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
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
abstract public class AbstractSingleArticleController extends AbstractController
{

	protected String viewName;
	protected String urlPrefix;
	protected ArticleDao articleDao;
	protected ContentFilter contentFilter;

	/**
	 * Sets the view name to forward to.
	 * @param viewName view name
	 */
	@Required
	public void setViewName(String viewName)
	{ this.viewName = viewName; }

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
	 * Loads the article associated with this request.
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @return Article to display
	 */
	abstract protected Article loadArticle(HttpServletRequest request, HttpServletResponse response);

	/**
	 * Loads the selected article.
	 */
	@Override
	protected final ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Article article = loadArticle(request, response);
		
		if (article == null)
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND, request.getRequestURI());
			return null;
		}
		
		// create model
		ModelAndView mav = new ModelAndView(viewName);
	
		// wrap article list
		List<ArticleDecorator> wrappedArticles = new ArrayList<ArticleDecorator>(1);
		wrappedArticles.add(new ArticleDecorator(article, contentFilter));
	
		// populate model
		mav.addObject("articles", wrappedArticles);
		mav.addObject("pageSubTitle", article.getTitle());
	
		return mav;
	}

	/**
	 * Gets the portion of the URL within the application.
	 * @param request HTTP request
	 * @return application path
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
		return appPath;
	}
}
