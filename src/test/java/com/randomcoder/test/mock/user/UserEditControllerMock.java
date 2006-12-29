package com.randomcoder.test.mock.user;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindException;

import com.randomcoder.user.UserEditController;

public class UserEditControllerMock extends UserEditController
{
	@Override
	public void onBindOnNewForm(HttpServletRequest request, Object _command, BindException errors)
	{
		super.onBindOnNewForm(request, _command, errors);
	}
}