package org.randomcoder.test.mock.user;

import javax.servlet.http.*;

import org.randomcoder.user.UserListController;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

@SuppressWarnings("javadoc")
public class UserListControllerMock extends UserListController
{

	@Override
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception
	{
		return super.handle(request, response, command, errors);
	}
	
}
