package org.randomcoder.security.userdetails;

import static org.randomcoder.test.TestObjectFactory.*;

import java.security.PublicKey;
import java.util.*;

import junit.framework.TestCase;

import org.acegisecurity.*;
import org.acegisecurity.userdetails.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.*;

import org.randomcoder.saml.*;
import org.randomcoder.security.cardspace.*;
import org.randomcoder.test.TestObjectFactory;
import org.randomcoder.test.mock.dao.*;
import org.randomcoder.user.*;
import org.randomcoder.user.User;

@SuppressWarnings("javadoc")
public class UserDetailsServiceImplTest extends TestCase
{
	private UserDetailsServiceImpl svc = null;
	private CardSpaceTokenDaoMock cardSpaceTokenDao = null;
	private UserDaoMock userDao = null;
	private RoleDaoMock roleDao = null;
		
	private SamlAssertion existingUserAssertion = null;
	private SamlAssertion missingUserAssertion = null;
	private SamlAssertion missingPpidAssertion = null;
	
	private PublicKey existingUserKey = null;
	private PublicKey missingUserKey = null;
	private PublicKey missingPpidKey = null;
	
	private CardSpaceCredentials existingUserCredentials = null;
	private CardSpaceCredentials missingUserCredentials = null;
	private CardSpaceCredentials missingPpidCredentials = null;
		
	@Override
	public void setUp() throws Exception
	{
		cardSpaceTokenDao = new CardSpaceTokenDaoMock();
		userDao = new UserDaoMock();
		roleDao = new RoleDaoMock();
		svc = new UserDetailsServiceImpl();
		svc.setCardSpaceTokenDao(cardSpaceTokenDao);
		svc.setUserDao(userDao);
		svc.setDebug(false);
		
		{
			Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_TEST);
			missingPpidKey = existingUserKey = TestObjectFactory.getPublicKey(doc);
			existingUserAssertion = TestObjectFactory.getSamlAssertion(doc);
			
			Element assertionEl = SamlUtils.findFirstSamlAssertion(doc);			
			NodeList atts = assertionEl.getElementsByTagNameNS(SamlUtils.SAML_10_NS, "Attribute");
			for (int i = 0; i < atts.getLength(); i++)
			{
				Element att = (Element) atts.item(i);
				if ("privatepersonalidentifier".equals(att.getAttribute("AttributeName")))
					att.getParentNode().removeChild(att);
			}
			missingPpidAssertion = new SamlAssertion(assertionEl);
			existingUserCredentials = new CardSpaceCredentials(existingUserAssertion, existingUserKey, new Date());
			missingPpidCredentials = new CardSpaceCredentials(missingPpidAssertion, missingPpidKey, new Date());
		}
		
		{
			Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_TEST_2);
			missingUserKey = TestObjectFactory.getPublicKey(doc);
			missingUserAssertion = TestObjectFactory.getSamlAssertion(doc);
			missingUserCredentials = new CardSpaceCredentials(missingUserAssertion, missingUserKey, new Date());
		}
		
		{
			Role role = new Role();
			role.setName("ROLE_TEST");
			role.setDescription("Test role");	
			roleDao.mockCreate(role);
			
			List<Role> roles = new ArrayList<Role>();
			roles.add(role);
			
			User user = new User();
			user.setUserName("test");
			user.setEnabled(true);
			user.setPassword(User.hashPassword("Password1"));
			user.setEmailAddress("test@example.com");
			user.setRoles(roles);
			userDao.create(user);
			
			CardSpaceToken token = new CardSpaceToken();
			token.setPrivatePersonalIdentifier(existingUserCredentials.getPrivatePersonalIdentifier());
			token.setIssuerHash(DigestUtils.shaHex(existingUserCredentials.getIssuerPublicKey()));
			token.setEmailAddress("test@example.com");
			token.setCreationDate(new Date());
			token.setUser(user);
			cardSpaceTokenDao.create(token);
		}
		
		{
			User user = new User();
			user.setUserName("test-no-password");
			user.setEnabled(true);
			user.setEmailAddress("test-no-password@example.com");
			user.setRoles(new ArrayList<Role> ());
			userDao.create(user);
		}
	}

	@Override
	public void tearDown() throws Exception
	{
		svc = null;
		cardSpaceTokenDao = null;
		userDao = null;
		roleDao = null;
		existingUserAssertion = null;
		missingUserAssertion = null;
		missingPpidAssertion = null;
		existingUserKey = null;
		missingUserKey = null;
		missingPpidKey = null;
		existingUserCredentials = null;
		missingUserCredentials = null;
		missingPpidCredentials = null;
	}

	public void testLoadUserByUsername()
	{
		UserDetails details = svc.loadUserByUsername("test");
		assertNotNull(details);
		assertEquals("test", details.getUsername());
		
		assertEquals(User.hashPassword("Password1"), details.getPassword());
		
		GrantedAuthority[] authorities =  details.getAuthorities();
		assertNotNull(authorities);
		assertEquals(1, authorities.length);
		assertEquals("ROLE_TEST", authorities[0].getAuthority());
		
		assertTrue(details.isAccountNonExpired());
		assertTrue(details.isAccountNonLocked());
		assertTrue(details.isCredentialsNonExpired());
		assertTrue(details.isEnabled());		
	}

	public void testLoadUserByUsernameDebug()
	{
		svc.setDebug(true);
		testLoadUserByUsername();
	}
	
	public void testLoadUserByUsernameNotFound() throws Exception
	{
		try
		{
			svc.loadUserByUsername("bogus");
			fail("UsernameNotFoundException expected");
		}
		catch (UsernameNotFoundException e)
		{
			// pass
		}
	}

	public void testLoadUserByUsernameNoPassword() throws Exception
	{
		try
		{
			svc.loadUserByUsername("test-no-password");
			fail("UsernameNotFoundException expected");
		}
		catch (UsernameNotFoundException e)
		{
			// pass
		}
	}
	
	public void testLoadUserByCardSpaceCredentials()
	{
		UserDetails details = svc.loadUserByCardSpaceCredentials(existingUserCredentials);
		assertNotNull(details);
		assertEquals("test", details.getUsername());
	}

	public void testLoadUserByCardSpaceCredentialsNotFound()
	{
		try
		{
			svc.loadUserByCardSpaceCredentials(missingUserCredentials);
			fail("BadCredentialsException expected");
		}
		catch (BadCredentialsException e)
		{
			// pass
		}
	}

	public void testLoadUserByCardSpaceCredentialsMissingPpid()
	{
		try
		{
			svc.loadUserByCardSpaceCredentials(missingPpidCredentials);
			fail("InvalidCredentialsException expected");
		}
		catch (InvalidCredentialsException e)
		{
			// pass
		}
	}	
}
