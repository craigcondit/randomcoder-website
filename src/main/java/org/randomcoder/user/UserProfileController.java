package org.randomcoder.user;

import java.util.*;

import javax.servlet.http.*;

import org.randomcoder.bo.UserBusiness;
import org.randomcoder.db.User;
import org.springframework.validation.*;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.CancellableFormController;

/**
 * Controller used to handle editing user profiles.
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
public class UserProfileController extends CancellableFormController
{
	private UserBusiness userBusiness;

	/**
	 * Sets the UserBusiness implementation to use.
	 * 
	 * @param userBusiness
	 *            UserBusiness implementation
	 */
	public void setUserBusiness(UserBusiness userBusiness)
	{
		this.userBusiness = userBusiness;
	}

	/**
	 * Initializes form with custom property editors.
	 * 
	 * @param request
	 *            HTTP request
	 * @param binder
	 *            data binder
	 * @throws Exception
	 *             if an error occurs
	 */
	@Override
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception
	{
		super.initBinder(request, binder);
	}

	/**
	 * Populates the model with reference data.
	 * 
	 * @param request
	 *            HTTP request
	 * @param command
	 *            command object
	 * @param errors
	 *            error object
	 * @return required data for view
	 */
	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception
	{
		Map<String, Object> data = new HashMap<String, Object>();

		User user = getUser(request);

		data.put("user", user);

		return data;
	}

	/**
	 * Pre-populates form on new request.
	 * 
	 * @param request
	 *            HTTP request
	 * @param command
	 *            command object
	 * @param errors
	 *            error object
	 * @throws Exception
	 *             if an error occurs
	 */
	@Override
	protected void onBindOnNewForm(HttpServletRequest request, Object command, BindException errors)
			throws Exception
	{
		UserProfileCommand cmd = (UserProfileCommand) command;

		User user = getUser(request);

		cmd.setEmailAddress(user.getEmailAddress());
		cmd.setWebsite(user.getWebsite());
	}

	/**
	 * Updates the current user's profile.
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @param command
	 *            command object
	 * @param errors
	 *            error object
	 * @throws Exception
	 *             if an error occurs
	 * @return ModelAndView configured with {@link #setSuccessView(String)}
	 */
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception
	{
		User user = getUser(request);

		UserProfileCommand form = (UserProfileCommand) command;
		userBusiness.updateUser(form, user.getId());

		return new ModelAndView(getSuccessView());
	}

	private User getUser(HttpServletRequest request)
	{
		// get current user
		String userName = request.getUserPrincipal().getName();

		User user = userBusiness.findUserByName(userName);
		if (user == null)
			throw new UserNotFoundException("No such user: " + userName);
		return user;
	}
}