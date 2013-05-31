package org.randomcoder.user;

import static org.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_ALL_FIELDS;

import java.security.PublicKey;
import java.util.*;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import org.randomcoder.cardspace.*;
import org.randomcoder.saml.SamlAssertion;
import org.randomcoder.security.UnauthorizedException;
import org.randomcoder.security.cardspace.CardSpaceCredentials;
import org.randomcoder.test.TestObjectFactory;
import org.randomcoder.test.mock.dao.*;

@SuppressWarnings("javadoc")
public class UserBusinessImplTest extends TestCase
{
	private UserBusinessImpl userBusiness;
	private UserDaoMock userDao;
	private CardSpaceTokenDaoMock cardSpaceTokenDao;
	
	@Override
	public void setUp()
	{
		userBusiness = new UserBusinessImpl();
		userDao = new UserDaoMock();
		cardSpaceTokenDao = new CardSpaceTokenDaoMock();
		userBusiness.setUserDao(userDao);
		userBusiness.setCardSpaceTokenDao(cardSpaceTokenDao);
	}

	public void testChangePassword()
	{
		User user = new User();
		user.setUserName("test-change-password");
		user.setEnabled(true);
		user.setEmailAddress("test@example.com");
		user.setPassword(User.hashPassword("test-password"));
		
		userDao.create(user);
		
		userBusiness.changePassword("test-change-password", "test-new-password");
		
		User changed = userDao.findByUserName("test-change-password");
		assertNotNull("Null user", changed);
		assertEquals("Wrong password", User.hashPassword("test-new-password"), changed.getPassword());
	}

	public void testChangePasswordUserNotFound()
	{
		try
		{
			userBusiness.changePassword("bogus-user", "bogus-password");
			fail("UserNotFoundException expected");
		}
		catch (UserNotFoundException e)
		{
			// pass
		}
	}
	
	public void testCreateUser()
	{
		UserAddCommand cmd = new UserAddCommand();
		
		cmd.setUserName("test-create");
		cmd.setEmailAddress("test-create@example.com");
		cmd.setPassword("testCreate1");
		cmd.setPassword2("testCreate1");
		cmd.setEnabled(true);
		
		Role testRole = new Role();
		testRole.setId(1L);
		testRole.setName("test-role");
		testRole.setDescription("Test role");
		
		cmd.setRoles(new Role[] { testRole });
		
		userBusiness.createUser(cmd);
		
		User added = userDao.findByUserName("test-create");
		
		assertNotNull("Null user", added);
		assertEquals("Wrong username", "test-create", added.getUserName());
		assertEquals("Wrong email address", "test-create@example.com", added.getEmailAddress());
		assertEquals("Wrong password", User.hashPassword("testCreate1"), added.getPassword());
		assertEquals("Not enabled", true, added.isEnabled());
		assertNotNull("Null role list", added.getRoles());
		assertEquals("Wrong role count", 1, added.getRoles().size());
		assertEquals("Wrong role name", "test-role", added.getRoles().get(0).getName());
	}

	public void testCreateAccountByPassword()
	{
		AccountCreateCommand cmd = new AccountCreateCommand();
		cmd.setUserName("test-create");
		cmd.setEmailAddress("test-create@example.com");
		cmd.setPassword("testCreate1");
		cmd.setPassword2("testCreate1");
		cmd.setWebsite("http://www.example.com/");
		cmd.setFormType("PASS");
		cmd.setFormComplete(true);
		
		userBusiness.createAccount(cmd);
		
		User added = userDao.findByUserName("test-create");
		
		assertNotNull("Null user", added);
		assertEquals("Wrong username", "test-create", added.getUserName());
		assertEquals("Wrong email address", "test-create@example.com", added.getEmailAddress());
		assertEquals("Wrong password", User.hashPassword("testCreate1"), added.getPassword());
		assertEquals("Not enabled", true, added.isEnabled());
		assertNotNull("Null role list", added.getRoles());
		assertEquals("Wrong role count", 0, added.getRoles().size());
	}
	
