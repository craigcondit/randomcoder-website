package com.randomcoder.crypto;

import java.io.*;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.InitializingBean;

/**
 * EncryptionContext which implements padded AES encryption using a
 * temporary key.
 * 
 * <pre>
 * Copyright (c) 2007, Craig Condit. All rights reserved.
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
public final class TransientAESEncryptionContext implements EncryptionContext, InitializingBean
{
	private static final String ALGORITHM = "AES";
	private static final String CIPHER = "AES/CBC/PKCS7Padding";
	private static final int IV_SIZE = 16;
	
	private byte[] keyData = null;
	private int keySize = 128;
	
	static
	{
		// initialize bouncycastle provider if it's not available already
		if (Security.getProvider("BC") == null)
			Security.addProvider(new BouncyCastleProvider());
	}
	
	/**
	 * Sets the key size in bits (defaults to 128).
	 * @param keySize key size
	 */
	public void setKeySize(int keySize)
	{
		this.keySize = keySize;
	}
	
	/**
	 * Initializes the internal state of the object after all
	 * properties have been set.
	 * @throws Exception if an error occurs
	 */
	@Override
	public void afterPropertiesSet() throws Exception
	{
		if (keyData != null) return; // can't do this twice!
		
		KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
		kgen.init(keySize);
		keyData = kgen.generateKey().getEncoded();
	}

	@Override
	public byte[] encrypt(byte[] data) throws EncryptionException
	{
		if (data == null)
			throw new EncryptionException("No data supplied");
		
		try
		{			
			SecretKeySpec keySpec = new SecretKeySpec(keyData, ALGORITHM);
			
			Cipher cipher = Cipher.getInstance(CIPHER);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			
			byte[] enc = cipher.doFinal(data);
			byte[] iv = cipher.getIV();
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bos.write(iv);
			bos.write(enc);
			bos.flush();
			bos.close();
			return bos.toByteArray();
		}
		catch (Exception e)
		{
			throw new EncryptionException("Error while encrypting data", e);
		}
	}
	
	@Override
	public byte[] decrypt(byte[] data) throws EncryptionException
	{
		if (data == null)
			throw new EncryptionException("No data supplied");
		
		if (data.length < IV_SIZE)
			throw new EncryptionException("Unable to read initialization vector");
		
		try
		{
			byte[] iv = new byte[16];
			System.arraycopy(data, 0, iv, 0, IV_SIZE);
			
			SecretKeySpec keySpec = new SecretKeySpec(keyData, ALGORITHM);
			
			Cipher cipher = Cipher.getInstance(CIPHER);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
			return cipher.doFinal(data, IV_SIZE, data.length - IV_SIZE);
		}
		catch (Exception e)
		{
			throw new EncryptionException("Error while decrypting data", e);
		}
	}
}