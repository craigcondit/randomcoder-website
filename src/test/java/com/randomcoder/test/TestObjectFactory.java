package com.randomcoder.test;

import java.io.*;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.springframework.core.io.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import com.randomcoder.crypto.*;
import com.randomcoder.saml.*;
import com.randomcoder.xml.XmlUtils;
import com.randomcoder.xml.security.XmlSecurityUtils;

public class TestObjectFactory
{
	public static final String RESOURCE_SAML_ASSERTION_TEST = "/data/saml-assertion-test.xml";
	public static final String RESOURCE_SAML_ASSERTION_TEST_2 = "/data/saml-assertion-test-2.xml";
	public static final String RESOURCE_SAML_ASSERTION_ALL_FIELDS = "/data/saml-assertion-all-fields.xml";
	
	private static final String RESOURCE_XMLSEC_PROPS = "/data/xml-security.properties";
	
	private static final String CERTIFICATE_PASSWORD = "certificate.password";
	private static final String CERTIFICATE_ALIAS = "certificate.alias";
	private static final String KEYSTORE_PASSWORD = "keystore.password";
	private static final String KEYSTORE_TYPE = "keystore.type";
	private static final String KEYSTORE_RESOURCE = "keystore.resource";
	private static final String CLIENT_PUBLICKEY_ENCODED = "client.publickey.encoded";

	public static String getEncodedClientPublicKey() throws IOException
	{
		Properties properties = new Properties();
		properties.load(TestObjectFactory.class.getResourceAsStream(RESOURCE_XMLSEC_PROPS));
		return properties.getProperty(CLIENT_PUBLICKEY_ENCODED);
	}
	
	public static KeystoreCertificateFactoryBean getKeystoreCertificateFactoryBean()
	throws Exception
	{
		Properties properties = new Properties();
		properties.load(TestObjectFactory.class.getResourceAsStream(RESOURCE_XMLSEC_PROPS));

		KeystoreCertificateFactoryBean factory = new KeystoreCertificateFactoryBean();
		
		Resource keystoreLocation = new ClassPathResource(properties.getProperty(KEYSTORE_RESOURCE));
		
		factory.setKeystoreLocation(keystoreLocation);
		factory.setKeystoreType(properties.getProperty(KEYSTORE_TYPE));
		factory.setKeystorePassword(properties.getProperty(KEYSTORE_PASSWORD));
		factory.setCertificateAlias(properties.getProperty(CERTIFICATE_ALIAS));
		factory.setCertificatePassword(properties.getProperty(CERTIFICATE_PASSWORD));
		factory.afterPropertiesSet();
		
		return factory;
	}
	
	public static String getResourceAsString(String resource) throws IOException
	{
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(TestObjectFactory.class.getResourceAsStream(resource)));
			StringBuilder sbuf = new StringBuilder();
			char[] buf = new char[32768];
			int c;
			do
			{
				c = reader.read(buf);
				if (c >= 0) sbuf.append(buf, 0, c); 
			}
			while (c > 0);
			return sbuf.toString();
		}
		finally
		{
			reader.close();
		}
	}
	
	public static CertificateContext getCertificateContext() throws Exception
	{
		return getCertificateContext(getKeystoreCertificateFactoryBean());
	}
	
	public static CertificateContext getCertificateContext(KeystoreCertificateFactoryBean factory) throws Exception
	{
		return (CertificateContext) factory.getObject();
	}
	
	public static X509Certificate getCertificate() throws Exception
	{
		return getCertificate(getCertificateContext());
	}

	public static X509Certificate getCertificate(CertificateContext context) throws Exception
	{
		return context.getCertificate();
	}
	
	public static PrivateKey getPrivateKey() throws Exception
	{
		return getPrivateKey(getCertificateContext());
	}
	
	public static PrivateKey getPrivateKey(CertificateContext context) throws Exception
	{
		return context.getPrivateKey();
	}
	
	public static Document getXmlDocument(String resource) throws Exception
	{
		return XmlUtils.parseXml(new InputSource(TestObjectFactory.class.getResourceAsStream(resource)));
	}
	
	public static Document getDecryptedXmlDocument(String resource) throws Exception
	{
		Document doc = getXmlDocument(resource);		
		Element el = XmlSecurityUtils.findFirstEncryptedData(doc);
		XmlSecurityUtils.decrypt(doc, el, getPrivateKey());
		return doc;
	}
	
	public static SamlAssertion getSamlAssertion(String resource) throws Exception
	{
		Document assertion = getDecryptedXmlDocument(resource);
		Element assertionEl = SamlUtils.findFirstSamlAssertion(assertion);		
		return new SamlAssertion(assertionEl);
	}

	public static SamlAssertion getSamlAssertion(Document decryptedXml) throws Exception
	{
		Element assertionEl = SamlUtils.findFirstSamlAssertion(decryptedXml);		
		return new SamlAssertion(assertionEl);
	}
	
	public static PublicKey getPublicKey(Document decryptedXml) throws Exception
	{
		SamlUtils.findFirstSamlAssertion(decryptedXml).setIdAttribute("AssertionID", true);
		Element signature = XmlSecurityUtils.findFirstSignature(decryptedXml);
		return XmlSecurityUtils.verifySignature(signature);
	}
}