	public void testCreateAccountByCardSpace()
	{
		CardSpaceTokenSpec spec = new CardSpaceTokenSpec("ppid", "issuerHash", new Date(new Date().getTime() + 600000));
		
		AccountCreateCommand cmd = new AccountCreateCommand();
		cmd.setUserName("test-create");
		cmd.setEmailAddress("test-create@example.com");
		cmd.setCardSpaceTokenSpec(spec);
		cmd.setWebsite("http://www.example.com/");
		cmd.setFormType("INFOCARD");
		cmd.setFormComplete(true);
		
		userBusiness.createAccount(cmd, spec);
		
		User added = userDao.findByUserName("test-create");
		
		assertNotNull("Null user", added);
		assertEquals("Wrong username", "test-create", added.getUserName());
		assertEquals("Wrong email address", "test-create@example.com", added.getEmailAddress());
		assertNull("Password exists", added.getPassword());
		assertEquals("Not enabled", true, added.isEnabled());
		assertNotNull("Null role list", added.getRoles());
		assertEquals("Wrong role count", 0, added.getRoles().size());
		
		CardSpaceToken token = cardSpaceTokenDao.findByPrivatePersonalIdentifier("ppid", "issuerHash");
		assertNotNull("Null token", token);
	}
	
	public void testUpdateUser()
	{
		User user = new User();
		user.setUserName("test-update-user");
		user.setEnabled(true);
		user.setEmailAddress("test-update@example.com");
		user.setPassword(User.hashPassword("testPassword1"));		
		user.setRoles(new ArrayList<Role> ());
		Long id = userDao.create(user);
		
		UserEditCommand cmd = new UserEditCommand();
		cmd.consume(user);
		cmd.setEmailAddress("test-update2@example.com");
		cmd.setPassword("testPassword2");
		cmd.setPassword2("testPassword2");
		userBusiness.updateUser(cmd, id);
		
		User updated = userDao.read(id);
		
		assertNotNull("Null user", updated);
		assertEquals("Wrong username", "test-update-user", updated.getUserName());
		assertEquals("Wrong email address", "test-update2@example.com", updated.getEmailAddress());
		assertEquals("Wrong password", User.hashPassword("testPassword2"), updated.getPassword());
		assertNotNull("Null role list", updated.getRoles());
		assertTrue("Not enabled", updated.isEnabled());				
		assertEquals("Wrong role count", 0, updated.getRoles().size());
	}

	public void testDeleteUser()
	{
		User user = new User();
		user.setUserName("test-delete-user");
		user.setEnabled(true);
		user.setEmailAddress("test-delete@example.com");
		user.setPassword(User.hashPassword("testPassword1"));		
		user.setRoles(new ArrayList<Role> ());
		Long id = userDao.create(user);
		
		User read = userDao.read(id);
		assertNotNull("Found pending deleted user", read);
				
		userBusiness.deleteUser(id);
		
		read = userDao.read(id);
		assertNull("Found deleted user", read);
	}
	
	public void testDeleteUserCardSpace()
	{		
		User user = new User();
		user.setUserName("test-delete-user");
		user.setEnabled(true);
		user.setEmailAddress("test-delete@example.com");
		user.setPassword(null);		
		user.setRoles(new ArrayList<Role> ());
		Long id = userDao.create(user);
		
		CardSpaceToken token = new CardSpaceToken();
		token.setCreationDate(new Date());
		token.setEmailAddress("test-delete@example.com");
		token.setIssuerHash("issuerHash");
		token.setPrivatePersonalIdentifier("ppid");
		token.setUser(user);
		cardSpaceTokenDao.create(token);
		
		assertNotNull("Found pending deleted token", cardSpaceTokenDao.findByPrivatePersonalIdentifier("ppid", "issuerHash"));
		
		User read = userDao.read(id);
		assertNotNull("Found pending deleted user", read);
				
		userBusiness.deleteUser(id);
		
		read = userDao.read(id);
		assertNull("Found deleted user", read);
		
		assertNull("Found deleted token", cardSpaceTokenDao.findByPrivatePersonalIdentifier("ppid", "issuerHash"));
	}

