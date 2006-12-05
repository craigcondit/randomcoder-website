package com.randomcoder.springmvc.test;

import static org.junit.Assert.*;

import org.junit.*;

import com.randomcoder.springmvc.EnumPropertyEditor;

public class EnumPropertyEditorTest
{
	private EnumPropertyEditor editor;

	@Before public void setUp() throws Exception
	{
		editor = new EnumPropertyEditor(TestEnum.class);
	}

	@After public void tearDown() throws Exception
	{
		editor = null;
	}

	@Test public void testGetAsText()
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

	@Test public void testSetAsText()
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
	
	protected enum TestEnum
	{
		ONE, TWO, THREE;
	}
}
