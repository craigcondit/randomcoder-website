package org.randomcoder.test.mock.user;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestDataBinder;

import org.randomcoder.user.AbstractUserController;

@SuppressWarnings("javadoc")
public class AbstractUserControllerMock extends AbstractUserController
{
	@Override
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception
	{
		super.initBinder(request, binder);
	}

	@Override
	public Map referenceData(HttpServletRequest request)
	{
		return super.referenceData(request);
	}
	
}
