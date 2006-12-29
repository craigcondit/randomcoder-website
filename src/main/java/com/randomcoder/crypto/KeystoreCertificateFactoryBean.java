package com.randomcoder.crypto;

import java.io.InputStream;
import java.security.*;
import java.security.cert.X509Certificate;

import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;

/**
 * Spring FactoryBean which produces <code>CertificateContext</code> objects
 * from keystores.
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre> 
 */
public class KeystoreCertificateFactoryBean implements FactoryBean, InitializingBean
{
	private Resource keystoreLocation;
	private String keystorePassword;
	private String keystoreType;
	private String certificateAlias;
	private String certificatePassword;
	private boolean init = false;
	private KeyStore keystore;

	/**
	 * Sets the location of the keystore to load certificates from.
	 * @param keystoreLocation keystore location
	 */
	@Required
	public void setKeystoreLocation(Resource keystoreLocation)
	{
		this.keystoreLocation = keystoreLocation;
	}
	
	/**
	 * Sets the password used to encrypt to the keystore.
	 * @param keystorePassword keystore password
	 */
	@Required
	public void setKeystorePassword(String keystorePassword)
	{
		this.keystorePassword = keystorePassword;
	}
	
	/**
	 * Sets the keystore type (pkcs12, etc.)
	 * @param keystoreType keystore type
	 */
	@Required
	public void setKeystoreType(String keystoreType)
	{
		this.keystoreType = keystoreType;
	}
	
	/**
	 * Sets the alias that the certificate is stored under
	 * in the keystore.
	 * @param certificateAlias certificate alias
	 */
	@Required
	public void setCertificateAlias(String certificateAlias)
	{
		this.certificateAlias = certificateAlias;
	}
	
	/**
	 * Sets the password used to encrypt the certificate in the keystore.
	 * @param certificatePassword certificate password
	 */
	@Required
	public void setCertificatePassword(String certificatePassword)
	{
		this.certificatePassword = certificatePassword;
	}
	
	/**
	 * Performs final setup of the class.
	 * @throws Exception if initialization fails
	 */
	public void afterPropertiesSet() throws Exception
	{
		InputStream is = keystoreLocation.getInputStream();
		
		try
		{
			keystore = KeyStore.getInstance(keystoreType);
			keystore.load(keystoreLocation.getInputStream(), keystorePassword.toCharArray());
	    init = true;
		}
		finally
		{
			try { is.close(); } catch (Exception ignored) {}
		}
	}
	
	/**
	 * Gets the object type this factory returns.
	 * @return always <code>CertificateContext</code>
	 */
	public Class getObjectType()
	{
		return CertificateContext.class;
	}

	/**
	 * Determines if this factory returns a singleton or not.
	 * @return always false
	 */
	public boolean isSingleton()
	{
		return false;
	}
	
	/**
	 * Gets an instance of the target object from this factory
	 * @return CertificateContext instance
	 * @throws Exception if instantiation fails
	 */
	public Object getObject() throws Exception
	{
		if (!init)
			throw new FactoryBeanNotInitializedException();
		
		X509Certificate certificate = (X509Certificate) keystore.getCertificate(certificateAlias);
		PrivateKey privateKey = (PrivateKey) keystore.getKey(certificateAlias, certificatePassword.toCharArray());
		
		return new CertificateContext(certificate, privateKey);
	}
	
}
