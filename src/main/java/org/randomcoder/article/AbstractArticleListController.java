package org.randomcoder.article;

import java.util.*;

import javax.servlet.http.*;

import org.acegisecurity.ui.AbstractProcessingFilter;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import org.randomcoder.content.ContentFilter;
import org.randomcoder.tag.*;

/**
 * Abstract base class for controllers which generate article lists.
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
 * 
 * @param <PageCommand> command type
 */
abstract public class AbstractArticleListController<PageCommand extends ArticlePageCommand> extends AbstractCommandController
{
	/**
	 * Article DAO.
	 */
	protected ArticleDao articleDao;
	
	/**
	 * Tag Business.
	 */
	protected TagBusiness tagBusiness;
	
	/**
	 * Content Filter.
	 */
	protected ContentFilter contentFilter;
	
	/**
	 * View name.
	 */
	protected String viewName;
	
	/**
	 * Default page size.
	 */
	protected int defaultPageSize = 10;
	
	/**
	 * Maximum page size.
	 */
	protected int maximumPageSize = 50;

	/**
	 * Sets the ArticleDao implementation to use.
	 * @param articleDao ArticleDao implementation
	 */
	@Required
	public void setArticleDao(ArticleDao articleDao)
	{
		this.articleDao = articleDao;
	}

	/**
	 * Sets the TagBusiness implementation to use.
	 * @param tagBusiness TagBusiness implementation
	 */
	public void setTagBusiness(TagBusiness tagBusiness)
	{
		this.tagBusiness = tagBusiness;
	}
	
	/**
	 * Sets the default number of items to display per page (defaults to 10).
	 * @param defaultPageSize default number of items per page
	 */
	public void setDefaultPageSize(int defaultPageSize)
	{
		this.defaultPageSize = defaultPageSize;
	}

	/**
	 * Sets the maximum number of items to allow per page (defaults to 50).
	 * @param maximumPageSize maximum number of items per page
	 */
	public void setMaximumPageSize(int maximumPageSize)
	{
		this.maximumPageSize = maximumPageSize;
	}

	/**
	 * Sets the content filter to use for decorating articles.
	 * @param contentFilter ContentFilter implementation
	 */
	@Required
	public void setContentFilter(ContentFilter contentFilter)
	{
		this.contentFilter = contentFilter;
	}

	/**
	 * Sets the name of the view to forward to once processing is complete.
	 * @param viewName view name
	 */
	@Required
	public void setViewName(String viewName)
	{
		this.viewName = viewName;
	}

	/**
	 * Lists articles between start and end dates.
	 * @param command page command
	 * @param startDate start date
	 * @param endDate end date
	 * @return list of Articles
	 */
	abstract protected List<Article> listArticlesBetweenDates(PageCommand command, Date startDate, Date endDate);
	
	/**
	 * Lists articles before a given cutoff date.
	 * @param command page command
	 * @param cutoffDate cutoff date
	 * @param start starting result
	 * @param limit limit on number of results
	 * @return list of Articles
	 */
	abstract protected List<Article> listArticlesBeforeDateInRange(PageCommand command, Date cutoffDate, int start, int limit);
	
	/**
	 * Counts articles before a given cutoff date.
	 * @param command page command
	 * @param cutoffDate cutoff date
	 * @return number of articles which match
	 */
	abstract protected int countArticlesBeforeDate(PageCommand command, Date cutoffDate);
	
	/**
	 * Gets the subtitle to add to the page.
	 * 
	 * @param command page command
	 * @return subtitle or null if none 
	 */
	abstract protected String getSubTitle(PageCommand command);
	
