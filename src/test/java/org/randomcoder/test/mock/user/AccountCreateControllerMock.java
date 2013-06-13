package org.randomcoder.test.mock.user;

import javax.servlet.http.HttpServletRequest;

import org.randomcoder.user.AccountCreateController;
import org.springframework.web.bind.ServletRequestDataBinder;

@SuppressWarnings("javadoc")
public class AccountCreateControllerMock extends AccountCreateController
{
	@Override
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
	throws Exception
	{
		super.initBinder(request, binder);
	}
}
