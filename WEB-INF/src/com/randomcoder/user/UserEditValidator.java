package com.randomcoder.user;

public class UserEditValidator extends UserAddValidator
{
	
	@Override
	public boolean supports(Class targetClass)
	{
		return UserEditCommand.class.equals(targetClass);
	}

}
