package org.randomcoder.download.cache;

import java.util.List;

import net.sf.ehcache.*;

import org.apache.commons.logging.*;
import org.randomcoder.download.*;
import org.randomcoder.download.Package;
import org.springframework.beans.factory.annotation.Required;

/**
 * Package list producer which caches lookups to an underlying target.
 */
public class CachingPackageListProducer implements PackageListProducer
{
	private static final Log logger = LogFactory.getLog(CachingPackageListProducer.class);

	private Cache cache;
	private String cacheKey;
	private PackageListProducer target;

	/**
	 * Sets the EHCache instance to use.
	 * 
	 * @param cache
	 *          cache instance
	 */
	@Required
	public void setCache(Cache cache)
	{
		this.cache = cache;
	}

	/**
	 * Sets the key to use for populating the cache.
	 * 
	 * @param cacheKey
	 *          cache key
	 */
	@Required
	public void setCacheKey(String cacheKey)
	{
		this.cacheKey = cacheKey;
	}

	/**
	 * Sets the target package list producer to execute.
	 * 
	 * @param target
	 *          target package list producer
	 */
	@Required
	public void setTarget(PackageListProducer target)
	{
		this.target = target;
	}

	/**
	 * Gets a list of packages, possibly from a cache.
	 * 
	 * @throws PackageListException
	 *           if an error occurs
	 * @return list of packages
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Package> getPackages() throws PackageListException
	{
		logger.debug("Loading packages for target: " + target);
		Element element = cache.get(cacheKey);

		if (element != null)
		{
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
	 * 
	 * @throws PackageListException
	 *           if an error occurs
	 */
	public void refresh() throws PackageListException
	{
		logger.debug("Refreshing cache for target: " + target);

		List<Package> result = target.getPackages();
		cache.put(new Element(cacheKey, result));
	}
}
