package com.randomcoder.test.mock.user;

import javax.servlet.http.HttpServletRequest;

import com.randomcoder.user.UserAddController;

public class UserAddControllerMock extends UserAddController
{
	@Override
	public void onBindOnNewForm(HttpServletRequest request, Object command) throws Exception
	{
		super.onBindOnNewForm(request, command);
	}	
}