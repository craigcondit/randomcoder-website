package org.randomcoder.user;

import static org.easymock.EasyMock.*;
import junit.framework.TestCase;

import org.easymock.IMocksControl;
import org.randomcoder.bo.UserBusiness;

@SuppressWarnings("javadoc")
public class RolePropertyEditorTest extends TestCase
{
	private IMocksControl control;
	private UserBusiness ub;
	private RolePropertyEditor editor;
	
	@Override
	public void setUp() throws Exception
	{
		control = createControl();
		ub = control.createMock(UserBusiness.class);
		editor = new RolePropertyEditor(ub);
	}

	@Override
	public void tearDown() throws Exception
	{
		editor = null;
		ub = null;
		control = null;
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
		
		expect(ub.findRoleByName("set-as-text")).andReturn(role);
		control.replay();
		
		editor.setAsText("set-as-text");		
		Object value = editor.getValue();
		control.verify();
		
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
}