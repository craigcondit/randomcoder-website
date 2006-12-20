package com.randomcoder.security.userdetails.test;

import static org.junit.Assert.*;

import java.security.*;
import java.util.*;

import org.acegisecurity.*;
import org.acegisecurity.userdetails.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.*;
import org.springframework.core.io.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import com.randomcoder.crypto.*;
import com.randomcoder.saml.*;
import com.randomcoder.security.cardspace.*;
import com.randomcoder.security.userdetails.UserDetailsServiceImpl;
import com.randomcoder.user.*;
import com.randomcoder.user.User;
import com.randomcoder.user.test.*;
import com.randomcoder.xml.XmlUtils;
import com.randomcoder.xml.security.XmlSecurityUtils;

public class UserDetailsServiceImplTest
{
	private static final String RES_ENCRYPTED = "/xmlsec/saml-encrypted.xml";
	private static final String RES_ENCRYPTED2 = "/xmlsec/test-encrypted.xml";
	private static final String RES_XMLSEC_PROPS = "/xmlsec/xmlsec.properties";
	
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
		
	@Before
	public void setUp() throws Exception
	{
		cardSpaceTokenDao = new CardSpaceTokenDaoMock();
		userDao = new UserDaoMock();
		roleDao = new RoleDaoMock();
		svc = new UserDetailsServiceImpl();
		svc.setCardSpaceTokenDao(cardSpaceTokenDao);
		svc.setUserDao(userDao);
		svc.setDebug(false);
		
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream(RES_XMLSEC_PROPS));

		KeystoreCertificateFactoryBean keystoreFactory = new KeystoreCertificateFactoryBean();
		
		Resource keystoreLocation = new ClassPathResource(properties.getProperty("keystore.resource"));
		
		keystoreFactory.setKeystoreLocation(keystoreLocation);
		keystoreFactory.setKeystoreType(properties.getProperty("keystore.type"));
		keystoreFactory.setKeystorePassword(properties.getProperty("keystore.password"));
		keystoreFactory.setCertificateAlias(properties.getProperty("certificate.alias"));
		keystoreFactory.setCertificatePassword(properties.getProperty("certificate.password"));
		keystoreFactory.afterPropertiesSet();
		CertificateContext certContext = (CertificateContext) keystoreFactory.getObject();
		
		PrivateKey serverPrivateKey = certContext.getPrivateKey();
		
		{
			Document doc = XmlUtils.parseXml(new InputSource(getClass().getResourceAsStream(RES_ENCRYPTED)));
			Element el = XmlSecurityUtils.findFirstEncryptedData(doc);
			XmlSecurityUtils.decrypt(doc, el, serverPrivateKey);
			Element sig = XmlSecurityUtils.findFirstSignature(doc);
			Element assertionEl = SamlUtils.findFirstSamlAssertion(doc);
			assertionEl.setIdAttribute("AssertionID", true);
			missingPpidKey = existingUserKey = XmlSecurityUtils.verifySignature(sig);
			existingUserAssertion = new SamlAssertion(assertionEl);
			
			NodeList atts = assertionEl.getElementsByTagNameNS(SamlUtils.SAML_10_NS, "Attribute");
			for (int i = 0; i < atts.getLength(); i++)
			{
				Element att = (Element) atts.item(i);
				if ("privatepersonalidentifier".equals(att.getAttribute("AttributeName")))
					att.getParentNode().removeChild(att);
			}
			missingPpidAssertion = new SamlAssertion(assertionEl);
			existingUserCredentials = new CardSpaceCredentials(existingUserAssertion, existingUserKey);
			missingPpidCredentials = new CardSpaceCredentials(missingPpidAssertion, missingPpidKey);
		}
		
		{
			Document doc = XmlUtils.parseXml(new InputSource(getClass().getResourceAsStream(RES_ENCRYPTED2)));
			Element el = XmlSecurityUtils.findFirstEncryptedData(doc);
			XmlSecurityUtils.decrypt(doc, el, serverPrivateKey);
			Element sig = XmlSecurityUtils.findFirstSignature(doc);
			Element assertionEl = SamlUtils.findFirstSamlAssertion(doc);
			assertionEl.setIdAttribute("AssertionID", true);
			missingUserKey = XmlSecurityUtils.verifySignature(sig);
			missingUserAssertion = new SamlAssertion(assertionEl);
			missingUserCredentials = new CardSpaceCredentials(missingUserAssertion, missingUserKey);
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

	@After
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

	@Test
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

	@Test
	public void testLoadUserByUsernameDebug()
	{
		svc.setDebug(true);
		testLoadUserByUsername();
	}
	
	@Test(expected=UsernameNotFoundException.class)
	public void testLoadUserByUsernameNotFound() throws Exception
	{
		svc.loadUserByUsername("bogus");
	}

	@Test(expected=UsernameNotFoundException.class)
	public void testLoadUserByUsernameNoPassword() throws Exception
	{
		svc.loadUserByUsername("test-no-password");
	}
	
	@Test
	public void testLoadUserByCardSpaceCredentials()
	{
		UserDetails details = svc.loadUserByCardSpaceCredentials(existingUserCredentials);
		assertNotNull(details);
		assertEquals("test", details.getUsername());
	}

	@Test(expected=BadCredentialsException.class)
	public void testLoadUserByCardSpaceCredentialsNotFound()
	{
		svc.loadUserByCardSpaceCredentials(missingUserCredentials);
	}

	@Test(expected=InvalidCredentialsException.class)
	public void testLoadUserByCardSpaceCredentialsMissingPpid()
	{		
		svc.loadUserByCardSpaceCredentials(missingPpidCredentials);
	}
	
}
