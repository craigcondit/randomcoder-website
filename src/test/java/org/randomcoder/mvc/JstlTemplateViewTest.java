package org.randomcoder.mvc;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.easymock.*;
import org.junit.*;
import org.randomcoder.test.mock.springmvc.JstlTemplateViewMock;
import org.springframework.web.context.WebApplicationContext;

@SuppressWarnings("javadoc")
public class JstlTemplateViewTest
{
	private JstlTemplateViewMock view;
	private JstlTemplateViewMock parent;
	private IMocksControl control;
	private HttpServletRequest req;
	private WebApplicationContext wac;
	private ServletContext sc;
	
	@Before
	public void setUp()
	{
		control = createControl();
		req = control.createMock(HttpServletRequest.class);
		sc = control.createMock(ServletContext.class);
		wac = control.createMock(WebApplicationContext.class);
		
		parent = new JstlTemplateViewMock();
		view = new JstlTemplateViewMock();
		view.setParent(parent);
		
		view.setServletContext(sc);
		view.setApplicationContext(wac);
	}

	@After
	public void tearDown()
	{
		view = null;
		parent = null;
		wac = null;
		sc = null;
		req = null;
		control = null;
	}

	@Test
	public void testGetUrl()
	{
		assertEquals(null, view.getUrl());
		parent.setUrl("/parent");
		assertEquals("/parent", view.getUrl());
		view.setUrl("/child");
		assertEquals("/child", view.getUrl());
		view.setUrl(null);
		assertEquals("/parent", view.getUrl());
	}
	
	@Test
	public void testGetTemplateName()
	{
		assertEquals("template", view.getTemplateName());
		parent.setTemplateName("parent");
		assertEquals("parent", view.getTemplateName());
		view.setTemplateName("child");
		assertEquals("child", view.getTemplateName());
		view.setTemplateName(null);
		assertEquals("parent", view.getTemplateName());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void testExposeHelpers() throws Exception
	{
		Map<String, Object> parentMap = new HashMap<>();
		Map<String, Object> childMap = new HashMap<>();
		
		parentMap.put("parent", "parent");
		parentMap.put("both", "parent");
		
		childMap.put("child", "child");
		childMap.put("both", "child");
		
		parent.setTemplateAttributes(parentMap);
		view.setTemplateAttributes(childMap);

		Capture<Object> cm = new Capture<>();

		control.reset();
		
		expect(wac.getServletContext()).andStubReturn(null);
		expect(wac.getBean(isA(String.class), isA(Class.class))).andStubReturn(null);
		
		req.setAttribute(eq("template"), capture(cm));
		
		expect(req.getAttribute("org.springframework.web.servlet.support.RequestContext.CONTEXT")).andStubReturn(wac);
		expect(req.getAttribute(isA(String.class))).andStubReturn(null);
		expect(req.getSession(false)).andStubReturn(null);
		expect(req.getLocale()).andStubReturn(null);
		req.setAttribute(isA(String.class), anyObject());
		expectLastCall().anyTimes();
		
		expect(sc.getAttribute(isA(String.class))).andStubReturn(null);
		
		control.replay();
		
		view.exposeHelpers(req);
		control.verify();
		
		Map templateMap = (Map) cm.getValue();
		
		assertEquals("parent", templateMap.get("parent"));
		assertEquals("child", templateMap.get("child"));
		assertEquals("child", templateMap.get("both"));
	}
}
