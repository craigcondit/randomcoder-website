package com.randomcoder.test.mock.acegisecurity;

import org.acegisecurity.*;

public class AuthenticationMock implements Authentication
{
	private static final long serialVersionUID = 3105620828874678824L;

	public AuthenticationMock() {}
	
	public GrantedAuthority[] getAuthorities()
	{
		return new GrantedAuthority[] {};
	}

	public Object getCredentials()
	{
		return null;
	}

	public Object getDetails()
	{
		return null;
	}

	public Object getPrincipal()
	{
		return "temp";
	}

	public boolean isAuthenticated()
	{
		return false;
	}

	public void setAuthenticated(boolean authenticated) throws IllegalArgumentException
	{
	}

	public String getName()
	{
		return "temp";
	}
	
}