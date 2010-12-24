package com.randomcoder.security.cardspace;

import static com.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_TEST;

import java.security.PublicKey;
import java.util.Date;

import junit.framework.TestCase;

import org.acegisecurity.BadCredentialsException;
import org.w3c.dom.Document;

import com.randomcoder.saml.SamlAssertion;
import com.randomcoder.test.TestObjectFactory;

public class CardSpaceMaximumAgeValidatorTest extends TestCase
{
	private CardSpaceMaximumAgeValidator validator;
	private SamlAssertion assertion;
	private PublicKey publicKey;	
	
	@Override
	public void setUp() throws Exception
	{
		Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_TEST);
		publicKey = TestObjectFactory.getPublicKey(doc);
		assertion = TestObjectFactory.getSamlAssertion(doc);		
		validator = new CardSpaceMaximumAgeValidator();
	}

	@Override
	public void tearDown() throws Exception
	{
		validator = null;
		assertion = null;
		publicKey = null;
	}

	public void testSetClockSkewNegative()
	{
		try
		{
			validator.setClockSkew(-1);
			fail("IllegalArgumentException expected");
		}
		catch (IllegalArgumentException e)
		{
			// pass
		}
	}

	public void testSetMaximumTokenAgeNegative()
	{
		try
		{
			validator.setMaximumTokenAge(-1);
			fail("IllegalArgumentException expected");
		}
		catch (IllegalArgumentException e)
		{
			// pass
		}
	}

	public void testValidateTooEarly()
	{
		try
		{
			Date issueInstant = assertion.getIssueInstant();
			Date received = new Date(issueInstant.getTime() - 60001);
			
			CardSpaceCredentials credentials = new CardSpaceCredentials(assertion, publicKey, received);
			
			validator.setClockSkew(60);
			validator.setMaximumTokenAge(300);
			validator.validate(credentials);
			fail("BadCredentialsException expected");
		}
		catch (BadCredentialsException e)
		{
			// pass
		}
	}

	public void testValidateAlmostTooEarly()
	{
		Date issueInstant = assertion.getIssueInstant();
		Date received = new Date(issueInstant.getTime() - 60000);
		
		CardSpaceCredentials credentials = new CardSpaceCredentials(assertion, publicKey, received);
		
		validator.setClockSkew(60);
		validator.setMaximumTokenAge(300);
		validator.validate(credentials);
	}

	public void testValidateTooLate()
	{
		try
		{
			Date issueInstant = assertion.getIssueInstant();
			Date received = new Date(issueInstant.getTime() + 360000);
			
			CardSpaceCredentials credentials = new CardSpaceCredentials(assertion, publicKey, received);
			
			validator.setClockSkew(60);
			validator.setMaximumTokenAge(300);
			validator.validate(credentials);
			fail("BadCredentialsException expected");
		}
		catch (BadCredentialsException e)
		{
			// pass
		}
	}

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