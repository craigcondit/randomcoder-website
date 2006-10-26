package com.randomcoder.user;

import org.springframework.validation.*;

public class UserAddValidator implements Validator
{
	public boolean supports(Class targetClass)
	{
		return UserAddCommand.class.equals(targetClass);
	}

	public void validate(Object target, Errors errors)
	{
	// TODO Auto-generated method stub

	}

}
