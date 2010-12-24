package org.randomcoder.test.mock.jse;

import java.security.Principal;

public class PrincipalMock implements Principal
{
	private final String name;
	
	public PrincipalMock(String name)
	{
		this.name = name;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
}
