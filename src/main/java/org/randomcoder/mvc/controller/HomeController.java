package org.randomcoder.mvc.controller;

import java.util.*;

import org.randomcoder.db.Article;
import org.randomcoder.mvc.command.ArticleListCommand;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller class which handles the front page of the site.
 */
@Controller("homeController")
public class HomeController extends AbstractArticleListController<ArticleListCommand>
{

	@Override
	protected Page<Article> listArticlesBeforeDate(ArticleListCommand command, Date cutoffDate, Pageable pageable)
	{
		return articleBusiness.listArticlesBeforeDate(cutoffDate, pageable);
	}

	@Override
	protected List<Article> listArticlesBetweenDates(ArticleListCommand command, Date startDate, Date endDate)
	{
		return articleBusiness.listArticlesBetweenDates(startDate, endDate);
	}

	@Override
	protected String getSubTitle(ArticleListCommand command)
	{
		return null;
	}

	/**
	 * Renders the home view.
	 * 
	 * @param command
	 *          page command
	 * @param model
	 *          MVC model
	 * @param pageable
	 *          paging parameters
	 * @return home view
	 */
	@RequestMapping("")
	public String home(ArticleListCommand command, Model model, @PageableDefaults(10) Pageable pageable)
	{
		populateModel(command, model, pageable);
		return "home";
	}
}