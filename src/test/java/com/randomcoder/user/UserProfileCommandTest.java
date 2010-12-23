package com.randomcoder.user;

import static com.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_ALL_FIELDS;

import java.security.PublicKey;
import java.util.Date;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import com.randomcoder.saml.SamlAssertion;
import com.randomcoder.security.cardspace.CardSpaceCredentials;
import com.randomcoder.test.TestObjectFactory;

public class UserProfileCommandTest extends TestCase
{
	private UserProfileCommand command;
	@Override
	protected void setUp() throws Exception
	{
		command = new UserProfileCommand();
	}

	@Override
	protected void tearDown() throws Exception
	{
		command = null;
	}

	public void testGetFormType()
	{
		command.setFormType("PREFS");
		assertEquals("PREFS", command.getFormType());
	}

	public void testGetXmlToken() throws Exception
	{
		Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_ALL_FIELDS);
		SamlAssertion assertion = TestObjectFactory.getSamlAssertion(doc);
		PublicKey publicKey = TestObjectFactory.getPublicKey(doc);
		Date now = new Date();
		
		CardSpaceCredentials cred = new CardSpaceCredentials(assertion, publicKey, now);
		
		command.setXmlToken(cred);
		assertSame(cred, command.getXmlToken());
	}

	public void testGetEmailAddress()
	{
		command.setEmailAddress("test@example.com");
		assertEquals("test@example.com", command.getEmailAddress());
	}

	public void testGetWebsite()
	{
		command.setWebsite("http://test.com/");
		assertEquals("http://test.com/", command.getWebsite());
	}

	public void testProduce()
	{
		command.setEmailAddress("test@example.com");
		command.setFormType("PREFS");
		command.setWebsite("http://test.com/");
		
		User user = new User();
		command.produce(user);
		
		assertEquals("Wrong website", "http://test.com/", user.getWebsite());
		assertEquals("Wrong email address", "test@example.com", user.getEmailAddress());
	}
}