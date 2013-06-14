package org.randomcoder.mvc.controller;

import java.security.Principal;

import javax.inject.Inject;

import org.randomcoder.bo.ArticleBusiness;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing articles.
 */
@Controller("articleController")
public class ArticleController
{
	private ArticleBusiness articleBusiness;
	private String viewName;

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
	 * Deletes the selected article.
	 * 
	 * @param id
	 *            article ID
	 * @param user
	 *            current user
	 * @return default view
	 */
	@RequestMapping("/article/delete")
	public String deleteArticle(@RequestParam("id") long id, Principal user)
	{
		articleBusiness.deleteArticle(user.getName(), id);
		return "default";
	}
}
