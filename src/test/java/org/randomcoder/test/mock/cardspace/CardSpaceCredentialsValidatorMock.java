package org.randomcoder.test.mock.cardspace;

import org.acegisecurity.AuthenticationException;

import org.randomcoder.security.cardspace.*;

@SuppressWarnings("javadoc")
public class CardSpaceCredentialsValidatorMock implements CardSpaceCredentialsValidator
{
	public CardSpaceCredentialsValidatorMock() {}

	@Override
	public void validate(CardSpaceCredentials credentials) throws AuthenticationException
	{
	}
}
