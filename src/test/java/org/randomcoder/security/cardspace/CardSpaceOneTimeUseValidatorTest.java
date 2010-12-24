package org.randomcoder.security.cardspace;

import static org.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_TEST;

import java.security.PublicKey;
import java.util.Date;

import junit.framework.TestCase;

import org.acegisecurity.BadCredentialsException;
import org.w3c.dom.Document;

import org.randomcoder.saml.SamlAssertion;
import org.randomcoder.test.TestObjectFactory;
import org.randomcoder.test.mock.dao.CardSpaceSeenTokenDaoMock;

public class CardSpaceOneTimeUseValidatorTest extends TestCase
{
	private CardSpaceOneTimeUseValidator validator;
	private SamlAssertion assertion;
	private PublicKey publicKey;
	private CardSpaceSeenTokenDao cardSpaceSeenTokenDao;
	
	@Override
	public void setUp() throws Exception
	{
		Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_TEST);
		publicKey = TestObjectFactory.getPublicKey(doc);
		assertion = TestObjectFactory.getSamlAssertion(doc);
		cardSpaceSeenTokenDao = new CardSpaceSeenTokenDaoMock();
		validator = new CardSpaceOneTimeUseValidator();
		validator.setCardSpaceSeenTokenDao(cardSpaceSeenTokenDao);
	}

	@Override
	public void tearDown() throws Exception
	{
		cardSpaceSeenTokenDao = null;
		validator = null;
		assertion = null;
		publicKey = null;
	}

	public void testValidateSuccess()
	{
		CardSpaceCredentials credentials = new CardSpaceCredentials(assertion, publicKey, new Date());		
		validator.validate(credentials);
	}

	public void testValidateDuplicate()
	{
		try
		{
			CardSpaceCredentials credentials = new CardSpaceCredentials(assertion, publicKey, new Date());		
			validator.validate(credentials);
			validator.validate(credentials);
			fail("BadCredentialsException expected");
		}
		catch (BadCredentialsException e)
		{
			// pass
		}
	}
}
