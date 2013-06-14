package org.randomcoder.mvc.controller;

import java.util.*;

import javax.inject.Inject;

import org.randomcoder.article.ArticleDecorator;
import org.randomcoder.bo.*;
import org.randomcoder.content.ContentFilter;
import org.randomcoder.db.Article;
import org.randomcoder.mvc.command.ArticlePageCommand;
import org.randomcoder.tag.TagCloudEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;

/**
 * Abstract base class for controllers which generate article lists.
 * 
 * @param <T>
 *            command type
 */
abstract public class AbstractArticleListController<T extends ArticlePageCommand>
{
	/**
	 * Article Business.
	 */
	protected ArticleBusiness articleBusiness;

	/**
	 * Tag Business.
	 */
	protected TagBusiness tagBusiness;

	/**
	 * Content Filter.
	 */
	protected ContentFilter contentFilter;

	/**
	 * Default page size.
	 */
	protected int defaultPageSize = 10;

	/**
	 * Maximum page size.
	 */
	protected int maximumPageSize = 50;

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
	 * Sets the default number of items to display per page (defaults to 10).
	 * 
	 * @param defaultPageSize
	 *            default number of items per page
	 */
	@Value("${article.pagesize.default}")
	public void setDefaultPageSize(int defaultPageSize)
	{
		this.defaultPageSize = defaultPageSize;
	}

	/**
	 * Sets the maximum number of items to allow per page (defaults to 50).
	 * 
	 * @param maximumPageSize
	 *            maximum number of items per page
	 */
	@Value("${article.pagesize.max}")
	public void setMaximumPageSize(int maximumPageSize)
	{
		this.maximumPageSize = maximumPageSize;
	}

	/**
	 * Sets the content filter to use for decorating articles.
	 * 
	 * @param contentFilter
	 *            ContentFilter implementation
	 */
	@Inject
	public void setContentFilter(ContentFilter contentFilter)
	{
		this.contentFilter = contentFilter;
	}

	/**
	 * Lists articles between start and end dates.
	 * 
	 * @param command
	 *            page command
	 * @param startDate
	 *            start date
	 * @param endDate
	 *            end date
	 * @return list of Articles
	 */
	abstract protected List<Article> listArticlesBetweenDates(T command, Date startDate, Date endDate);

	/**
	 * Lists articles before a given cutoff date.
	 * 
	 * @param command
	 *            page command
	 * @param cutoffDate
	 *            cutoff date
	 * @param start
	 *            starting result
	 * @param limit
	 *            limit on number of results
	 * @return list of Articles
	 */
	abstract protected List<Article> listArticlesBeforeDateInRange(T command, Date cutoffDate, int start, int limit);

	/**
	 * Counts articles before a given cutoff date.
	 * 
	 * @param command
	 *            page command
	 * @param cutoffDate
	 *            cutoff date
	 * @return number of articles which match
	 */
	abstract protected int countArticlesBeforeDate(T command, Date cutoffDate);

	/**
	 * Gets the subtitle to add to the page.
	 * 
	 * @param command
	 *            page command
	 * @return subtitle or null if none
	 */
	abstract protected String getSubTitle(T command);

	/**
	 * Populates the model.
	 * 
	 * @param command
	 *            page command
	 * @param model
	 *            model
	 */
	protected final void populateModel(T command, Model model)
	{
		// get current month
		Calendar currentMonth = Calendar.getInstance();
		currentMonth.setTime(new Date());
		if (command.getYear() > 0 && command.getMonth() > 0)
		{
			currentMonth.set(Calendar.YEAR, command.getYear());
			currentMonth.set(Calendar.MONTH, command.getMonth() - 1);
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
		{
			days[i] = false;
		}

		Calendar cal = Calendar.getInstance();
		for (Article article : listArticlesBetweenDates(command, currentMonth.getTime(), nextMonth.getTime()))
		{
			cal.setTime(article.getCreationDate());
			days[cal.get(Calendar.DAY_OF_MONTH) - 1] = true;
		}

		Calendar cutoff = Calendar.getInstance();
		cutoff.setTime(currentMonth.getTime());

		if (command.getYear() > 0 && command.getMonth() > 0 && command.getDay() > 0)
		{
			cutoff.set(Calendar.DAY_OF_MONTH, command.getDay());
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
		int start = Math.max(0, command.getStart());
		int limit = Math.min(Math.max(0, command.getLimit()), maximumPageSize);
		if (limit < 1)
		{
			limit = defaultPageSize;
		}

		// load articles
		List<Article> articles = listArticlesBeforeDateInRange(command, cutoff.getTime(), start, limit);

		// count articles for pager
		int pageCount = countArticlesBeforeDate(command, cutoff.getTime());

		// wrap article list
		List<ArticleDecorator> wrappedArticles = new ArrayList<ArticleDecorator>(articles.size());
		for (Article article : articles)
		{
			wrappedArticles.add(new ArticleDecorator(article, contentFilter));
		}

		// get tag cloud
		List<TagCloudEntry> tagCloud = tagBusiness.getTagCloud();

		// populate model
		model.addAttribute("articles", wrappedArticles);
		model.addAttribute("days", days);
		model.addAttribute("pageCount", pageCount);
		model.addAttribute("pageStart", start);
		model.addAttribute("pageLimit", limit);
		model.addAttribute("tagCloud", tagCloud);

		String subTitle = getSubTitle(command);
		if (subTitle != null)
		{
			model.addAttribute("pageSubTitle", subTitle);
		}
	}
}