package com.randomcoder.security.cardspace.test;

import static org.junit.Assert.*;

import java.security.*;
import java.util.*;

import org.acegisecurity.*;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.UserDetails;
import org.junit.*;
import org.springframework.core.io.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import com.randomcoder.crypto.*;
import com.randomcoder.saml.*;
import com.randomcoder.security.cardspace.*;
import com.randomcoder.xml.XmlUtils;
import com.randomcoder.xml.security.XmlSecurityUtils;

public class CardSpaceAuthenticationProviderTest
{
	
	private static final String RES_ENCRYPTED = "/xmlsec/allfields-encrypted.xml";
	private static final String RES_XMLSEC_PROPS = "/xmlsec/xmlsec.properties";
	
	private SamlAssertion assertion;
	private PublicKey publicKey;
	private CardSpaceAuthenticationProvider provider;
	
	@Before
	public void setUp() throws Exception
	{
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
		Document assertionDoc = XmlUtils.parseXml(new InputSource(getClass().getResourceAsStream(RES_ENCRYPTED)));
		Element el = XmlSecurityUtils.findFirstEncryptedData(assertionDoc);
		XmlSecurityUtils.decrypt(assertionDoc, el, serverPrivateKey);
		Element assertionEl = SamlUtils.findFirstSamlAssertion(assertionDoc);
		assertion = new SamlAssertion(assertionEl);
		
		Element sig = XmlSecurityUtils.findFirstSignature(assertionDoc);
		assertionEl.setIdAttribute("AssertionID", true);
		publicKey = XmlSecurityUtils.verifySignature(sig);
		
		provider = new CardSpaceAuthenticationProvider();
		provider.setCardSpaceUserDetailsService(new CardSpaceUserDetailsServiceMock(false));
	}

	@After
	public void tearDown() throws Exception
	{
		assertion = null;
		publicKey = null;
		provider = null;
	}

	@Test
	public void testSetValidators()
	{
		List<CardSpaceCredentialsValidator> validators = new ArrayList<CardSpaceCredentialsValidator>();
		validators.add(new CardSpaceCredentialsValidatorMock());
		provider.setValidators(validators);
		provider.afterPropertiesSet();
	}

	@Test
	public void testSetValidator()
	{
		provider.setValidator(new CardSpaceCredentialsValidatorMock());
		provider.afterPropertiesSet();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAfterPropertiesSetNoValidators()
	{
		provider.afterPropertiesSet();
	}

	@Test
	public void testAuthenticate()
	{
		provider.setValidator(new CardSpaceCredentialsValidatorMock());
		provider.afterPropertiesSet();
		
		// null auth
		assertNull(provider.authenticate(null));
		
		// wrong token type
		assertNull(provider.authenticate(new UsernamePasswordAuthenticationToken("user", "pass")));
				
		// populate test objects
		CardSpaceCredentials creds = new CardSpaceCredentials(assertion, publicKey, new Date());		
		CardSpaceAuthenticationToken token = new CardSpaceAuthenticationToken(creds);
		
		// null user
		provider.setCardSpaceUserDetailsService(new CardSpaceUserDetailsServiceMock(true));
		assertNull(provider.authenticate(token));
		
		// success
		provider.setCardSpaceUserDetailsService(new CardSpaceUserDetailsServiceMock(false));
		Authentication auth = provider.authenticate(token);
		assertNotNull(auth);
		assertEquals(CardSpaceAuthenticationToken.class, auth.getClass());
	}

	@Test
	public void testSupports()
	{
		assertFalse(provider.supports(String.class));
		assertTrue(provider.supports(CardSpaceAuthenticationToken.class));
	}

	@SuppressWarnings("unused")
	private static class CardSpaceUserDetailsServiceMock implements CardSpaceUserDetailsService
	{
		private final boolean returnNull;
		
		public CardSpaceUserDetailsServiceMock(boolean returnNull)
		{
			this.returnNull = returnNull;
		}

		public UserDetails loadUserByCardSpaceCredentials(CardSpaceCredentials credentials)
		throws AuthenticationException
		{
			return returnNull ? null : new UserDetailsMock();
		}		
	}
	
	@SuppressWarnings("unused")
	private static class CardSpaceCredentialsValidatorMock implements CardSpaceCredentialsValidator
	{
		public CardSpaceCredentialsValidatorMock() {}

		public void validate(CardSpaceCredentials credentials) throws AuthenticationException
		{
		}
	}
	
	private static class UserDetailsMock implements UserDetails
	{
		private static final long serialVersionUID = -6648737648831411882L;

		public UserDetailsMock() {}
		
		public GrantedAuthority[] getAuthorities()
		{
			return new GrantedAuthority[] { new GrantedAuthorityImpl("ROLE_TEST") };
		}

		public String getPassword()
		{
			return "pass";
		}

		public String getUsername()
		{
			return "test";
		}

		public boolean isAccountNonExpired()
		{
			return true;
		}

		public boolean isAccountNonLocked()
		{
			return true;
		}

		public boolean isCredentialsNonExpired()
		{
			return true;
		}

		public boolean isEnabled()
		{
			return true;
		}		
	}
}
