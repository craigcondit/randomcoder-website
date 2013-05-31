package org.randomcoder.test.mock.user;

import javax.servlet.http.HttpServletRequest;

import org.randomcoder.user.UserAddController;

@SuppressWarnings("javadoc")
public class UserAddControllerMock extends UserAddController
{
	@Override
	public void onBindOnNewForm(HttpServletRequest request, Object command) throws Exception
	{
		super.onBindOnNewForm(request, command);
	}	
}
