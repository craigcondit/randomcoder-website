package org.randomcoder.mvc.controller;

import java.util.*;

import org.randomcoder.db.Article;
import org.randomcoder.mvc.command.ArticlePageCommand;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller class which handles the front page of the site.
 */
@Controller("homeController")
public class HomeController extends AbstractArticleListController<ArticlePageCommand>
{
	@Override
	protected List<Article> listArticlesBetweenDates(ArticlePageCommand command, Date startDate, Date endDate)
	{
		return articleBusiness.listArticlesBetweenDates(startDate, endDate);
	}

	@Override
	protected List<Article> listArticlesBeforeDateInRange(ArticlePageCommand command, Date cutoffDate, int start, int limit)
	{
		return articleBusiness.listArticlesBeforeDateInRange(cutoffDate, start, limit);
	}

	@Override
	protected int countArticlesBeforeDate(ArticlePageCommand command, Date cutoffDate)
	{
		return articleBusiness.countArticlesBeforeDate(cutoffDate);
	}

	@Override
	protected String getSubTitle(ArticlePageCommand command)
	{
		return null;
	}

	/**
	 * Renders the home view.
	 * 
	 * @param command
	 *            page command
	 * @param model
	 *            MVC model
	 * @return home view
	 */
	@RequestMapping("")
	public String home(ArticlePageCommand command, Model model)
	{
		populateModel(command, model);
		return "home";
	}
}