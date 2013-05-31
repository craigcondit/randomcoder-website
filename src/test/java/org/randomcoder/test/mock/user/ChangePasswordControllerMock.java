package org.randomcoder.test.mock.user;

import javax.servlet.http.HttpServletRequest;

import org.randomcoder.user.ChangePasswordController;

@SuppressWarnings("javadoc")
public class ChangePasswordControllerMock extends ChangePasswordController
{
	@Override
	public void onBind(HttpServletRequest request, Object command) throws Exception
	{
		super.onBind(request, command);
	}
	
}	
