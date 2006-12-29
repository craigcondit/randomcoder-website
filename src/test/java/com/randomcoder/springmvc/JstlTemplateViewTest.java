package com.randomcoder.springmvc;

import java.util.*;

import junit.framework.TestCase;

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.web.*;

import com.randomcoder.test.mock.springmvc.JstlTemplateViewMock;

public class JstlTemplateViewTest extends TestCase
{
	private JstlTemplateViewMock view;
	private JstlTemplateViewMock parent;
	
	@Override
	public void setUp() throws Exception
	{
		parent = new JstlTemplateViewMock();
		view = new JstlTemplateViewMock();
		view.setParent(parent);
	}

	@Override
	public void tearDown() throws Exception
	{
		view = null;
		parent = null;
	}

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
}
