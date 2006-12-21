package com.randomcoder.saml.test;

import static org.junit.Assert.*;

import java.security.PrivateKey;
import java.util.*;

import org.junit.*;
import org.springframework.core.io.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import com.randomcoder.crypto.*;
import com.randomcoder.saml.*;
import com.randomcoder.security.cardspace.CardSpaceAttributes;
import com.randomcoder.xml.XmlUtils;
import com.randomcoder.xml.security.XmlSecurityUtils;

public class SamlAssertionTest
{
	private static final String RES_ENCRYPTED = "/xmlsec/saml-encrypted.xml";
	private static final String RES_XMLSEC_PROPS = "/xmlsec/xmlsec.properties";
	
	private PrivateKey serverPrivateKey;
	private Document assertionDoc;
	private Element assertionEl;
	
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
		
		serverPrivateKey = certContext.getPrivateKey();
		
		assertionDoc = XmlUtils.parseXml(new InputSource(getClass().getResourceAsStream(RES_ENCRYPTED)));
		Element el = XmlSecurityUtils.findFirstEncryptedData(assertionDoc);
		XmlSecurityUtils.decrypt(assertionDoc, el, serverPrivateKey);
		assertionEl = SamlUtils.findFirstSamlAssertion(assertionDoc);
	}

	@After
	public void tearDown() throws Exception
	{
		assertionDoc = null;
		assertionEl = null;
		serverPrivateKey = null;
	}
	
	@Test
	public void testSamlAssertion() throws Exception
	{
		SamlAssertion assertion = new SamlAssertion(assertionEl);
		assertEquals(SamlVersion.SAML_1_1, assertion.getVersion());
		assertEquals("uuid:b89586f8-4aa8-4157-9db6-bb10afd471eb", assertion.getAssertionId());
		assertEquals("http://schemas.xmlsoap.org/ws/2005/05/identity/issuer/self", assertion.getIssuer());
		
		Date issueInstant = assertion.getIssueInstant();
		assertNotNull(issueInstant);
		
		Date notBefore = assertion.getNotBefore();
		assertNotNull(notBefore);
		assertFalse("issueInstant after notBefore", issueInstant.after(notBefore));
		
		Date notOnOrAfter = assertion.getNotOnOrAfter();
		assertNotNull(notOnOrAfter);
		assertTrue("notOnOrAfter before notBefore", notOnOrAfter.after(notBefore));
		
		// get assertions
		List<SamlAttribute> attributes = assertion.getAttributes();
		assertNotNull(attributes);
		assertEquals(4, attributes.size());
		
		SamlAttributeSpec spec = attributes.get(0).getAttributeSpec();
		assertEquals(CardSpaceAttributes.PRIVATE_PERSONAL_IDENTIFIER, spec);
		
		assertEquals("DLb/nzDbfMEHkME4VYeny0teGTjvYtVdrQvmX9W055E=", attributes.get(0).getValue());		
	}

}
