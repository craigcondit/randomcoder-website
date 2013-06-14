package org.randomcoder.article;

import javax.servlet.http.*;

import org.randomcoder.mvc.command.ArticleEditCommand;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller class which handles article updating.
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
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
public class ArticleEditController extends AbstractArticleController
{
	/**
	 * Pre-populates form on new request and checks permissions.
	 */
	@Override
	protected void onBindOnNewForm(HttpServletRequest request, Object command, BindException errors)
	{
		ArticleEditCommand cmd = (ArticleEditCommand) command;
		
		articleBusiness.loadArticleForEditing(cmd, cmd.getId(), request.getUserPrincipal().getName());
	}

	/**
	 * Modifies the selected article on form submission.
	 */
	@Override
	public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
	{
		ArticleEditCommand cmd = (ArticleEditCommand) command;
		
		articleBusiness.updateArticle(cmd, cmd.getId(), request.getUserPrincipal().getName());
		
		return new ModelAndView(getSuccessView());
	}
}
