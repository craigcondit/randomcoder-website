package org.randomcoder.test.mock.acegisecurity;

import org.acegisecurity.*;
import org.acegisecurity.userdetails.UserDetails;

public class UserDetailsMock implements UserDetails
{
	private static final long serialVersionUID = -6648737648831411882L;

	public UserDetailsMock() {}
	
	@Override
	public GrantedAuthority[] getAuthorities()
	{
		return new GrantedAuthority[] { new GrantedAuthorityImpl("ROLE_TEST") };
	}

	@Override
	public String getPassword()
	{
		return "pass";
	}

	@Override
	public String getUsername()
	{
		return "test";
	}

	@Override
	public boolean isAccountNonExpired()
	{
		return true;
	}

	@Override
	public boolean isAccountNonLocked()
	{
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired()
	{
		return true;
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}		
}
