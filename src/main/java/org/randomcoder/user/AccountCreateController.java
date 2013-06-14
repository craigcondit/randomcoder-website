package org.randomcoder.user;

import javax.servlet.http.*;

import org.randomcoder.bo.UserBusiness;
import org.randomcoder.mvc.command.AccountCreateCommand;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.CancellableFormController;

/**
 * Controller class which handles adding user accounts.
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
public class AccountCreateController extends CancellableFormController
{
	private static final long TOKEN_EXPIRATION_TIME = 30 * 60 * 1000; // 30 minutes
	
	private UserBusiness userBusiness;

	/**
	 * Sets the UserBusiness implementation to use.
	 * @param userBusiness UserBusiness implementation
	 */
	@Required
	public void setUserBusiness(UserBusiness userBusiness)
	{
		this.userBusiness = userBusiness;
	}
	
	/**
	 * Binds custom property editors.
	 * @param request HTTP request
	 * @param binder data binder
	 * @throws Exception if an error occurs 
	 */
	@Override
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception
	{
		super.initBinder(request, binder);
	}
	
	/**
	 * Creates a new account based on form submission.
	 */
	@Override
	public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
	throws Exception
	{
		AccountCreateCommand form = (AccountCreateCommand) command;
		
		// password auth, done
		userBusiness.createAccount(form);			

		return new ModelAndView(getSuccessView());
	}
}