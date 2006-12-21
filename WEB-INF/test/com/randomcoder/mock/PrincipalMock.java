package com.randomcoder.mock;

import java.security.Principal;

public class PrincipalMock implements Principal
{
	private final String name;
	
	public PrincipalMock(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
}
