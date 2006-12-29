package com.randomcoder.security.cardspace;

import static com.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_TEST;

import java.security.PublicKey;
import java.util.Date;

import org.acegisecurity.BadCredentialsException;
import org.junit.*;
import org.w3c.dom.Document;

import com.randomcoder.saml.SamlAssertion;
import com.randomcoder.test.TestObjectFactory;

public class CardSpaceMaximumAgeValidatorTest
{
	private CardSpaceMaximumAgeValidator validator;
	private SamlAssertion assertion;
	private PublicKey publicKey;	
	
	@Before
	public void setUp() throws Exception
	{
		Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_TEST);
		publicKey = TestObjectFactory.getPublicKey(doc);
		assertion = TestObjectFactory.getSamlAssertion(doc);		
		validator = new CardSpaceMaximumAgeValidator();
	}

	@After
	public void tearDown() throws Exception
	{
		validator = null;
		assertion = null;
		publicKey = null;
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSetClockSkewNegative()
	{
		validator.setClockSkew(-1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSetMaximumTokenAgeNegative()
	{
		validator.setMaximumTokenAge(-1);
	}

	@Test(expected=BadCredentialsException.class)
	public void testValidateTooEarly()
	{
		Date issueInstant = assertion.getIssueInstant();
		Date received = new Date(issueInstant.getTime() - 60001);
		
		CardSpaceCredentials credentials = new CardSpaceCredentials(assertion, publicKey, received);
		
		validator.setClockSkew(60);
		validator.setMaximumTokenAge(300);
		validator.validate(credentials);
	}

	@Test
	public void testValidateAlmostTooEarly()
	{
		Date issueInstant = assertion.getIssueInstant();
		Date received = new Date(issueInstant.getTime() - 60000);
		
		CardSpaceCredentials credentials = new CardSpaceCredentials(assertion, publicKey, received);
		
		validator.setClockSkew(60);
		validator.setMaximumTokenAge(300);
		validator.validate(credentials);
	}

	@Test(expected=BadCredentialsException.class)
	public void testValidateTooLate()
	{
		Date issueInstant = assertion.getIssueInstant();
		Date received = new Date(issueInstant.getTime() + 360000);
		
		CardSpaceCredentials credentials = new CardSpaceCredentials(assertion, publicKey, received);
		
		validator.setClockSkew(60);
		validator.setMaximumTokenAge(300);
		validator.validate(credentials);
	}

	@Test
	public void testValidateAlmostTooLate()
	{
		Date issueInstant = assertion.getIssueInstant();
		Date received = new Date(issueInstant.getTime() + 359999);
		
		CardSpaceCredentials credentials = new CardSpaceCredentials(assertion, publicKey, received);
		
		validator.setClockSkew(60);
		validator.setMaximumTokenAge(300);
		validator.validate(credentials);
	}
}