	public void testDeleteCardSpaceToken()
	{
		User user = new User();
		user.setUserName("test-delete-user");
		user.setEnabled(true);
		user.setEmailAddress("test-delete@example.com");
		user.setPassword(null);		
		user.setRoles(new ArrayList<Role> ());
		userDao.create(user);

		User user2 = new User();
		user2.setUserName("test-delete-user-2");
		user2.setEnabled(true);
		user2.setEmailAddress("test-delete@example.com");
		user2.setPassword(null);		
		user2.setRoles(new ArrayList<Role> ());
		userDao.create(user2);
		
		CardSpaceToken token = new CardSpaceToken();
		token.setCreationDate(new Date());
		token.setEmailAddress("test-delete@example.com");
		token.setIssuerHash("issuerHash");
		token.setPrivatePersonalIdentifier("ppid");
		token.setUser(user);
		Long id = cardSpaceTokenDao.create(token);

		// attempt deletion from non-existent user
		try
		{
			userBusiness.deleteCardSpaceToken("bogus", id);
			fail("Exception not thrown");
		}
		catch (UserNotFoundException e) {}		

		// attempt deletion of non-existent token
		try
		{
			userBusiness.deleteCardSpaceToken("test-delete-user", id + 1);
			fail("Exception not thrown");
		}
		catch (CardSpaceTokenNotFoundException e) {}		

		// attempt deletion of other user's token
		try
		{
			userBusiness.deleteCardSpaceToken("test-delete-user-2", id);
			fail("Exception not thrown");
		}
		catch (UnauthorizedException e) {}		
		
		// test normal case
		assertNotNull("Found pending deleted token", cardSpaceTokenDao.findByPrivatePersonalIdentifier("ppid", "issuerHash"));
		
		userBusiness.deleteCardSpaceToken("test-delete-user", id);
		
		assertNull("Found deleted token", cardSpaceTokenDao.findByPrivatePersonalIdentifier("ppid", "issuerHash"));
		
	}
	
	public void testLoadUserForEditing()
	{
		User user = new User();
		user.setUserName("test-load-user");
		user.setEnabled(true);
		user.setEmailAddress("test-load@example.com");
		user.setPassword(User.hashPassword("testPassword1"));		
		user.setRoles(new ArrayList<Role> ());
		Long id = userDao.create(user);
		
		UserEditCommand cmd = new UserEditCommand();
		
		userBusiness.loadUserForEditing(cmd, id);
		
		assertEquals("Wrong username", "test-load-user", cmd.getUserName());
		assertEquals("Wrong email address", "test-load@example.com", cmd.getEmailAddress());
		assertTrue("Not enabled", cmd.isEnabled());
		assertNotNull("Null role list", cmd.getRoles());
		assertTrue("Not enabled", cmd.isEnabled());				
		assertEquals("Wrong role count", 0, cmd.getRoles().length);
	}

	public void testLoadUserForEditingUserNotFound()
	{
		try
		{
			UserEditCommand cmd = new UserEditCommand();		
			userBusiness.loadUserForEditing(cmd, (long) -1);		
			fail("UserNotFoundException expected");
		}
		catch (UserNotFoundException e)
		{
			// pass
		}
	}
	
	public void testAssociateCardSpaceCredentials()
	throws Exception
	{
		User user = new User();
		user.setUserName("test-load-user");
		user.setEnabled(true);
		user.setEmailAddress("test-load@example.com");
		user.setPassword(User.hashPassword("testPassword1"));		
		user.setRoles(new ArrayList<Role> ());
		Long id = userDao.create(user);
		
		Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_ALL_FIELDS);
		SamlAssertion assertion = TestObjectFactory.getSamlAssertion(doc);
		PublicKey publicKey = TestObjectFactory.getPublicKey(doc);
		
