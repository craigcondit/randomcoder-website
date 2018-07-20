package org.randomcoder.mvc.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import javax.inject.Inject;

import org.randomcoder.article.moderation.ModerationException;
import org.randomcoder.bo.ArticleBusiness;
import org.randomcoder.db.Article;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller class which manages comments.
 */
@Controller("commentController")
public class CommentController {
	private ArticleBusiness articleBusiness;

	/**
	 * Sets the ArticleBusiness implementation to use.
	 * 
	 * @param articleBusiness
	 *            ArticleBusiness implementation
	 */
	@Inject
	public void setArticleBusiness(ArticleBusiness articleBusiness) {
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
	@RequestMapping(value = "/comment/{id}/approve", method = RequestMethod.POST, params = "_verb=PUT")
	public View approveCommentBrowser(@PathVariable("id") long id) throws ModerationException {
		Article article = articleBusiness.approveComment(id);
		return new RedirectView(article.getPermalinkUrl(), true);
	}

	/**
	 * Approves the selected comment.
	 * 
	 * @param id
	 *            comment ID
	 * @throws ModerationException
	 *             if an error occurs
	 */
	@RequestMapping(value = "/comment/{id}/approve", method = RequestMethod.PUT, params = "!_verb")
	@ResponseStatus(value = NO_CONTENT)
	@ResponseBody
	public void approveComment(@PathVariable("id") long id) throws ModerationException {
		articleBusiness.approveComment(id);
	}

	/**
	 * Disapproves the selected comment.
	 * 
	 * @param id
	 *            comment ID
	 * @return view to redirect to
	 * @throws ModerationException
	 *             if an error occurs
	 */
	@RequestMapping(value = "/comment/{id}/approve", method = RequestMethod.POST, params = "_verb=DELETE")
	public View disapproveCommentBrowser(@PathVariable("id") long id) throws ModerationException {
		Article article = articleBusiness.disapproveComment(id);
		return new RedirectView(article.getPermalinkUrl(), true);
	}

	/**
	 * Disapproves the selected comment.
	 * 
	 * @param id
	 *            comment ID
	 * @throws ModerationException
	 *             if an error occurs
	 */
	@RequestMapping(value = "/comment/{id}/approve", method = RequestMethod.DELETE, params = "!_verb")
	@ResponseStatus(value = NO_CONTENT)
	@ResponseBody
	public void disapproveComment(@PathVariable("id") long id) throws ModerationException {
		articleBusiness.disapproveComment(id);
	}

	/**
	 * Deletes the selected comment.
	 * 
	 * @param id
	 *            comment ID
	 * @return view to redirect to
	 */
	@RequestMapping(value = "/comment/{id}", method = RequestMethod.POST, params = "_verb=DELETE")
	public View deleteCommentBrowser(@PathVariable("id") long id) {
		Article article = articleBusiness.deleteComment(id);
		return new RedirectView(article.getPermalinkUrl(), true);
	}

	/**
	 * Deletes the selected comment.
	 * 
	 * @param id
	 *            comment ID
	 */
	@RequestMapping(value = "/comment/{id}", method = RequestMethod.DELETE, params = "!_verb")
	@ResponseStatus(value = NO_CONTENT)
	@ResponseBody
	public void deleteComment(@PathVariable("id") long id) {
		articleBusiness.deleteComment(id);
	}
}
