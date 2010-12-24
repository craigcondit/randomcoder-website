package org.randomcoder.user;

import java.beans.PropertyEditor;
import java.util.*;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.ServletRequestDataBinder;

import org.randomcoder.test.mock.dao.RoleDaoMock;
import org.randomcoder.test.mock.user.AbstractUserControllerMock;

public class AbsUserControllerTest extends TestCase
{
	private AbstractUserControllerMock controller;
	private RoleDaoMock roleDao;
	
	@Override
	public void setUp() throws Exception
	{
		roleDao = new RoleDaoMock();
		controller = new AbstractUserControllerMock();
		controller.setRoleDao(roleDao);
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
		roleDao.mockCreate(role);		
		
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
	}

	@Override
	public void tearDown() throws Exception
	{
		controller = null;
		roleDao = null;
	}
}
