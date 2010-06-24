package com.randomcoder.test.mock.acegisecurity;

import org.acegisecurity.ConfigAttribute;

public class ConfigAttributeMock implements ConfigAttribute
{
	private static final long serialVersionUID = -174801702398598227L;
	
	private final String attribute;
	
	public ConfigAttributeMock(String attribute)
	{
		this.attribute = attribute;
	}
	
	@Override
	public String getAttribute() { return attribute; }
}
