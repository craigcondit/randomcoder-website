package org.randomcoder.mvc.controller;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.randomcoder.db.*;
import org.randomcoder.mvc.command.ArticleTagListCommand;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class which handles displaying articles by tag.
 */
@Controller("articleTagListController")
public class ArticleTagListController extends AbstractArticleListController<ArticleTagListCommand>
{
	private static final Log logger = LogFactory.getLog(ArticleTagListController.class);

	@Override
	protected Page<Article> listArticlesBeforeDate(ArticleTagListCommand command, Date cutoffDate, Pageable pageable)
	{
		return articleBusiness.listArticlesByTagBeforeDate(command.getTag(), cutoffDate, pageable);
	}

	@Override
	protected List<Article> listArticlesBetweenDates(ArticleTagListCommand command, Date startDate, Date endDate)
	{
		return articleBusiness.listArticlesByTagBetweenDates(command.getTag(), startDate, endDate);
	}

	@Override
	protected String getSubTitle(ArticleTagListCommand command)
	{
		Tag tag = command.getTag();
		return tag == null ? null : tag.getDisplayName();
	}

	/**
	 * Renders articles for a given tag.
	 * 
	 * @param command
	 *          tag page command
	 * @param tagName
	 *          tag name to display
	 * @param model
	 *          MVC model
	 * @param pageable
	 *          paging variables
	 * @return home view
	 */
	@RequestMapping("/tags/{tagName}")
	public String tagList(
			ArticleTagListCommand command, Model model,
			@PathVariable("tagName") String tagName,
			@PageableDefaults(10) Pageable pageable)
	{
		tagName = StringUtils.trimToEmpty(tagName).toLowerCase(Locale.US);
		logger.debug("Tag name: " + tagName);
		command.setTag(tagBusiness.findTagByName(tagName));

		populateModel(command, model, pageable);

		return "article-tag-list";
	}
}