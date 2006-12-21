package com.randomcoder.test.mock.user;

import javax.servlet.http.*;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.randomcoder.user.UserListController;

public class UserListControllerMock extends UserListController
{

	@Override
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception
	{
		return super.handle(request, response, command, errors);
	}
	
}