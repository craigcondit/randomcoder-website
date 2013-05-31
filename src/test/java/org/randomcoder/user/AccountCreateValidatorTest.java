package org.randomcoder.user;

import static org.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_ALL_FIELDS;

import java.security.PublicKey;
import java.util.*;

import junit.framework.TestCase;

import org.springframework.validation.*;
import org.w3c.dom.Document;

import org.randomcoder.cardspace.*;
import org.randomcoder.saml.SamlAssertion;
import org.randomcoder.security.cardspace.CardSpaceCredentials;
import org.randomcoder.test.TestObjectFactory;
import org.randomcoder.test.mock.dao.*;

@SuppressWarnings("javadoc")
public class AccountCreateValidatorTest extends TestCase
{
	private UserDaoMock userDao;
	private CardSpaceTokenDaoMock cardSpaceTokenDao;
	
	private AccountCreateValidator validator;

	@Override
	protected void setUp() throws Exception
	{
		userDao = new UserDaoMock();
		cardSpaceTokenDao = new CardSpaceTokenDaoMock();		
		validator = new AccountCreateValidator();
		validator.setMinimumUsernameLength(6);
		validator.setMinimumPasswordLength(6);
		validator.setCardSpaceTokenDao(cardSpaceTokenDao);
		validator.setUserDao(userDao);
	}

	@Override
	protected void tearDown() throws Exception
	{
		validator = null;
		cardSpaceTokenDao = null;
		userDao = null;
	}

	public void testSupports()
	{
		assertTrue("Validator doesn't support command class", validator.supports(AccountCreateCommand.class));
	}

	public void testValidate()
	{
		BindException errors;
		
		// setup
		AccountCreateCommand command = new AccountCreateCommand();
		
		User user = new User();
		user.setUserName("existing-user");
		user.setEmailAddress("existing@example.com");
		user.setPassword(User.hashPassword("Password1"));
		user.setRoles(new ArrayList<Role>());
		user.setEnabled(true);		
		userDao.create(user);
		
		// null command
		errors = new BindException(command, "command");
		validator.validate(null, errors);
		assertEquals("Wrong number of errors occurred", 1, errors.getErrorCount());
		
		// missing form type
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong number of errors occurred", 1, errors.getErrorCount());
		
		// empty form
		command.setFormType("PASS");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong number of errors occurred", 4, errors.getErrorCount());
		assertEquals("Wrong error count for userName", 1, errors.getFieldErrorCount("userName"));		
		assertEquals("Wrong error count for emailAddress", 1, errors.getFieldErrorCount("emailAddress"));
		assertEquals("Wrong error count for password", 1, errors.getFieldErrorCount("password"));
		assertEquals("Wrong error count for password2", 1, errors.getFieldErrorCount("password2"));
		
		// username too short
		command.setUserName("short");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for userName", 1, errors.getFieldErrorCount("userName"));
		
		// username exists
		command.setUserName("existing-user");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for userName", 1, errors.getFieldErrorCount("userName"));
		
		// username valid
		command.setUserName("new-user");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for userName", 0, errors.getFieldErrorCount("userName"));
		
		// password too short (and no match with password2)
		command.setPassword("short");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for password", 1, errors.getFieldErrorCount("password"));
		assertEquals("Wrong error count for password2", 1, errors.getFieldErrorCount("password2"));
		
		// passwords valid
		command.setPassword("testPassword");
		command.setPassword2("testPassword");		
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for password", 0, errors.getFieldErrorCount("password"));
		assertEquals("Wrong error count for password2", 0, errors.getFieldErrorCount("password2"));
		
		// invalid email address
		command.setEmailAddress("bogus");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for emailAddress", 1, errors.getFieldErrorCount("emailAddress"));
		
		// valid email address
		command.setEmailAddress("test@example.com");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for emailAddress", 0, errors.getFieldErrorCount("emailAddress"));
		
		// invalid website
		command.setWebsite("bogus");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for website", 1, errors.getFieldErrorCount("website"));
		
		// valid website
		command.setWebsite("http://www.exmaple.com/");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for website", 0, errors.getFieldErrorCount("website"));
		
		assertEquals("Wrong number of errors occurred", 0, errors.getErrorCount());		
	}
	
	public void testValidateCardSpacePass1() throws Exception
	{
		BindException errors;
		
		// setup
		AccountCreateCommand command = new AccountCreateCommand();
		
		User user = new User();
		user.setUserName("existing-user");
		user.setEmailAddress("existing@example.com");
		user.setPassword(User.hashPassword("Password1"));
		user.setRoles(new ArrayList<Role>());
		user.setEnabled(true);		
		userDao.create(user);
		
		// empty form
		command.setFormType("INFOCARD");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong number of errors occurred", 1, errors.getErrorCount());
		assertEquals("Wrong error count for xmlToken", 1, errors.getFieldErrorCount("xmlToken"));
		
		// valid credentials
		Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_ALL_FIELDS);
		SamlAssertion assertion = TestObjectFactory.getSamlAssertion(doc);
		PublicKey publicKey = TestObjectFactory.getPublicKey(doc);		
		MockCardSpaceCredentials cred = new MockCardSpaceCredentials(assertion, publicKey, new Date());
		command.setXmlToken(cred);		
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong number of errors occurred", 0, errors.getErrorCount());
		assertEquals("Wrong error count for xmlToken", 0, errors.getFieldErrorCount("xmlToken"));
		
