package com.randomcoder.test.mock.user;

import javax.servlet.http.HttpServletRequest;

import com.randomcoder.user.ChangePasswordController;

public class ChangePasswordControllerMock extends ChangePasswordController
{
	@Override
	public void onBind(HttpServletRequest request, Object command) throws Exception
	{
		super.onBind(request, command);
	}
	
}	