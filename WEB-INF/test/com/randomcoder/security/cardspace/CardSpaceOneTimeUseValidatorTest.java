package com.randomcoder.security.cardspace;

import static com.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_TEST;

import java.security.PublicKey;
import java.util.Date;

import org.acegisecurity.BadCredentialsException;
import org.junit.*;
import org.w3c.dom.Document;

import com.randomcoder.saml.SamlAssertion;
import com.randomcoder.test.TestObjectFactory;
import com.randomcoder.test.mock.dao.CardSpaceSeenTokenDaoMock;

public class CardSpaceOneTimeUseValidatorTest
{
	private CardSpaceOneTimeUseValidator validator;
	private SamlAssertion assertion;
	private PublicKey publicKey;
	private CardSpaceSeenTokenDao cardSpaceSeenTokenDao;
	
	@Before
	public void setUp() throws Exception
	{
		Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_TEST);
		publicKey = TestObjectFactory.getPublicKey(doc);
		assertion = TestObjectFactory.getSamlAssertion(doc);
		cardSpaceSeenTokenDao = new CardSpaceSeenTokenDaoMock();
		validator = new CardSpaceOneTimeUseValidator();
		validator.setCardSpaceSeenTokenDao(cardSpaceSeenTokenDao);
	}

	@After
	public void tearDown() throws Exception
	{
		cardSpaceSeenTokenDao = null;
		validator = null;
		assertion = null;
		publicKey = null;
	}

	@Test
	public void testValidateSuccess()
	{
		CardSpaceCredentials credentials = new CardSpaceCredentials(assertion, publicKey, new Date());		
		validator.validate(credentials);
	}

	@Test(expected=BadCredentialsException.class)
	public void testValidateDuplicate()
	{
		CardSpaceCredentials credentials = new CardSpaceCredentials(assertion, publicKey, new Date());		
		validator.validate(credentials);
		validator.validate(credentials);
	}
}