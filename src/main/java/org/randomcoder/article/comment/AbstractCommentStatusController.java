package org.randomcoder.article.comment;

import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.springframework.web.servlet.view.RedirectView;

import org.randomcoder.article.*;
import org.randomcoder.bo.ArticleBusiness;
import org.randomcoder.springmvc.IdCommand;

/**
 * Abstract base class for controllers which modify a comment's status.
 * 
 * <pre>
 * Copyright (c) 2007, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
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
	 * @param response HTTP response
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
