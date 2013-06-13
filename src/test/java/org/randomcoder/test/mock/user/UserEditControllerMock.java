package org.randomcoder.test.mock.user;

import javax.servlet.http.HttpServletRequest;

import org.randomcoder.user.UserEditController;
import org.springframework.validation.BindException;

@SuppressWarnings("javadoc")
public class UserEditControllerMock extends UserEditController
{
	@Override
	public void onBindOnNewForm(HttpServletRequest request, Object _command, BindException errors)
	{
		super.onBindOnNewForm(request, _command, errors);
	}
}
