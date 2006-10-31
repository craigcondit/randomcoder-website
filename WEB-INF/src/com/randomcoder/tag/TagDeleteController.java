package com.randomcoder.tag;

import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import com.randomcoder.springmvc.IdCommand;

/**
 * Controller class which handles tag deletion.
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
public class TagDeleteController extends AbstractCommandController
{
	private TagBusiness tagBusiness;
	private String viewName;

	/**
	 * Sets the TagBusiness implementation to use.
	 * @param tagBusiness TagBusiness implementation
	 */
	@Required
	public void setTagBusiness(TagBusiness tagBusiness)
	{
		this.tagBusiness = tagBusiness;
	}

	/**
	 * Sets the name of the view to pass control to once processing is complete.
	 * @param viewName view name
	 */
	@Required
	public void setViewName(String viewName)
	{
		this.viewName = viewName;
	}

	/**
	 * Deletes the selected tag.
	 */
	@Override
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
	{
		IdCommand cmd = (IdCommand) command;
		
		tagBusiness.deleteTag(cmd.getId());

		return new ModelAndView(viewName);
	}

}
