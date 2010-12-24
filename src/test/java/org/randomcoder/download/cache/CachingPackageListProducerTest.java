package org.randomcoder.download.cache;

import java.util.*;

import junit.framework.TestCase;
import net.sf.ehcache.*;

import org.randomcoder.download.*;
import org.randomcoder.download.Package;

public class CachingPackageListProducerTest extends TestCase
{
	CachingPackageListProducer producer;
	MockPackageListProducer target;
	CacheManager cacheManager;
	
	@Override
	protected void setUp() throws Exception
	{
		cacheManager = new CacheManager(getClass().getResource("/ehcache-test.xml"));
		Cache cache = cacheManager.getCache("test");
		
		producer = new CachingPackageListProducer();		
		producer.setCache(cache);
		producer.setCacheKey("test");
		target = new MockPackageListProducer();
		producer.setTarget(target);
	}

	@Override
	protected void tearDown() throws Exception
	{
		producer = null;
		cacheManager.shutdown();
		cacheManager = null;
	}
	
	public void testGetPackages() throws Exception
	{
		List<Package> packages = producer.getPackages();
		assertNotNull("Null list", packages);
		List<Package> packages2 = producer.getPackages(); // should be cached
		assertNotNull("Null cached list", packages2);
		assertSame("Second list not cached", packages, packages2);
	}
	
	public void testReset() throws Exception
	{
		List<Package> packages = producer.getPackages();
		assertNotNull("Null list", packages);
		List<Package> packages2 = producer.getPackages(); // should be cached
		assertNotNull("Null cached list", packages2);
		assertSame("Second list not cached", packages, packages2);
		producer.refresh();
		List<Package> packages3 = producer.getPackages(); // should not be cached
		assertNotNull("Null third list", packages3);
		assertNotSame("Third list cached", packages2, packages3);
	}

	class MockPackageListProducer implements PackageListProducer
	{
		@Override
		public List<Package> getPackages() throws PackageListException
		{
			return new ArrayList<Package>();
		}		
	}
}
