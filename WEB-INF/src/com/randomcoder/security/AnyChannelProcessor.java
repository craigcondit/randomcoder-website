package com.randomcoder.security;

import java.io.IOException;

import javax.servlet.ServletException;

import org.acegisecurity.*;
import org.acegisecurity.intercept.web.FilterInvocation;
import org.acegisecurity.securechannel.ChannelProcessor;

public class AnyChannelProcessor implements ChannelProcessor
{
	private static final String ATTRIBUTE = "REQUIRES_ANY";
	
	public void decide(FilterInvocation invocation, ConfigAttributeDefinition definition)
	throws IOException, ServletException
	{
	}

	public boolean supports(ConfigAttribute att)	
	{
		return (ATTRIBUTE.equals(att.getAttribute()));
	}

}
