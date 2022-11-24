package org.randomcoder.mvc.editor;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.bo.UserBusiness;
import org.randomcoder.db.Role;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

public class RolePropertyEditorTest {
    private IMocksControl control;
    private UserBusiness ub;
    private RolePropertyEditor editor;

    @Before
    public void setUp() {
        control = createControl();
        ub = control.createMock(UserBusiness.class);
        editor = new RolePropertyEditor(ub);
    }

    @After
    public void tearDown() {
        editor = null;
        ub = null;
        control = null;
    }

    @Test
    public void testGetAsText() {
        Role role = new Role();
        role.setName("get-as-text");
        role.setDescription("Get as text");

        editor.setValue(role);

        assertEquals("Wrong value", "get-as-text", editor.getAsText());
    }

    @Test
    public void testGetAsTextNull() {
        editor.setValue(null);
        assertEquals("", editor.getAsText());
    }

    @Test
    public void testSetAsText() {
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

    @Test(expected = IllegalArgumentException.class)
    public void testSetAsTextInvalidRole() {
        editor.setAsText("bogus-role");
    }
}