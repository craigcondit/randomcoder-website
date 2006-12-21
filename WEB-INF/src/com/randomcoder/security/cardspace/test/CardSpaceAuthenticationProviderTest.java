package com.randomcoder.security.cardspace.test;

import static org.junit.Assert.*;

import java.util.*;

import org.acegisecurity.*;
import org.acegisecurity.userdetails.UserDetails;
import org.junit.*;

import com.randomcoder.security.cardspace.*;

public class CardSpaceAuthenticationProviderTest
{
	private CardSpaceAuthenticationProvider provider = null;
	
	@Before
	public void setUp() throws Exception
	{
		provider = new CardSpaceAuthenticationProvider();
		provider.setCardSpaceUserDetailsService(new CardSpaceUserDetailsServiceMock());
	}

	@After
	public void tearDown() throws Exception
	{
		provider = null;
	}

	@Test
	public void testSetValidators()
	{
		List<CardSpaceCredentialsValidator> validators = new ArrayList<CardSpaceCredentialsValidator>();
		validators.add(new CardSpaceCredentialsValidatorMock());
		provider.setValidators(validators);
		provider.afterPropertiesSet();
	}

	@Test
	public void testSetValidator()
	{
		provider.setValidator(new CardSpaceCredentialsValidatorMock());
		provider.afterPropertiesSet();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAfterPropertiesSetNoValidators()
	{
		provider.afterPropertiesSet();
	}

	@Test
	public void testAuthenticate()
	{
		provider.setValidator(new CardSpaceCredentialsValidatorMock());
		provider.afterPropertiesSet();
		
		Authentication auth = null;
		auth = provider.authenticate(auth);
		
		fail("Not yet implemented");
	}

	@Test
	public void testSupports()
	{
		assertFalse(provider.supports(String.class));
		assertTrue(provider.supports(CardSpaceAuthenticationToken.class));
	}

	@SuppressWarnings("unused")
	private static class CardSpaceUserDetailsServiceMock implements CardSpaceUserDetailsService
	{
		public CardSpaceUserDetailsServiceMock() {}

		public UserDetails loadUserByCardSpaceCredentials(CardSpaceCredentials credentials)
		throws AuthenticationException
		{
			return null;
		}		
	}
	
	@SuppressWarnings("unused")
	private static class CardSpaceCredentialsValidatorMock implements CardSpaceCredentialsValidator
	{
		public CardSpaceCredentialsValidatorMock() {}

		public void validate(CardSpaceCredentials credentials) throws AuthenticationException
		{
		}
	}
}
