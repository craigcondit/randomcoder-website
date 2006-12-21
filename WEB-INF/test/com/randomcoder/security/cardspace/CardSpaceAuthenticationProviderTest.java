package com.randomcoder.security.cardspace;

import static com.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_ALL_FIELDS;
import static org.junit.Assert.*;

import java.security.PublicKey;
import java.util.*;

import org.acegisecurity.Authentication;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.junit.*;
import org.w3c.dom.Document;

import com.randomcoder.saml.SamlAssertion;
import com.randomcoder.test.TestObjectFactory;
import com.randomcoder.test.mock.cardspace.*;

public class CardSpaceAuthenticationProviderTest
{
	private SamlAssertion assertion;
	private PublicKey publicKey;
	private CardSpaceAuthenticationProvider provider;
	
	@Before
	public void setUp() throws Exception
	{
		Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_ALL_FIELDS);
		publicKey = TestObjectFactory.getPublicKey(doc);
		assertion = TestObjectFactory.getSamlAssertion(doc);		
		provider = new CardSpaceAuthenticationProvider();
		provider.setCardSpaceUserDetailsService(new CardSpaceUserDetailsServiceMock(false));
	}

	@After
	public void tearDown() throws Exception
	{
		assertion = null;
		publicKey = null;
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
		
		// null auth
		assertNull(provider.authenticate(null));
		
		// wrong token type
		assertNull(provider.authenticate(new UsernamePasswordAuthenticationToken("user", "pass")));
				
		// populate test objects
		CardSpaceCredentials creds = new CardSpaceCredentials(assertion, publicKey, new Date());		
		CardSpaceAuthenticationToken token = new CardSpaceAuthenticationToken(creds);
		
		// null user
		provider.setCardSpaceUserDetailsService(new CardSpaceUserDetailsServiceMock(true));
		assertNull(provider.authenticate(token));
		
		// success
		provider.setCardSpaceUserDetailsService(new CardSpaceUserDetailsServiceMock(false));
		Authentication auth = provider.authenticate(token);
		assertNotNull(auth);
		assertEquals(CardSpaceAuthenticationToken.class, auth.getClass());
	}

	@Test
	public void testSupports()
	{
		assertFalse(provider.supports(String.class));
		assertTrue(provider.supports(CardSpaceAuthenticationToken.class));
	}

}
