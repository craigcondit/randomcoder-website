package com.randomcoder.springmvc;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.view.JstlView;

/**
 * Custom JSTL-based template view.
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
public class JstlTemplateView extends JstlView
{
	private static final String DEFAULT_TEMPLATE_NAME = "template";

	private Map<String, Object> attributes;
	private String templateName;
	private String url;
	private JstlTemplateView parent;

	/**
	 * Sets the attributes to add to this template.
	 * @param attributes map of attribute key/value pairs.
	 */
	public void setAttributes(Map<String, Object> attributes)
	{
		this.attributes = attributes;
	}

	/**
	 * Gets the URL associated with this view.
	 * 
	 * <p> Delegates to the parent view if no url was specified. </p>
	 * 
	 * @return url
	 */
	@Override
	public String getUrl()
	{
		if (url != null)
			return url;
		if (parent != null)
			return parent.getUrl();
		return super.getUrl();
	}

	/**
	 * Sets the URL for this view.
	 * @param url url
	 */
	@Override
	public void setUrl(String url)
	{
		this.url = url;
		super.setUrl(url);
	}

	/**
	 * Sets the name of the request attribute to populate with the template map.
	 * @param templateName template name (defaults to "template")
	 */
	public void setTemplateName(String templateName)
	{
		this.templateName = templateName;
	}

	/**
	 * Sets the parent template of this template.
	 * 
	 * <p> Several methods in this class delegate to the parent. </p>
	 * @param parent parent template
	 */
	public void setParent(JstlTemplateView parent)
	{
		this.parent = parent;
	}

	/**
	 * Adds the template map to the request attribute named by
	 * {@link #setTemplateName(String)}.
	 * @param request HTTP request
	 * @throws Exception if an error occurs
	 */
	@Override
	protected void exposeHelpers(HttpServletRequest request) throws Exception
	{
		super.exposeHelpers(request);

		Map<String, Object> templateMap = new HashMap<String, Object>();
		populateTemplateMap(templateMap);

		request.setAttribute(getTemplateName(), templateMap);
	}

	/**
	 * Populates the given template map with attributes for this template.
	 * 
	 * <p> This method merges the template map with that of the parent view if it
	 * is found. </p>
	 * 
	 * @param templateMap map of key / value pairs
	 */
	protected void populateTemplateMap(Map<String, Object> templateMap)
	{
		if (parent != null)
			parent.populateTemplateMap(templateMap);
		if (attributes != null)
			templateMap.putAll(attributes);
	}

	/**
	 * Gets the request attribute to store the template map under.
	 * 
	 * <p> This method delegates to the parent template if no name is found. </p>
	 * @return template name
	 */
	protected String getTemplateName()
	{
		if (templateName != null)
			return templateName;
		if (parent != null)
			return parent.getTemplateName();
		return DEFAULT_TEMPLATE_NAME;
	}

}
