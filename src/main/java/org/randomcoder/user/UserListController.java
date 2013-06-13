package org.randomcoder.user;

import java.util.List;

import javax.servlet.http.*;

import org.randomcoder.bo.UserBusiness;
import org.randomcoder.db.User;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

/**
 * Controller used to list users.
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
public class UserListController extends AbstractCommandController
{
	private UserBusiness userBusiness;
	private String viewName;
	private int defaultPageSize = 25;
	private int maximumPageSize = 100;

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
	 * Sets the name of the view to use for the user list.
	 * 
	 * @param viewName
	 *            view name
	 */
	@Required
	public void setViewName(String viewName)
	{
		this.viewName = viewName;
	}

	/**
	 * Sets the default number of items to display per page (defaults to 25).
	 * 
	 * @param defaultPageSize
	 *            default number of items per page
	 */
	public void setDefaultPageSize(int defaultPageSize)
	{
		this.defaultPageSize = defaultPageSize;
	}

	/**
	 * Sets the maximum number of items to allow per page (defaults to 100).
	 * 
	 * @param maximumPageSize
	 *            maximum number of items per page
	 */
	public void setMaximumPageSize(int maximumPageSize)
	{
		this.maximumPageSize = maximumPageSize;
	}

	/**
	 * Handles user list requests.
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @param command
	 *            command object
	 * @param errors
	 *            error object
	 * @return ModelAndView configured with {@link #setViewName(String)}
	 */
	@Override
	protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception
	{
		UserListCommand cmd = (UserListCommand) command;

		// set range
		int start = cmd.getStart();
		if (start < 0)
			start = 0;
		cmd.setStart(start);

		int limit = cmd.getLimit();
		if (limit <= 0)
			limit = defaultPageSize;
		if (limit > maximumPageSize)
			limit = maximumPageSize;
		cmd.setLimit(limit);

		List<User> users = userBusiness.listUsersInRange(start, limit);
		int count = userBusiness.countUsers();

		// create model
		ModelAndView mav = new ModelAndView(viewName);

		// populate model
		mav.addObject("users", users);
		mav.addObject("pageCount", count);
		mav.addObject("pageStart", start);
		mav.addObject("pageLimit", limit);

		return mav;
	}

}
