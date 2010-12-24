package com.randomcoder.download.cache;

import java.util.List;

import net.sf.ehcache.*;

import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Required;

import com.randomcoder.download.*;
import com.randomcoder.download.Package;

/**
 * Package list producer which caches lookups to an underlying target.
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
public class CachingPackageListProducer implements PackageListProducer
{
	private static final Log logger = LogFactory.getLog(CachingPackageListProducer.class);
	
	private Cache cache;
	private String cacheKey;
	private PackageListProducer target;

	/**
	 * Sets the EHCache instance to use.
	 * @param cache cache instance
	 */
	@Required
	public void setCache(Cache cache)
	{
		this.cache = cache;
	}
	
	/**
	 * Sets the key to use for populating the cache.
	 * @param cacheKey cache key
	 */
	@Required
	public void setCacheKey(String cacheKey)
	{
		this.cacheKey = cacheKey;
	}
	
	/**
	 * Sets the target package list producer to execute.
	 * @param target target package list producer
	 */
	@Required
	public void setTarget(PackageListProducer target)
	{
		this.target = target;
	}
	
	/**
	 * Gets a list of packages, possibly from a cache.
	 * @throws PackageListException if an error occurs
	 * @return list of packages
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Package> getPackages() throws PackageListException
	{
		logger.debug("Loading packages for target: " + target);
		Element element = cache.get(cacheKey);
		
		if (element != null) {
			logger.debug("Using cached value");
			return (List) element.getValue();		
		}
			
		logger.debug("Calling target");
		List<Package> result = target.getPackages();
		cache.put(new Element(cacheKey, result));
		return result;
	}
	
	/**
	 * Refreshes the cache with updated data
	 * @throws PackageListException if an error occurs
	 */
	public void refresh() throws PackageListException
	{
		logger.debug("Refreshing cache for target: " + target);
		
		List<Package> result = target.getPackages();
		cache.put(new Element(cacheKey, result));		
	}
}