	/**
	 * Populates the model.
	 */
	@Override
	protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
	{
		ArticlePageCommand pager = (ArticlePageCommand) command;
		PageCommand pageCommand = getPageCommand(command);
	
		// removed any saved request in case login was canceled
		HttpSession session = request.getSession(false);
		if (session != null) session.removeAttribute(AbstractProcessingFilter.ACEGI_SAVED_REQUEST_KEY);
		
		// get current month
		Calendar currentMonth = Calendar.getInstance();
		currentMonth.setTime(new Date());
		if (pager.getYear() > 0 && pager.getMonth() > 0)
		{
			currentMonth.set(Calendar.YEAR, pager.getYear());
			currentMonth.set(Calendar.MONTH, pager.getMonth() - 1);
		}
		currentMonth.set(Calendar.DAY_OF_MONTH, 1);
		currentMonth.set(Calendar.HOUR_OF_DAY, 0);
		currentMonth.set(Calendar.MINUTE, 0);
		currentMonth.set(Calendar.SECOND, 0);
		currentMonth.set(Calendar.MILLISECOND, 0);
	
		// get next month
		Calendar nextMonth = Calendar.getInstance();
		nextMonth.setTime(currentMonth.getTime());
		nextMonth.add(Calendar.MONTH, 1);
		nextMonth.set(Calendar.DAY_OF_MONTH, 1);
		nextMonth.set(Calendar.HOUR_OF_DAY, 0);
		nextMonth.set(Calendar.MINUTE, 0);
		nextMonth.set(Calendar.SECOND, 0);
		nextMonth.set(Calendar.MILLISECOND, 0);
	
		// mark calendar with days containing articles
		boolean[] days = new boolean[31];
		for (int i = 0; i < 31; i++)
			days[i] = false;
	
		Calendar cal = Calendar.getInstance();
		for (Article article : listArticlesBetweenDates(pageCommand, currentMonth.getTime(), nextMonth.getTime()))
		{
			cal.setTime(article.getCreationDate());
			days[cal.get(Calendar.DAY_OF_MONTH) - 1] = true;
		}
	
		Calendar cutoff = Calendar.getInstance();
		cutoff.setTime(currentMonth.getTime());
	
		if (pager.getYear() > 0 && pager.getMonth() > 0 && pager.getDay() > 0)
		{
			cutoff.set(Calendar.DAY_OF_MONTH, pager.getDay());
			cutoff.add(Calendar.DAY_OF_MONTH, 1);
		}
		else
		{
			cutoff.add(Calendar.MONTH, 1);
		}
		cutoff.set(Calendar.HOUR_OF_DAY, 0);
		cutoff.set(Calendar.MINUTE, 0);
		cutoff.set(Calendar.SECOND, 0);
		cutoff.set(Calendar.MILLISECOND, 0);
	
		// set range
		int start = pager.getStart();
		if (start < 0)
			start = 0;
		int limit = pager.getLimit();
		if (limit <= 0)
			limit = defaultPageSize;
		if (limit > maximumPageSize)
			limit = maximumPageSize;
			
		// load articles
		List<Article> articles = listArticlesBeforeDateInRange(pageCommand, cutoff.getTime(), start, limit);

		// count articles for pager
		int pageCount = countArticlesBeforeDate(pageCommand, cutoff.getTime());
	
		// create model
		ModelAndView mav = new ModelAndView(viewName);
	
		// wrap article list
		List<ArticleDecorator> wrappedArticles = new ArrayList<ArticleDecorator>(articles.size());
		for (Article article : articles)
			wrappedArticles.add(new ArticleDecorator(article, contentFilter));
	
		// get tag cloud
		List<TagCloudEntry> tagCloud = tagBusiness.getTagCloud();
		
		// populate model
		mav.addObject("articles", wrappedArticles);
		mav.addObject("days", days);
		mav.addObject("pageCount", pageCount);
		mav.addObject("pageStart", start);
		mav.addObject("pageLimit", limit);
		mav.addObject("tagCloud", tagCloud);
	
		String subTitle = getSubTitle(pageCommand);
		if (subTitle != null) mav.addObject("pageSubTitle", subTitle);
		
		// return
		return mav;
	}

	@SuppressWarnings("unchecked")
	private PageCommand getPageCommand(Object command)
	{
		return (PageCommand) command;
	}
}
