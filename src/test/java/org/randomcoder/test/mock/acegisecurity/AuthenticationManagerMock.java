package org.randomcoder.test.mock.acegisecurity;

import org.acegisecurity.*;

public class AuthenticationManagerMock implements AuthenticationManager
{
	public AuthenticationManagerMock() {}
	
	@Override
	public Authentication authenticate(Authentication auth)
	throws AuthenticationException
	{
		return auth;
	}
	
}