		// missing ppid
		String ppid = cred.getPrivatePersonalIdentifier();
		cred.setPrivatePersonalIdentifier(null);
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong number of errors occurred", 1, errors.getErrorCount());
		assertEquals("Wrong error count for xmlToken", 1, errors.getFieldErrorCount("xmlToken"));
		
		// missing email address
		cred.setPrivatePersonalIdentifier(ppid);
		String emailAddress = cred.getEmailAddress();
		cred.setEmailAddress(null);
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong number of errors occurred", 1, errors.getErrorCount());
		assertEquals("Wrong error count for xmlToken", 1, errors.getFieldErrorCount("xmlToken"));
		
		// existing
		cred.setEmailAddress(emailAddress);
		CardSpaceToken token = new CardSpaceToken();
		token.setCreationDate(new Date());
		token.setEmailAddress("test@example.com");
		token.setIssuerHash(CardSpaceUtils.calculateIssuerHash(cred));
		token.setPrivatePersonalIdentifier(cred.getPrivatePersonalIdentifier());
		token.setUser(user);
		cardSpaceTokenDao.create(token);
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong number of errors occurred", 1, errors.getErrorCount());
		assertEquals("Wrong error count for xmlToken", 1, errors.getFieldErrorCount("xmlToken"));
		
	}	

	public void testValidateCardSpacePass2() throws Exception
	{
		BindException errors;
		CardSpaceTokenSpec spec;
		
		// setup
		AccountCreateCommand command = new AccountCreateCommand();
		
		User user = new User();
		user.setUserName("existing-user");
		user.setEmailAddress("existing@example.com");
		user.setPassword(User.hashPassword("Password1"));
		user.setRoles(new ArrayList<Role>());
		user.setEnabled(true);		
		userDao.create(user);
		
		command.setFormType("INFOCARD");
		
		// expired token
		spec = new CardSpaceTokenSpec("ppid", "issuerHash", new Date(new Date().getTime() - 60000));
		command.setCardSpaceTokenSpec(spec);
		command.setFormComplete(true);
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong number of errors occurred", 1, errors.getErrorCount());
		assertEquals("Wrong error count for xmlToken", 1, errors.getFieldErrorCount("xmlToken"));
		
		// current token, missing form data
		spec = new CardSpaceTokenSpec("ppid", "issuerHash", new Date(new Date().getTime() + 60000));
		command.setCardSpaceTokenSpec(spec);
		command.setFormComplete(true);
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong number of errors occurred", 2, errors.getErrorCount());
		assertEquals("Wrong error count for xmlToken", 0, errors.getFieldErrorCount("xmlToken"));
		
		// username too short
		command.setUserName("short");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for userName", 1, errors.getFieldErrorCount("userName"));
		
		// username exists
		command.setUserName("existing-user");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for userName", 1, errors.getFieldErrorCount("userName"));
		
		// username valid
		command.setUserName("new-user");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for userName", 0, errors.getFieldErrorCount("userName"));
		
		// invalid email address
		command.setEmailAddress("bogus");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for emailAddress", 1, errors.getFieldErrorCount("emailAddress"));
		
		// valid email address
		command.setEmailAddress("test@example.com");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for emailAddress", 0, errors.getFieldErrorCount("emailAddress"));
		
		// invalid website
		command.setWebsite("bogus");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for website", 1, errors.getFieldErrorCount("website"));
		
		// valid website
		command.setWebsite("http://www.exmaple.com/");
		errors = new BindException(command, "command");
		validator.validate(command, errors);
		assertEquals("Wrong error count for website", 0, errors.getFieldErrorCount("website"));
		
		assertEquals("Wrong number of errors occurred", 0, errors.getErrorCount());		
	}
	
	static class MockCardSpaceCredentials extends CardSpaceCredentials
	{
		private static final long serialVersionUID = -8609294972545228453L;
		private String emailAddress;		
		private String privatePersonalIdentifier;

		public MockCardSpaceCredentials(SamlAssertion assertion, PublicKey publicKey, Date receivedInstant)
		{			
			super(assertion, publicKey, receivedInstant);
			emailAddress = super.getEmailAddress();
			privatePersonalIdentifier = super.getPrivatePersonalIdentifier();
		}
		
		public void setEmailAddress(String emailAddress)
		{
			this.emailAddress = emailAddress;
		}

		@Override
		public String getEmailAddress()
		{
			return emailAddress;
		}

		public void setPrivatePersonalIdentifier(String privatePersonalIdentifier)
		{
			this.privatePersonalIdentifier = privatePersonalIdentifier;
		}
		
		@Override
		public String getPrivatePersonalIdentifier()
		{
			return privatePersonalIdentifier;
		}
	}
}
