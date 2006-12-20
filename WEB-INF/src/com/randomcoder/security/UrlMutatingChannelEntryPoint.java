package com.randomcoder.security;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import org.acegisecurity.securechannel.ChannelEntryPoint;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Required;

/**
 * Acegi ChannelEntryPoint implementation which wraps an existing implemention
 * but provides URL-mutating capabilities.
 * <p>
 * Specifically, a user-defined list of URL suffixes may be removed from the
 * URL passed to the underlying implementation.
 * </p> 
 * 
 * <p>
 * This is useful to remove welcome-file patterns from the redirect sent to
 * the browser on HTTP / HTTPS state transitions.</p>
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
public class UrlMutatingChannelEntryPoint implements ChannelEntryPoint
{
	private static final Log logger = LogFactory.getLog(UrlMutatingChannelEntryPoint.class);
	
	private ChannelEntryPoint channelEntryPoint;
	private String[] suffixes =
		new String[] { "/index.jsp", "/index.html", "/index.htm" };
	private String replacement = "/";
	
	/**
	 * Sets the ChannelEntryPoint to wrap.
	 * @param channelEntryPoint channel entry point
	 */
	@Required
	public void setChannelEntryPoint(ChannelEntryPoint channelEntryPoint)
	{
		this.channelEntryPoint = channelEntryPoint;
	}
	
	/**
	 * Sets the list of suffixes to strip from URLs.
	 * <p>
	 * Defaults to { /index.jsp, /index.html, /index.htm }.
	 * @param suffixes array of suffixes to strip
	 */
	public void setSuffixes(String[] suffixes)
	{
		this.suffixes = suffixes;
	}
	
	/**
	 * Sets a single suffix to replace.
	 * @param suffix suffix
	 */
	public void setSuffix(String suffix)
	{
		this.suffixes = new String[] { suffix };
	}

	/**
	 * Sets the replacement to append when a suffix is removed.
	 * <p>Default implementation uses a single "/".</p>
	 * @param replacement replacement suffix
	 */
	public void setReplacement(String replacement)
	{
		this.replacement = replacement;
	}
	
	/**
	 * Processes this channel.
	 * <p>
	 * This method delegates to the underlying channel entry point, substituting
	 * an alternate ServletRequest if url-rewriting is necessary.
	 * </p>
	 * @param request servlet request
	 * @param response servlet response
	 * @throws IOException if an I/O error occurs
	 * @throws ServletException if any other error occurs 
	 */
	public void commence(ServletRequest request, ServletResponse response) throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest) request;
		
    String pathInfo = req.getPathInfo();
    String servletPath = req.getServletPath();
    
    for (String suffix : suffixes)
    {
    	logger.debug("Testing for suffix: " + suffix);
    	
    	if (pathInfo == null)
    	{
    		// process servlet path
    		if (servletPath.endsWith(suffix))
    		{    			
    			String replacedServletPath = servletPath.substring(0, servletPath.lastIndexOf(suffix)) + replacement;
    			logger.debug("Replaced servletPath: original=" + servletPath + " replaced=" + replacedServletPath);
    			req = new PathMutableHttpServletRequestWrapper(req, replacedServletPath, pathInfo);
    			break;
    		}
    	}
    	else
    	{
    		// process pathInfo
    		if (pathInfo.endsWith(suffix))
    		{
    			String replacedPathInfo = pathInfo.substring(0, servletPath.lastIndexOf(suffix)) + replacement;
    			logger.debug("Replaced pathInfo: original=" + pathInfo + " replaced=" + replacedPathInfo);
    			req = new PathMutableHttpServletRequestWrapper(req, servletPath, replacedPathInfo);
    			
    			break;
    		}
    	}
    }
    
		channelEntryPoint.commence(req, response);
	}
	
	/**
	 * HttpServletRequestWrapper which overrides servletPath and pathInfo. 
	 */
	private static class PathMutableHttpServletRequestWrapper extends HttpServletRequestWrapper
	{
		private String servletPath;
		private String pathInfo;
		
		/**
		 * Creates a new wrapped HttpServletRequest using the given wrapped object.
		 * @param wrapped wrapped request
		 * @param servletPath servlet path
		 * @param pathInfo path info
		 */
		public PathMutableHttpServletRequestWrapper(HttpServletRequest wrapped, String servletPath, String pathInfo)
		{
			super(wrapped);
			this.servletPath = servletPath;
			this.pathInfo = pathInfo;
		}

		/**
		 * Gets the path info for this request.
		 * @return path info
		 */
		@Override
		public String getPathInfo()
		{
			return pathInfo;
		}		
		
		/**
		 * Gets the servlet path for this request.
		 * @return servlet path
		 */
		@Override
		public String getServletPath()
		{
			return servletPath;
		}
	}
}
