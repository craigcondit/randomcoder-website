package com.randomcoder.user;

import java.util.List;

import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import com.randomcoder.bean.User;
import com.randomcoder.dao.UserDao;

public class UserListController extends AbstractCommandController
{
	private UserDao userDao;	
	private String viewName;
	private int defaultPageSize = 25;	
	private int maximumPageSize = 100;
	
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
	 * Sets the name of the view to use for the user list.
	 * @param viewName view name
	 */
	@Required
	public void setViewName(String viewName)
	{
		this.viewName = viewName;
	}
	
	/**
	 * Sets the default number of items to display per page (defaults to 25).
	 * @param defaultPageSize default number of items per page
	 */
	public void setDefaultPageSize(int defaultPageSize)
	{
		this.defaultPageSize = defaultPageSize;
	}

	/**
	 * Sets the maximum number of items to allow per page (defaults to 100).
	 * @param maximumPageSize maximum number of items per page
	 */
	public void setMaximumPageSize(int maximumPageSize)
	{
		this.maximumPageSize = maximumPageSize;
	}
	
	@Override
	protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception
	{
		UserListCommand cmd = (UserListCommand) command;
		
		// set range
		int start = cmd.getStart();
		if (start < 0) start = 0;
		cmd.setStart(start);
		
		int limit = cmd.getLimit();
		if (limit <= 0) limit = defaultPageSize;
		if (limit > maximumPageSize) limit = maximumPageSize;		
		cmd.setLimit(limit);

		List<User> users = userDao.listAllInRange(start, limit);
		int count = userDao.countAll();

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