		CardSpaceCredentials cred = new CardSpaceCredentials(assertion, publicKey, new Date());
		
		userBusiness.associateCardSpaceCredentials(id, cred);
		
		String ppid = cred.getPrivatePersonalIdentifier();
		String issuerHash = CardSpaceUtils.calculateIssuerHash(cred);
		CardSpaceToken token = cardSpaceTokenDao.findByPrivatePersonalIdentifier(ppid, issuerHash);
		
		assertNotNull("Token not found", token);
		assertEquals("Wrong user", user, token.getUser());
		
		// attempt to add a second time
		try
		{			
			userBusiness.associateCardSpaceCredentials(id, cred);
			fail("Exception not thrown");
		}
		catch (CardSpaceTokenExistsException e)
		{
			// pass
		}
	}
	
	public void testAuditUsernamePasswordLogin()
	{
		User user = new User();
		user.setUserName("test-audit-user");
		user.setEnabled(true);
		user.setEmailAddress("test-audit@example.com");
		user.setPassword(User.hashPassword("testPassword1"));		
		user.setRoles(new ArrayList<Role> ());
		Long id = userDao.create(user);
				
		userBusiness.auditUsernamePasswordLogin("test-audit-user");
		
		User loaded = userDao.read(id);
		assertNotNull("Missing last login date", loaded.getLastLoginDate());
	}
	
	public void testAuditUsernamePasswordLoginNullUser()
	{
		try
		{
			userBusiness.auditUsernamePasswordLogin(null);
			fail("Exception not thrown");
		}
		catch (UserNotFoundException e)
		{
			// pass
		}
	}
	
	public void testAuditCardSpaceLogin() throws Exception
	{
		User user = new User();
		user.setUserName("test-audit-user");
		user.setEnabled(true);
		user.setEmailAddress("test-audit@example.com");
		user.setPassword(User.hashPassword("testPassword1"));		
		user.setRoles(new ArrayList<Role> ());
		Long id = userDao.create(user);
		
		Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_ALL_FIELDS);
		SamlAssertion assertion = TestObjectFactory.getSamlAssertion(doc);
		PublicKey publicKey = TestObjectFactory.getPublicKey(doc);
		
		CardSpaceCredentials cred = new CardSpaceCredentials(assertion, publicKey, new Date());
		String ppid = cred.getPrivatePersonalIdentifier();
		String issuerHash = CardSpaceUtils.calculateIssuerHash(cred);
		
		userBusiness.associateCardSpaceCredentials(id, cred);
		
		userBusiness.auditCardSpaceLogin(cred);
		
		
		CardSpaceToken loadedToken = cardSpaceTokenDao.findByPrivatePersonalIdentifier(ppid, issuerHash);		
		assertNotNull("Null last login on token", loadedToken.getLastLoginDate());
		
		User loadedUser = userDao.read(id);
		assertNotNull("Null last login on user", loadedUser.getLastLoginDate());
	}
	
	public void testAuditCardSpaceLoginNullToken() throws Exception
	{
		Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_ALL_FIELDS);
		SamlAssertion assertion = TestObjectFactory.getSamlAssertion(doc);
		PublicKey publicKey = TestObjectFactory.getPublicKey(doc);
		
		CardSpaceCredentials cred = new CardSpaceCredentials(assertion, publicKey, new Date());
		
		try
		{
			userBusiness.auditCardSpaceLogin(cred);
			fail("Exception not thrown");
		}
		catch (CardSpaceTokenNotFoundException e)
		{
			// pass
		}
	}
	
	
	@Override
	public void tearDown()
	{
		userDao = null;
		cardSpaceTokenDao = null;
		userBusiness = null;
	}
}
