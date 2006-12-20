package com.randomcoder.security.userdetails.test;

import static org.junit.Assert.*;

import java.security.*;
import java.util.*;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.userdetails.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.*;
import org.springframework.core.io.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import com.randomcoder.crypto.*;
import com.randomcoder.saml.*;
import com.randomcoder.security.cardspace.CardSpaceCredentials;
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
		
	private SamlAssertion assertion1 = null;
	private SamlAssertion assertion2 = null;
	
	private PublicKey key1 = null;
	private PublicKey key2 = null;
	
	private CardSpaceCredentials cred1 = null;
	private CardSpaceCredentials cred2 = null;
	
	@Before
	public void setUp() throws Exception
	{
		cardSpaceTokenDao = new CardSpaceTokenDaoMock();
		userDao = new UserDaoMock();
		svc = new UserDetailsServiceImpl();
		svc.setCardSpaceTokenDao(cardSpaceTokenDao);
		svc.setUserDao(userDao);
		
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
			key1 = XmlSecurityUtils.verifySignature(sig);
			assertion1 = new SamlAssertion(assertionEl);
			cred1 = new CardSpaceCredentials(assertion1, key1);
		}
		
		{
			Document doc = XmlUtils.parseXml(new InputSource(getClass().getResourceAsStream(RES_ENCRYPTED2)));
			Element el = XmlSecurityUtils.findFirstEncryptedData(doc);
			XmlSecurityUtils.decrypt(doc, el, serverPrivateKey);
			Element sig = XmlSecurityUtils.findFirstSignature(doc);
			Element assertionEl = SamlUtils.findFirstSamlAssertion(doc);
			assertionEl.setIdAttribute("AssertionID", true);
			key2 = XmlSecurityUtils.verifySignature(sig);
			assertion2 = new SamlAssertion(assertionEl);
			cred2 = new CardSpaceCredentials(assertion2, key2);
		}
		
		{
			User user = new User();
			user.setUserName("test");
			user.setEnabled(true);
			user.setPassword(User.hashPassword("Password1"));
			user.setEmailAddress("test@example.com");
			user.setRoles(new ArrayList<Role> ());
			userDao.create(user);
			
			CardSpaceToken token = new CardSpaceToken();
			token.setPrivatePersonalIdentifier(cred1.getPrivatePersonalIdentifier());
			token.setIssuerHash(DigestUtils.shaHex(cred1.getIssuerPublicKey()));
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
		assertion1 = null;
		assertion2 = null;
		key1 = null;
		key2 = null;
		cred1 = null;
		cred2 = null;
	}

	@Test
	public void testLoadUserByUsername()
	{
		UserDetails details = svc.loadUserByUsername("test");
		assertNotNull(details);
		assertEquals("test", details.getUsername());
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
		UserDetails details = svc.loadUserByCardSpaceCredentials(cred1);
		assertNotNull(details);
		assertEquals("test", details.getUsername());
	}

	@Test(expected=AuthenticationException.class)
	public void testLoadUserByCardSpaceCredentialsNotFound()
	{
		svc.loadUserByCardSpaceCredentials(cred2);
	}
	
}
