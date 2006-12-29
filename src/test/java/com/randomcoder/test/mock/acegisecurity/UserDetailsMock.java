package com.randomcoder.test.mock.acegisecurity;

import org.acegisecurity.*;
import org.acegisecurity.userdetails.UserDetails;

public class UserDetailsMock implements UserDetails
{
	private static final long serialVersionUID = -6648737648831411882L;

	public UserDetailsMock() {}
	
	public GrantedAuthority[] getAuthorities()
	{
		return new GrantedAuthority[] { new GrantedAuthorityImpl("ROLE_TEST") };
	}

	public String getPassword()
	{
		return "pass";
	}

	public String getUsername()
	{
		return "test";
	}

	public boolean isAccountNonExpired()
	{
		return true;
	}

	public boolean isAccountNonLocked()
	{
		return true;
	}

	public boolean isCredentialsNonExpired()
	{
		return true;
	}

	public boolean isEnabled()
	{
		return true;
	}		
}
