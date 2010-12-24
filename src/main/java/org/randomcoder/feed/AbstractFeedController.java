package org.randomcoder.feed;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;

/**
 * Base class for syndicated feed controllers.
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
abstract public class AbstractFeedController extends AbstractController
{
	private String urlPrefix;
	private FeedGenerator feedGenerator;
	
	/**
	 * Sets the URL prefix for this feed.
	 * 
	 * @param urlPrefix
	 *          URL prefix
	 */
	@Required
	public final void setUrlPrefix(String urlPrefix)
	{
		this.urlPrefix = urlPrefix;
	}
	
	/**
	 * Sets the feed generator to use.
	 * 
	 * @param feedGenerator
	 *          feed generator
	 */
	@Required
	public void setFeedGenerator(FeedGenerator feedGenerator)
	{
		this.feedGenerator = feedGenerator;
	}
	
	@Override
	protected ModelAndView handleRequestInternal(
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		// get parameters
		String path = getAppPath(request);
		
		if (!path.startsWith(urlPrefix))
			throw new IllegalArgumentException("Invalid path: " + path);
		
		path = path.substring(urlPrefix.length());
		
		if (path.startsWith("/"))
			path = path.substring(1);
		
		String[] params = path.split("/");
		
		// get feed data
		FeedInfo feedInfo = getFeed(request, response, params);

		// generate feed
		String feed = feedGenerator.generateFeed(feedInfo);

		// output feed
		byte[] data = feed.getBytes("UTF-8");

		response.setContentType(feedGenerator.getContentType());
		response.setContentLength(data.length);
		
		ServletOutputStream out = null;
		
		try
		{
			out = response.getOutputStream();
			out.write(data);
		}
		finally
		{
			if (out != null) try { out.close(); } catch (Throwable ignored) {}
		}

		return null;
	}

	/**
	 * Gets the information necessary to generate the feed.
	 * 
	 * @param request
	 *          HTTP request
	 * @param response
	 *          HTTP response
	 * @param params
	 *          additional paramaters taken from the path
	 * @return feed information
	 * @throws Exception
	 *           if an error occurs
	 */
	abstract protected FeedInfo getFeed(
			HttpServletRequest request, HttpServletResponse response,
			String... params) throws Exception;
			
	/**
	 * Gets the path of the current request relative to the context path.
	 * 
	 * @param request
	 *          HTTP request
	 * @return app path
	 */
	protected final String getAppPath(HttpServletRequest request)
	{
		UrlPathHelper helper = new UrlPathHelper();
		
		String appPath = helper.getPathWithinApplication(request);
		try
		{
			appPath = URLDecoder.decode(appPath, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException("Unsupported encoding", e);
		}
		
		if (logger.isDebugEnabled())
			logger.debug("appPath: " + appPath);
		
		return appPath;
	}
}
