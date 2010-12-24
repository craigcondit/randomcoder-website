package org.randomcoder.user;

import junit.framework.TestCase;

import org.randomcoder.test.mock.dao.RoleDaoMock;

public class RolePropertyEditorTest extends TestCase
{
	private RolePropertyEditor editor;
	private RoleDaoMock roleDao;
	
	@Override
	public void setUp() throws Exception
	{
		roleDao = new RoleDaoMock();
		editor = new RolePropertyEditor(roleDao);
	}

	public void testGetAsText()
	{
		Role role = new Role();
		role.setName("get-as-text");
		role.setDescription("Get as text");		
		
		editor.setValue(role);
		
		assertEquals("Wrong value", "get-as-text", editor.getAsText());
	}
	
	public void testGetAsTextNull()
	{
		editor.setValue(null);
		assertEquals("", editor.getAsText());
	}

	public void testSetAsText()
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

	public void testSetAsTextInvalidRole()
	{
		try
		{
			editor.setAsText("bogus-role");		
			fail("IllegalArgumentException expected");
		}
		catch (IllegalArgumentException e)
		{
			// pass
		}
	}
	
	@Override
	public void tearDown() throws Exception
	{
		roleDao = null;
		editor = null;
	}

}
