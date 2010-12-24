package org.randomcoder.crypto;

import junit.framework.TestCase;

public class EncryptedObjectPropertyEditorTest extends TestCase
{
	TransientAESEncryptionContext context;
	EncryptedObjectPropertyEditor editor;
	
	@Override
	protected void setUp() throws Exception
	{
		context = new TransientAESEncryptionContext();
		context.setKeySize(128);
		context.afterPropertiesSet();
		
		editor = new EncryptedObjectPropertyEditor(context);		
	}

	@Override
	protected void tearDown() throws Exception
	{
		editor = null;
		context = null;
	}

	public void testGetAsText()
	{
		editor.setValue(new EncryptedObject("test"));
		String encrypted = editor.getAsText();
		
		assertNotNull("Null encrypted data", encrypted);
		assertEquals("Wrong length", 152, encrypted.length());
	}

	public void testSetAsText()
	{
		// encrypt a value
		editor.setValue(new EncryptedObject("test"));
		String encrypted = editor.getAsText();
		
		// new editor instance
		editor = new EncryptedObjectPropertyEditor(context);		
		assertNull(editor.getValue());
		editor.setAsText(encrypted);
		
		Object value = editor.getValue();
		assertNotNull(value);
		assertEquals(EncryptedObject.class, value.getClass());
		assertEquals("test", ((EncryptedObject) value).getValue());
	}

	public void testSetAsTextNull()
	{
		editor.setAsText(null);
		assertNull(editor.getValue());
	}
	
	public void testSetAsTextInvalid()
	{
		try
		{
			editor.setAsText("BOGUS");
			fail("Exception not thrown");
		}
		catch (IllegalArgumentException e)
		{			
		}
	}
	
	public void testSetValue()
	{
		editor.setValue(new EncryptedObject("test"));
		Object value = editor.getValue();
		assertNotNull(value);
		assertEquals(EncryptedObject.class, value.getClass());
		assertEquals("test", ((EncryptedObject) value).getValue());
	}

	public void testSetValueNotSerializable()
	{
		try
		{
			editor.setValue(new Object());
			fail("Exception not thrown");
		}
		catch (IllegalArgumentException e)
		{
		}
	}
}
