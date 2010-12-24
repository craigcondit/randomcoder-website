package com.randomcoder.crypto;

import junit.framework.TestCase;

public class TransientAESEncryptionContextTest extends TestCase
{
	TransientAESEncryptionContext context;
	
	@Override
	protected void setUp() throws Exception
	{
		context = new TransientAESEncryptionContext();
		context.setKeySize(128);
		context.afterPropertiesSet();
	}

	@Override
	protected void tearDown() throws Exception
	{
		context = null;
	}

	public void testEncryptDecrypt() throws Exception
	{
		String test = "TEST DATA";
		byte[] data = test.getBytes("UTF-8");		
		byte[] encrypted = context.encrypt(data);
		byte[] decrypted = context.decrypt(encrypted);
		String test2 = new String(decrypted, "UTF-8");
		assertEquals(test, test2);
	}
	
	public void testEncryptNull() throws Exception
	{
		try
		{
			context.encrypt(null);
			fail("No exception thrown");
		}
		catch (EncryptionException e)
		{
		}
	}
	
	public void testDecryptNull() throws Exception
	{
		try
		{
			context.decrypt(null);
			fail("No exception thrown");
		}
		catch (EncryptionException e)
		{
			assertNotNull(e.getMessage());
		}
	}

	public void testDecryptEmpty() throws Exception
	{
		try
		{
			context.decrypt(new byte[] {});
			fail("No exception thrown");
		}
		catch (EncryptionException e)
		{
			assertNotNull(e.getMessage());
		}
	}

	public void testDecryptBadData() throws Exception
	{
		try
		{
			byte[] data = "This is not encrypted data".getBytes("UTF-8");
			context.decrypt(data);
			fail("No exception thrown");
		}
		catch (EncryptionException e)
		{
			assertNotNull(e.getCause());
		}
	}
}