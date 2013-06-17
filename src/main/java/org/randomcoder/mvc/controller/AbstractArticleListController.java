package org.randomcoder.mvc.controller;

import java.util.*;

import javax.inject.Inject;

import org.randomcoder.article.ArticleDecorator;
import org.randomcoder.bo.*;
import org.randomcoder.content.ContentFilter;
import org.randomcoder.db.Article;
import org.randomcoder.mvc.command.ArticleListCommand;
import org.randomcoder.tag.TagCloudEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefaults;
import org.springframework.ui.Model;

/**
 * Abstract base class for controllers which generate article lists.
 * 
 * @param <T>
 *            command type
 */
abstract public class AbstractArticleListController<T extends ArticleListCommand>
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
	 * Gets a page of articles before a given cutoff date.
	 * 
	 * @param command
	 *            page command
	 * @param cutoffDate
	 *            cutoff date
	 * @param pageable
	 *            paging parameters
	 * @return page of Articles
	 */
	abstract protected Page<Article> listArticlesBeforeDate(T command, Date cutoffDate, Pageable pageable);

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
	 *          page command
	 * @param model
	 *          model
	 * @param pageable
	 *          paging variables
	 */
	protected final void populateModel(T command, Model model, @PageableDefaults(10) Pageable pageable)
	{
		// set range and sort order
		int size = pageable.getPageSize();
		int page = pageable.getPageNumber();
		if (size > maximumPageSize)
		{
			size = maximumPageSize;
			page = 0;	
		}
		
		pageable = new PageRequest(page, size, new Sort(Direction.DESC, "creationDate"));
		
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

		// load articles
		Page<Article> articles = listArticlesBeforeDate(command, cutoff.getTime(), pageable);

		// wrap article list
		List<ArticleDecorator> wrappedArticles = new ArrayList<>(articles.getContent().size());
		for (Article article : articles.getContent())
		{
			wrappedArticles.add(new ArticleDecorator(article, contentFilter));
		}

		// get tag cloud
		List<TagCloudEntry> tagCloud = tagBusiness.getTagCloud();

		// populate model
		model.addAttribute("articles", wrappedArticles);
		model.addAttribute("pager", articles);
		model.addAttribute("days", days);
		model.addAttribute("tagCloud", tagCloud);

		String subTitle = getSubTitle(command);
		if (subTitle != null)
		{
			model.addAttribute("pageSubTitle", subTitle);
		}
	}
}