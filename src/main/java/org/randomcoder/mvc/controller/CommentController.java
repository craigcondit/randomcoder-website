package org.randomcoder.mvc.controller;

import javax.inject.Inject;

import org.randomcoder.article.moderation.ModerationException;
import org.randomcoder.bo.ArticleBusiness;
import org.randomcoder.db.Article;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller class which manages comments.
 */
@Controller("commentController")
public class CommentController
{
	private ArticleBusiness articleBusiness;

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
	 * Approves the selected comment.
	 * 
	 * @param id
	 *            comment ID
	 * @return view to redirect to
	 * @throws ModerationException
	 *             if an error occurs
	 */
	@RequestMapping(value = "/comment/{id}/approve", method = RequestMethod.POST)
	public View approveComment(@PathVariable("id") long id) throws ModerationException
	{
		Article article = articleBusiness.approveComment(id);
		return new RedirectView(article.getPermalinkUrl(), true);
	}

	/**
	 * Approves the selected comment.
	 * 
	 * @param id
	 *            comment ID
	 * @return view to redirect to
	 * @throws ModerationException
	 *             if an error occurs
	 */
	@RequestMapping(value = "/comment/{id}/disapprove", method = RequestMethod.POST)
	public View disapproveComment(@PathVariable("id") long id) throws ModerationException
	{
		Article article = articleBusiness.disapproveComment(id);
		return new RedirectView(article.getPermalinkUrl(), true);
	}

	/**
	 * Deletes the selected comment.
	 * 
	 * @param id
	 *            comment ID
	 * @return view to redirect to
	 */
	@RequestMapping(value = "/comment/{id}", method = RequestMethod.DELETE)
	public View deleteComment(@PathVariable("id") long id)
	{
		Article article = articleBusiness.deleteComment(id);
		return new RedirectView(article.getPermalinkUrl(), true);
	}

}
