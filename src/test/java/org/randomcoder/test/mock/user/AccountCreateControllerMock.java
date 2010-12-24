package com.randomcoder.test.mock.user;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestDataBinder;

import com.randomcoder.user.AccountCreateController;

public class AccountCreateControllerMock extends AccountCreateController
{
	@Override
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
	throws Exception
	{
		super.initBinder(request, binder);
	}
}
