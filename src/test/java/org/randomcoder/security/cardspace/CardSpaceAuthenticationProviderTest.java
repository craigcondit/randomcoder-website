package org.randomcoder.security.cardspace;

import static org.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_ALL_FIELDS;

import java.security.PublicKey;
import java.util.*;

import junit.framework.TestCase;

import org.acegisecurity.Authentication;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.w3c.dom.Document;

import org.randomcoder.saml.SamlAssertion;
import org.randomcoder.test.TestObjectFactory;
import org.randomcoder.test.mock.cardspace.*;

@SuppressWarnings("javadoc")
public class CardSpaceAuthenticationProviderTest extends TestCase
{
	private SamlAssertion assertion;
	private PublicKey publicKey;
	private CardSpaceAuthenticationProvider provider;
	
	@Override
	public void setUp() throws Exception
	{
		Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_ALL_FIELDS);
		publicKey = TestObjectFactory.getPublicKey(doc);
		assertion = TestObjectFactory.getSamlAssertion(doc);		
		provider = new CardSpaceAuthenticationProvider();
		provider.setCardSpaceUserDetailsService(new CardSpaceUserDetailsServiceMock(false));
	}

	@Override
	public void tearDown() throws Exception
	{
		assertion = null;
		publicKey = null;
		provider = null;
	}

	public void testSetValidators()
	{
		List<CardSpaceCredentialsValidator> validators = new ArrayList<CardSpaceCredentialsValidator>();
		validators.add(new CardSpaceCredentialsValidatorMock());
		provider.setValidators(validators);
		provider.afterPropertiesSet();
	}

	public void testSetValidator()
	{
		provider.setValidator(new CardSpaceCredentialsValidatorMock());
		provider.afterPropertiesSet();
	}
	
	public void testAfterPropertiesSetNoValidators()
	{
		try
		{
			provider.afterPropertiesSet();
			fail("IllegalArgumentException expected");
		}
		catch (IllegalArgumentException e)
		{
			// pass
		}
	}

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

	public void testSupports()
	{
		assertFalse(provider.supports(String.class));
		assertTrue(provider.supports(CardSpaceAuthenticationToken.class));
	}
}
