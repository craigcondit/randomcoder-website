package com.randomcoder.security.cardspace;

import static com.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_TEST;

import java.security.PublicKey;
import java.util.Date;

import org.acegisecurity.BadCredentialsException;
import org.junit.*;
import org.w3c.dom.*;

import com.randomcoder.saml.*;
import com.randomcoder.test.TestObjectFactory;

public class CardSpaceDateConstraintValidatorTest
{
	private CardSpaceDateConstraintValidator validator;
	private PublicKey publicKey;
	private SamlAssertion assertionNormal;
	private SamlAssertion assertionBoundsReversed;
	
	@Before
	public void setUp() throws Exception
	{
		Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_TEST);
		publicKey = TestObjectFactory.getPublicKey(doc);
		
		assertionNormal = TestObjectFactory.getSamlAssertion(doc);
		
		// swap notBefore and notOnOrAfter
		Element conditions = (Element) doc.getElementsByTagNameNS(SamlUtils.SAML_10_NS, "Conditions").item(0);
		String notBefore = conditions.getAttribute("NotBefore");
		String notOnOrAfter = conditions.getAttribute("NotOnOrAfter");
		conditions.setAttribute("NotBefore", notOnOrAfter);
		conditions.setAttribute("NotOnOrAfter", notBefore);
		
		assertionBoundsReversed = TestObjectFactory.getSamlAssertion(doc);
		
		validator = new CardSpaceDateConstraintValidator();		
	}

	@After
	public void tearDown() throws Exception
	{
		validator = null;
		publicKey = null;
		assertionNormal = null;
	}

	@Test
	public void testValidateSuccess()
	{
		Date notBefore = assertionNormal.getNotBefore();
		Date received = new Date(notBefore.getTime() + 1);
		
		CardSpaceCredentials credentials = new CardSpaceCredentials(assertionNormal, publicKey, received);
		
		validator.setClockSkew(60);
		validator.validate(credentials);
	}

	@Test(expected=BadCredentialsException.class)
	public void testValidateBoundsReversed()
	{
		Date notBefore = assertionBoundsReversed.getNotBefore();
		Date received = new Date(notBefore.getTime() + 1);
		
		CardSpaceCredentials credentials = new CardSpaceCredentials(assertionBoundsReversed, publicKey, received);
		
		validator.setClockSkew(60);
		validator.validate(credentials);
	}

	@Test(expected=BadCredentialsException.class)
	public void testValidateTooEarly()
	{
		Date notBefore = assertionNormal.getNotBefore();
		Date received = new Date(notBefore.getTime() - 60001);
		
		CardSpaceCredentials credentials = new CardSpaceCredentials(assertionNormal, publicKey, received);
		
		validator.setClockSkew(60);
		validator.validate(credentials);
	}

	@Test
	public void testValidateEarlyClockSkew()
	{
		Date notBefore = assertionNormal.getNotBefore();
		Date received = new Date(notBefore.getTime() - 60000);
		
		CardSpaceCredentials credentials = new CardSpaceCredentials(assertionNormal, publicKey, received);
		
		validator.setClockSkew(60);
		validator.validate(credentials);
	}

	@Test
	public void testValidateLateClockSkew()
	{
		Date notOnOrAfter = assertionNormal.getNotOnOrAfter();
		Date received = new Date(notOnOrAfter.getTime() + 59999);
		
		CardSpaceCredentials credentials = new CardSpaceCredentials(assertionNormal, publicKey, received);
		
		validator.setClockSkew(60);
		validator.validate(credentials);
	}

	@Test(expected=BadCredentialsException.class)
	public void testValidateTooLate()
	{
		Date notOnOrAfter = assertionNormal.getNotOnOrAfter();
		Date received = new Date(notOnOrAfter.getTime() + 60000);
		
		CardSpaceCredentials credentials = new CardSpaceCredentials(assertionNormal, publicKey, received);
		
		validator.setClockSkew(60);
		validator.validate(credentials);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetClockSkewNegative()
	{
		validator.setClockSkew(-1);
	}
	
}
