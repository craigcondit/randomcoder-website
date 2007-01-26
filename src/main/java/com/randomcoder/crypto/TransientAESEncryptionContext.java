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
	
	public void afterPropertiesSet() throws Exception
	{
		if (keyData != null) return; // can't do this twice!
		
		KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
		kgen.init(keySize);
		keyData = kgen.generateKey().getEncoded();
	}

	public byte[] encrypt(byte[] data) throws EncryptionException
	{
		try
		{
			if (data == null) throw new EncryptionException("No data supplied");
			
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
		catch (InvalidKeyException e)
		{
			throw new EncryptionException("Invalid key", e);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new EncryptionException("No such algorithm", e);
		}
		catch (NoSuchPaddingException e)
		{
			throw new EncryptionException("No such padding", e);
		}
		catch (IllegalBlockSizeException e)
		{
			throw new EncryptionException("Illegal block size", e);
		}
		catch (BadPaddingException e)
		{
			throw new EncryptionException("Bad padding", e);
		}
		catch (IOException e)
		{
			throw new EncryptionException("I/O error", e);
		}
	}
	
	public byte[] decrypt(byte[] data) throws EncryptionException
	{
		try
		{
			if (data == null)
				throw new EncryptionException("No data supplied");
			
			if (data.length < IV_SIZE)
				throw new EncryptionException("Unable to read initialization vector");
			
			byte[] iv = new byte[16];
			System.arraycopy(data, 0, iv, 0, IV_SIZE);
			
			SecretKeySpec keySpec = new SecretKeySpec(keyData, ALGORITHM);
			
			Cipher cipher = Cipher.getInstance(CIPHER);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
			return cipher.doFinal(data, IV_SIZE, data.length - IV_SIZE);
		}
		catch (InvalidAlgorithmParameterException e)
		{
			throw new EncryptionException("Invalid algorithm parameter", e);			
		}
		catch (InvalidKeyException e)
		{
			throw new EncryptionException("Invalid key", e);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new EncryptionException("No such algorithm", e);
		}
		catch (NoSuchPaddingException e)
		{
			throw new EncryptionException("No such padding", e);
		}
		catch (IllegalBlockSizeException e)
		{
			throw new EncryptionException("Illegal block size", e);
		}
		catch (BadPaddingException e)
		{
			throw new EncryptionException("Bad padding", e);
		}
	}
}