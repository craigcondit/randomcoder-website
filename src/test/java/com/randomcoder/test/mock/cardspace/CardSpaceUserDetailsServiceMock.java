package com.randomcoder.test.mock.cardspace;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.userdetails.UserDetails;

import com.randomcoder.security.cardspace.*;
import com.randomcoder.test.mock.acegisecurity.UserDetailsMock;

public class CardSpaceUserDetailsServiceMock implements CardSpaceUserDetailsService
{
	private final boolean returnNull;
	
	public CardSpaceUserDetailsServiceMock(boolean returnNull)
	{
		this.returnNull = returnNull;
	}

	public UserDetails loadUserByCardSpaceCredentials(CardSpaceCredentials credentials)
	throws AuthenticationException
	{
		return returnNull ? null : new UserDetailsMock();
	}		
}

