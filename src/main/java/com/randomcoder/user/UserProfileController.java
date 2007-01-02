package com.randomcoder.user;

import java.util.List;

import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

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
public class UserProfileController extends AbstractCommandController
{
	private UserDao userDao;
	private CardSpaceTokenDao cardSpaceTokenDao;
	private String viewName;
	
	
	/**
	 * Sets the UserDao implementation to use.
	 * @param userDao UserDao implementation
	 */
	@Required
	public void setUserDao(UserDao userDao)
	{
		this.userDao = userDao;
	}
	
	/**
	 * Sets the CardSpaceTokenDao implementation to use.
	 * @param cardSpaceTokenDao CardSpaceTokenDao implementation
	 */
	public void setCardSpaceTokenDao(CardSpaceTokenDao cardSpaceTokenDao)
	{
		this.cardSpaceTokenDao = cardSpaceTokenDao;
	}
	
	/**
	 * Sets the name of the view to use for the user list.
	 * @param viewName view name
	 */
	@Required
	public void setViewName(String viewName)
	{
		this.viewName = viewName;
	}
	
	@Override
	protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception
	{
		// get current user
		String userName = request.getUserPrincipal().getName();
		
		User user = userDao.findByUserName(userName);
		if (user == null)
			throw new UserNotFoundException("No such user: " + userName);
		
		List<CardSpaceToken> cardSpaceTokens = cardSpaceTokenDao.listByUser(user);
		
		// create model
		ModelAndView mav = new ModelAndView(viewName);
		
		// populate model
		mav.addObject("user", user);
		mav.addObject("cardSpaceTokens", cardSpaceTokens);
		
		return mav;
	}
}