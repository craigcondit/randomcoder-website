package org.randomcoder.test.mock.springmvc;

import javax.servlet.http.HttpServletRequest;

import org.randomcoder.springmvc.JstlTemplateView;

public class JstlTemplateViewMock extends JstlTemplateView
{
	@Override
	public void exposeHelpers(HttpServletRequest request) throws Exception
	{
		super.exposeHelpers(request);
	}

	@Override
	public String getTemplateName()
	{
		return super.getTemplateName();
	}
}
