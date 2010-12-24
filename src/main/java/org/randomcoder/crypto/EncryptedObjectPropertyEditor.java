package org.randomcoder.crypto;

import java.beans.PropertyEditorSupport;
import java.io.*;

import org.apache.commons.codec.binary.Base64;

/**
 * Property editor which supports converting to / from encrypted form.
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
public final class EncryptedObjectPropertyEditor extends PropertyEditorSupport
{
	private final EncryptionContext context;
	
	/**
	 * Creates a new property editor, using the given secret key context.
	 * @param context secret key context
	 */
	public EncryptedObjectPropertyEditor(EncryptionContext context)
	{
		this.context = context;
	}
	
	/**
	 * Gets the value of the associated object as text.
	 * @return string representation of object
	 */
	@Override
	public String getAsText()
	{			
		try
		{
			Object obj = getValue();
			if (obj == null) return "";
			
			// serialize
			byte[] ser = serializeObject(obj);

			// encrypt
			byte[] enc = context.encrypt(ser);

			// base-64 encode
			return new String(Base64.encodeBase64(enc), "UTF-8");
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error encrypting object", e);
		}
	}
	
	/**
	 * Sets the value of the object as a text string.
	 * @param string text value
	 * @throws IllegalArgumentException if value cannot be parsed
	 */
	@Override
	public void setAsText(String string) throws IllegalArgumentException
	{		
		try
		{
			if (string == null || string.trim().length() == 0)
			{
				setValue(null);
				return;
			}
			
			// base-64 decode
			byte[] enc = Base64.decodeBase64(string.getBytes("UTF-8"));
			
			// decrypt
			byte[] ser = context.decrypt(enc);
			
			// deserialize
			Object obj = deserializeObject(ser);
			
			// set value
			setValue(obj);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Error decrypting object", e);
		}
	}

	/**
	 * Sets the value of the associated object.
	 * @param value object to set
	 */
	@Override
	public void setValue(Object value)
	{
		try
		{
			// attempt serialization if value is specified
			if (value != null) serializeObject(value);
			
			super.setValue(value);
		}
		catch (IOException e)
		{
			throw new IllegalArgumentException("Value must be serializable");
		}
	}
	
	private byte[] serializeObject(Object obj)
	throws IOException
	{
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try
		{
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			
			oos.writeObject(obj);
		}
		finally
		{
			if (oos != null) oos.close();
		}
		
		return bos.toByteArray();
	}
	
	private Object deserializeObject(byte[] data)
	throws IOException, ClassNotFoundException
	{
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		try
		{
			bis = new ByteArrayInputStream(data);
			ois = new ObjectInputStream(bis);
			return ois.readObject();
		}
		finally
		{
			if (ois != null) ois.close();
		}
	}
}
