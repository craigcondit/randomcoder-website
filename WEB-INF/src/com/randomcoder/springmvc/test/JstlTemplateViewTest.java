package com.randomcoder.springmvc.test;

import static org.junit.Assert.assertEquals;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.junit.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.web.*;

import com.randomcoder.springmvc.JstlTemplateView;

public class JstlTemplateViewTest
{
	private JstlTemplateViewMock view;
	private JstlTemplateViewMock parent;
	
	@Before
	public void setUp() throws Exception
	{
		parent = new JstlTemplateViewMock();
		view = new JstlTemplateViewMock();
		view.setParent(parent);
	}

	@After
	public void tearDown() throws Exception
	{
		view = null;
		parent = null;
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

	@Test
	public void testExposeHelpers() throws Exception
	{
		Map<String, Object> parentMap = new HashMap<String, Object>();
		Map<String, Object> childMap = new HashMap<String, Object>();
		
		parentMap.put("parent", "parent");
		parentMap.put("both", "parent");
		
		childMap.put("child", "child");
		childMap.put("both", "child");
		
		parent.setAttributes(parentMap);
		view.setAttributes(childMap);
		
		// set some necessary container properties
		view.setServletContext(new MockServletContext());
		view.setApplicationContext(new GenericApplicationContext());
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		view.exposeHelpers(request);
		
		Map templateMap = (Map) request.getAttribute("template");
		
		assertEquals("parent", templateMap.get("parent"));
		assertEquals("child", templateMap.get("child"));
		assertEquals("child", templateMap.get("both"));
	}
	
	protected class JstlTemplateViewMock extends JstlTemplateView
	{
		@Override
		protected void exposeHelpers(HttpServletRequest request) throws Exception
		{
			super.exposeHelpers(request);
		}

		@Override
		protected String getTemplateName()
		{
			return super.getTemplateName();
		}
	}
}
