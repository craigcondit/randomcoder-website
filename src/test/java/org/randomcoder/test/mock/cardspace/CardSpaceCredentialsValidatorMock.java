package com.randomcoder.test.mock.cardspace;

import org.acegisecurity.AuthenticationException;

import com.randomcoder.security.cardspace.*;

public class CardSpaceCredentialsValidatorMock implements CardSpaceCredentialsValidator
{
	public CardSpaceCredentialsValidatorMock() {}

	@Override
	public void validate(CardSpaceCredentials credentials) throws AuthenticationException
	{
	}
}
