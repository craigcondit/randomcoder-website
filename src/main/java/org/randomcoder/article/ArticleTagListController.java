package org.randomcoder.article;

import java.net.URLDecoder;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.randomcoder.db.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.util.UrlPathHelper;


/**
 * Controller class which handles displaying articles by tag.
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
public class ArticleTagListController extends AbstractArticleListController<ArticleTagPageCommand>
{
	private static final Log logger = LogFactory.getLog(ArticleTagListController.class);
	
	private String urlPrefix;
	
	/**
	 * Sets the URL prefix to remove from path.
	 * @param urlPrefix url prefix
	 */
	@Required
	public void setUrlPrefix(String urlPrefix) { this.urlPrefix = urlPrefix; }
	
	@Override
	protected List<Article> listArticlesBetweenDates(ArticleTagPageCommand command, Date startDate, Date endDate)
	{
		return articleBusiness.listArticlesByTagBetweenDates(command.getTag(), startDate, endDate);
	}

	@Override
	protected List<Article> listArticlesBeforeDateInRange(ArticleTagPageCommand command, Date cutoffDate, int start, int limit)	
	{
		return articleBusiness.listArticlesByTagBeforeDateInRange(command.getTag(), cutoffDate, start, limit);
	}

	@Override
	protected int countArticlesBeforeDate(ArticleTagPageCommand command, Date cutoffDate)
	{
		return articleBusiness.countArticlesByTagBeforeDate(command.getTag(), cutoffDate);
	}
	
	@Override
	protected String getSubTitle(ArticleTagPageCommand command)
	{
		Tag tag = command.getTag();
		return tag == null ? null : tag.getDisplayName();
	}

	/**
	 * Binds additional parameters to command
	 */
	@Override
	protected void onBind(HttpServletRequest request, Object command) throws Exception
	{
		super.onBind(request, command);
		
		ArticleTagPageCommand pager = (ArticleTagPageCommand) command;
		
		UrlPathHelper helper = new UrlPathHelper();
		
		String appPath = helper.getPathWithinApplication(request);
		
		logger.debug("App path: " + appPath);
		
		appPath = URLDecoder.decode(appPath, "UTF-8");
		
		//appPath = helper.decodeRequestString(request, appPath);
		
		logger.debug("App path (after decode): " + appPath);
		
		if (appPath.startsWith(urlPrefix))
		{
			appPath = appPath.substring(urlPrefix.length());
			String[] params = appPath.split("/");
			
			if (params.length > 0)
			{
				String tagName = StringUtils.trimToEmpty(params[0]).toLowerCase(Locale.US);
				
				logger.debug("Tag name: " + tagName);
				
				pager.setTag(tagBusiness.findTagByName(tagName));
			}
		}		
	}
}