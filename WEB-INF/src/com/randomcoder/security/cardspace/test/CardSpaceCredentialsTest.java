package com.randomcoder.security.cardspace.test;

import static org.junit.Assert.*;

import java.security.*;
import java.util.*;

import org.junit.*;
import org.springframework.core.io.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import com.randomcoder.crypto.*;
import com.randomcoder.saml.*;
import com.randomcoder.security.cardspace.*;
import com.randomcoder.xml.XmlUtils;
import com.randomcoder.xml.security.XmlSecurityUtils;

public class CardSpaceCredentialsTest
{

	private static final String RES_ENCRYPTED = "/xmlsec/allfields-encrypted.xml";
	private static final String RES_XMLSEC_PROPS = "/xmlsec/xmlsec.properties";
	
	private SamlAssertion assertion;
	private PublicKey publicKey;
	
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
	}

	@After
	public void tearDown() throws Exception
	{
		assertion = null;
		publicKey = null;
	}

	@Test
	public void testCardSpaceCredentials() throws Exception
	{
		CardSpaceCredentials cred = new CardSpaceCredentials(assertion, publicKey);
		
		assertNotNull(cred.getIssueInstant());
		assertNotNull(cred.getNotBefore());
		assertNotNull(cred.getNotOnOrAfter());
		
		
		assertEquals("uuid:469807fb-9f15-4ea4-89d6-f9f65ec6f55b", cred.getAssertionId());
		assertEquals("http://schemas.xmlsoap.org/ws/2005/05/identity/issuer/self", cred.getIssuer());
		
		byte[] data = cred.getIssuerPublicKey();		
		assertNotNull(data);
		assertTrue(data.length > 0);
		
		assertEquals(SamlVersion.SAML_1_1, cred.getVersion());
		assertEquals("ma+MqcBN55LOepIdXwwG5985zYsXGZBvVArNHduQ2jU=", cred.getPrivatePersonalIdentifier());
		assertEquals("CardSpace", cred.getFirstName());
		assertEquals("User", cred.getLastName());
		assertEquals("cardspace@example.com", cred.getEmailAddress());
		assertEquals("123 Main Ave.", cred.getStreet());
		assertEquals("Anytown", cred.getCity());
		assertEquals("CA", cred.getState());
		assertEquals("12345", cred.getZip());
		assertEquals("United States", cred.getCountry());
		assertEquals("(800) 555-1212", cred.getHomePhone());
		assertEquals("(800) 555-1313", cred.getOtherPhone());
		assertEquals("(800) 555-1414", cred.getMobilePhone());
		assertNotNull(cred.getDateOfBirth());
		assertEquals("http://www.example.com/", cred.getWebPage());		
		assertEquals(Gender.MALE, cred.getGender());
		
		Map<SamlAttributeSpec, String> claims = cred.getClaims();
		assertNotNull(claims);
		assertEquals(15, claims.keySet().size());		
	}
}
