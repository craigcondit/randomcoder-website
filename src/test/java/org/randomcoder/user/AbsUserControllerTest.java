package org.randomcoder.user;

import static org.easymock.EasyMock.*;

import java.beans.PropertyEditor;
import java.util.*;

import junit.framework.TestCase;

import org.easymock.IMocksControl;
import org.randomcoder.bo.UserBusiness;
import org.randomcoder.db.Role;
import org.randomcoder.test.mock.user.AbstractUserControllerMock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.ServletRequestDataBinder;

@SuppressWarnings("javadoc")
public class AbsUserControllerTest extends TestCase
{
	private AbstractUserControllerMock controller;
	private IMocksControl control;
	private UserBusiness ub;
	
	@Override
	public void setUp() throws Exception
	{
		control = createControl();
		ub = control.createMock(UserBusiness.class);
		controller = new AbstractUserControllerMock();
		controller.setUserBusiness(ub);
	}

	@Override
	public void tearDown() throws Exception
	{
		controller = null;
		ub = null;
		control = null;
	}
	
	public void testInitBinder() throws Exception
	{		
		MockHttpServletRequest request = new MockHttpServletRequest();
		ServletRequestDataBinder binder = new ServletRequestDataBinder(new Object());
		
		controller.initBinder(request, binder);
		PropertyEditor editor = binder.findCustomEditor(Role.class, null);
		
		assertNotNull("Null property editor", editor);
		assertTrue("Wrong type", editor instanceof RolePropertyEditor);
	}

	public void testReferenceData()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		
		// create a role
		Role role = new Role();
		role.setName("reference-data");
		role.setDescription("Reference data");
		
		expect(ub.listRoles()).andReturn(Collections.singletonList(role));
		control.replay();		
		
		Map refData = controller.referenceData(request);
		
		assertNotNull("Null refData", refData);
		Object value = refData.get("availableRoles");
		assertNotNull("availableRoles missing", value);
		assertTrue("Wrong data type", value instanceof List);
		List list = (List) value;
		assertEquals("Wrong list size", 1, list.size());
		Object item = list.get(0);
		assertNotNull("Null item", item);
		assertTrue("Wrong item type", item instanceof Role);
		Role testRole = (Role) item;
		assertEquals("Wrong role", "reference-data", testRole.getName());
		
		control.verify();
	}
}