package com.randomcoder.user.test;

import static org.junit.Assert.*;

import org.junit.*;

import com.randomcoder.user.*;

public class RolePropertyEditorTest
{
	private RolePropertyEditor editor;
	private RoleDaoMock roleDao;
	
	@Before	public void setUp() throws Exception
	{
		roleDao = new RoleDaoMock();
		editor = new RolePropertyEditor(roleDao);
	}

	@Test public void testGetAsText()
	{
		Role role = new Role();
		role.setName("get-as-text");
		role.setDescription("Get as text");		
		
		editor.setValue(role);
		
		assertEquals("Wrong value", "get-as-text", editor.getAsText());
	}
	
	@Test public void testGetAsTextNull()
	{
		editor.setValue(null);
		assertEquals("", editor.getAsText());
	}

	@Test public void testSetAsText()
	{
		Role role = new Role();
		role.setName("set-as-text");
		role.setDescription("Set as text");		
		roleDao.mockCreate(role);
		
		editor.setAsText("set-as-text");
		
		Object value = editor.getValue();		
		assertNotNull("Null object", value);
		assertTrue("Not a Role", value instanceof Role);
		Role editorRole = (Role) value;
		assertEquals("Wrong role name", "set-as-text", editorRole.getName());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSetAsTextInvalidRole()
	{
		editor.setAsText("bogus-role");		
	}
	
	@After public void tearDown() throws Exception
	{
		roleDao = null;
		editor = null;
	}

}
