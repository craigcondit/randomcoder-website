package org.randomcoder.download;

import java.util.*;

import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

/**
 * Controller which generates download links. 
 * 
 * <pre>
 * Copyright (c) 2007, Craig Condit. All rights reserved.
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
public class DownloadController extends AbstractCommandController
{
	private String viewName;
	private PackageListProducer packageListProducer;
	private int maximumVersionCount = 1;
	
	/**
	 * Sets the maximum number of versions to display per project.
	 * @param maximumVersionCount maximum number of versions
	 */
	public void setMaximumVersionCount(int maximumVersionCount)
	{
		this.maximumVersionCount = maximumVersionCount;
	}
	
	/**
	 * Sets the name of the view to display.
	 * @param viewName view name
	 */
	@Required
	public void setViewName(String viewName)
	{
		this.viewName = viewName;
	}
	
	/**
	 * Sets the PackageListProducer implementation to use.
	 * @param packageListProducer package list producer
	 */
	@Required
	public void setPackageListProducer(PackageListProducer packageListProducer)
	{
		this.packageListProducer = packageListProducer;
	}
	
	/**
	 * Generates a list of packages and forwards to the default view.
	 * @param request HTTP request
	 * @param response HTTP response
	 * @param command command object
	 * @param errors error object
	 * @throws Exception if an error occurs
	 */
	@Override
	protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception
	{
		DownloadCommand form = (DownloadCommand) command;
		
		ModelAndView mav = new ModelAndView(viewName);
		
		List<Package> packages = packageListProducer.getPackages();
		
		String packageName = form.getPackageName();
		if (StringUtils.isEmpty(packageName))
		{
			mav.addObject("packages", packages);			
		}
		else
		{
			List<Package> filtered = new ArrayList<Package>();
			for (Package pkg : packages)
			{
				if (packageName.equals(pkg.getName()))
					filtered.add(pkg);
			}
			mav.addObject("packages", filtered);						
			mav.addObject("packageName", packageName);
		}
		mav.addObject("showAll", form.isShowAll());
		mav.addObject("maximumVersionCount", form.isShowAll() ? Integer.MAX_VALUE : maximumVersionCount);
		
		return mav;
	}

}
