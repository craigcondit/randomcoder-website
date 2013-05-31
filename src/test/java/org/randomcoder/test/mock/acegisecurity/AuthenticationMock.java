package org.randomcoder.test.mock.acegisecurity;

import org.acegisecurity.*;

@SuppressWarnings("javadoc")
public class AuthenticationMock implements Authentication
{
	private static final long serialVersionUID = 3105620828874678824L;

	public AuthenticationMock() {}
	
	@Override
	public GrantedAuthority[] getAuthorities()
	{
		return new GrantedAuthority[] {};
	}

	@Override
	public Object getCredentials()
	{
		return null;
	}

	@Override
	public Object getDetails()
	{
		return null;
	}

	@Override
	public Object getPrincipal()
	{
		return "temp";
	}

	@Override
	public boolean isAuthenticated()
	{
		return false;
	}

	@Override
	public void setAuthenticated(boolean authenticated) throws IllegalArgumentException
	{
	}

	@Override
	public String getName()
	{
		return "temp";
	}
	
}
