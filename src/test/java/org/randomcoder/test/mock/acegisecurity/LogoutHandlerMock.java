package org.randomcoder.test.mock.acegisecurity;

import javax.servlet.http.*;

import org.acegisecurity.Authentication;
import org.acegisecurity.ui.logout.LogoutHandler;

public class LogoutHandlerMock implements LogoutHandler
{		
	private Authentication authentication = null;
	
	public LogoutHandlerMock() {}
	
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication auth)
	{
		authentication = auth;
	}
	
	public Authentication getAuthentication()
	{
		return authentication;
	}		
}
