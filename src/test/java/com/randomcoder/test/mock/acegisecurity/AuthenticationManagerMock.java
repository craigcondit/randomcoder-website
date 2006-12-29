package com.randomcoder.test.mock.acegisecurity;

import org.acegisecurity.*;

public class AuthenticationManagerMock implements AuthenticationManager
{
	public AuthenticationManagerMock() {}
	
	public Authentication authenticate(Authentication auth)
	throws AuthenticationException
	{
		return auth;
	}
	
}