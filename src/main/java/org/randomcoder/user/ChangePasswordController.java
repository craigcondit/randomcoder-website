package org.randomcoder.user;

import java.security.Principal;

import javax.servlet.http.*;

import org.randomcoder.bo.UserBusiness;
import org.randomcoder.db.User;
import org.randomcoder.mvc.command.ChangePasswordCommand;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.CancellableFormController;

/**
 * Controller used to change a user's password.
 * 
 * <pre>
 * Copyright (c) 2006-2007, Craig Condit. All rights reserved.
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
public class ChangePasswordController extends CancellableFormController
{
	private UserBusiness userBusiness;

	/**
	 * Sets the UserBusiness implementation to use.
	 * 
	 * @param userBusiness
	 *            UserBusiness implementation
	 */
	@Required
	public void setUserBusiness(UserBusiness userBusiness)
	{
		this.userBusiness = userBusiness;
	}

	/**
	 * Binds the current user to the given command.
	 * 
	 * @param request
	 *            HTTP request
	 * @param command
	 *            command object
	 * @throws Exception
	 *             if an error occurs
	 */
	@Override
	protected void onBind(HttpServletRequest request, Object command) throws Exception
	{
		super.onBind(request, command);

		ChangePasswordCommand form = (ChangePasswordCommand) command;

		// populate the form with the current user

		User user = null;
		String username = null;

		Principal principal = request.getUserPrincipal();
		if (principal != null)
			username = principal.getName();
		if (username != null)
			user = userBusiness.findUserByNameEnabled(username);

		form.setUser(user);
	}

	/**
	 * Handles form submissions.
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @param command
	 *            command object
	 * @param errors
	 *            error object
	 * @return ModelAndView configured using {@link #setSuccessView(String)}
	 */
	@Override
	public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
	{
		ChangePasswordCommand cmd = (ChangePasswordCommand) command;

		String userName = request.getUserPrincipal().getName();

		userBusiness.changePassword(userName, cmd.getPassword());

		return new ModelAndView(getSuccessView());
	}

}
