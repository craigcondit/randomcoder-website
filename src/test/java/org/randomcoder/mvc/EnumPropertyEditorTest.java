package org.randomcoder.mvc;

import junit.framework.TestCase;

@SuppressWarnings("javadoc")
public class EnumPropertyEditorTest extends TestCase
{
	private EnumPropertyEditor editor;

	@Override
	public void setUp() throws Exception
	{
		editor = new EnumPropertyEditor(TestEnum.class);
	}

	@Override
	public void tearDown() throws Exception
	{
		editor = null;
	}

	public void testGetAsText()
	{
		editor.setValue(TestEnum.ONE);
		assertEquals("Wrong enum value", "ONE", editor.getAsText());
		
		editor.setValue(TestEnum.TWO);
		assertEquals("Wrong enum value", "TWO", editor.getAsText());
		
		editor.setValue(TestEnum.THREE);
		assertEquals("Wrong enum value", "THREE", editor.getAsText());
		
		editor.setValue(null);
		assertEquals("Expected empty enum value", "", editor.getAsText());
	}

	public void testSetAsText()
	{
		editor.setAsText("ONE");
		assertEquals("Wrong enum value", TestEnum.ONE, editor.getValue());

		editor.setAsText("TWO");
		assertEquals("Wrong enum value", TestEnum.TWO, editor.getValue());

		editor.setAsText("THREE");
		assertEquals("Wrong enum value", TestEnum.THREE, editor.getValue());

		editor.setAsText("");
		assertNull("Got enum value", editor.getValue());

		try
		{
			editor.setAsText("BOGUS");
			fail("No exception thrown on bogus value");
		} catch (IllegalArgumentException e) {}		
	}
	
	protected static enum TestEnum
	{
		ONE, TWO, THREE;
	}
}
