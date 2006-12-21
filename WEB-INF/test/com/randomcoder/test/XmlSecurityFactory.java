package com.randomcoder.test;

import java.util.Properties;

import org.springframework.core.io.*;

import com.randomcoder.crypto.KeystoreCertificateFactoryBean;

public class XmlSecurityFactory
{
	private static final String RESOURCE_XMLSEC_PROPS = "/data/xml-security.properties";
	
	private static final String CERTIFICATE_PASSWORD = "certificate.password";
	private static final String CERTIFICATE_ALIAS = "certificate.alias";
	private static final String KEYSTORE_PASSWORD = "keystore.password";
	private static final String KEYSTORE_TYPE = "keystore.type";
	private static final String KEYSTORE_RESOURCE = "keystore.resource";

	public static KeystoreCertificateFactoryBean getKeystoreCertificateFactoryBean()
	throws Exception
	{
		Properties properties = new Properties();
		properties.load(XmlSecurityFactory.class.getResourceAsStream(RESOURCE_XMLSEC_PROPS));

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
	
}
