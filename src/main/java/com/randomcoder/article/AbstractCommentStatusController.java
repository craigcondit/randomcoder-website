package com.randomcoder.article;

import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.springframework.web.servlet.view.RedirectView;

import com.randomcoder.springmvc.IdCommand;

abstract public class AbstractCommentStatusController extends AbstractCommandController
{
	/**
	 * ArticleBusiness implementation.
	 */
	protected ArticleBusiness articleBusiness;

	/**
	 * Sets the ArticleBusiness implementation to use.
	 * @param articleBusiness ArticleBusiness implementation
	 */
	@Required
	public final void setArticleBusiness(ArticleBusiness articleBusiness)
	{
		this.articleBusiness = articleBusiness;
	}
	
	/**
	 * Updates the selected comment, and redirects back to permalink page for
	 * the associated article.
	 * @param request HTTP request
	 * @param resposne HTTP response
	 * @param command command object
	 * @param errors error object
	 * @throws Exception if an error occurs
	 * @return redirect view pointing to the associated article's permalink page
	 */
	@Override
	public final ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
	throws Exception
	{
		IdCommand cmd = (IdCommand) command;
				
		Article article = updateCommentStatus(cmd);

		View view = new RedirectView(article.getPermalinkUrl(), true);
		
		return new ModelAndView(view);
	}
		
	/**
	 * Updates the selected comment. 
	 * @param command Command object containing the ID to update
	 * @return Article to update
	 * @throws Exception if an error occurs
	 */
	abstract protected Article updateCommentStatus(IdCommand command) throws Exception;
}
