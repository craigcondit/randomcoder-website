package org.randomcoder.security.cardspace;

import static org.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_ALL_FIELDS;

import java.security.PublicKey;
import java.util.*;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import org.randomcoder.saml.*;
import org.randomcoder.test.TestObjectFactory;

public class CardSpaceCredentialsTest extends TestCase
{
	private SamlAssertion assertion;
	private PublicKey publicKey;
	
	@Override
	public void setUp() throws Exception
	{		
		Document doc = TestObjectFactory.getDecryptedXmlDocument(RESOURCE_SAML_ASSERTION_ALL_FIELDS);
		assertion = TestObjectFactory.getSamlAssertion(doc);
		publicKey = TestObjectFactory.getPublicKey(doc);
	}

	@Override
	public void tearDown() throws Exception
	{
		assertion = null;
		publicKey = null;
	}

	public void testCardSpaceCredentials() throws Exception
	{
		Date now = new Date();
		
		CardSpaceCredentials cred = new CardSpaceCredentials(assertion, publicKey, now);
		
		assertNotNull(cred.getIssueInstant());
		assertNotNull(cred.getNotBefore());
		assertNotNull(cred.getNotOnOrAfter());
		assertEquals(now, cred.getReceivedInstant());		
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
		assertEquals(CardSpaceGender.MALE, cred.getGender());
		
		Map<SamlAttributeSpec, String> claims = cred.getClaims();
		assertNotNull(claims);
		assertEquals(15, claims.keySet().size());		
	}
}
