package org.randomcoder.controller;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.randomcoder.article.ArticleTagPageCommand;
import org.randomcoder.db.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class which handles displaying articles by tag.
 */
@Controller("articleTagListController")
public class ArticleTagListController extends AbstractArticleListController<ArticleTagPageCommand>
{
	private static final Log logger = LogFactory.getLog(ArticleTagListController.class);

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
	 * Renders articles for a given tag.
	 * 
	 * @param command
	 *            tag page command
	 * @param tagName
	 *            tag name to display
	 * @param model
	 *            MVC model
	 * @return home view
	 */
	@RequestMapping("/tags/{tag}")
	public String tagList(
			ArticleTagPageCommand command,
			@PathVariable("tag") String tagName,
			Model model)
	{
		tagName = StringUtils.trimToEmpty(tagName).toLowerCase(Locale.US);
		logger.debug("Tag name: " + tagName);
		command.setTag(tagBusiness.findTagByName(tagName));

		populateModel(command, model);

		return "article-tag-list";
	}
}