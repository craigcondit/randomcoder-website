package org.randomcoder.download.cache;

import static org.junit.Assert.*;

import java.util.*;

import net.sf.ehcache.*;

import org.junit.*;
import org.randomcoder.download.*;
import org.randomcoder.download.Package;

@SuppressWarnings("javadoc")
public class CachingPackageListProducerTest
{
	CachingPackageListProducer producer;
	MockPackageListProducer target;
	CacheManager cacheManager;

	@Before
	public void setUp()
	{
		cacheManager = new CacheManager(getClass().getResource("/ehcache-test.xml"));
		Cache cache = cacheManager.getCache("test");
		
		producer = new CachingPackageListProducer();		
		producer.setCache(cache);
		producer.setCacheKey("test");
		target = new MockPackageListProducer();
		producer.setTarget(target);
	}

	@After
	public void tearDown()
	{
		producer = null;
		cacheManager.shutdown();
		cacheManager = null;
	}
	
	@Test
	public void testGetPackages() throws Exception
	{
		List<Package> packages = producer.getPackages();
		assertNotNull("Null list", packages);
		List<Package> packages2 = producer.getPackages(); // should be cached
		assertNotNull("Null cached list", packages2);
		assertSame("Second list not cached", packages, packages2);
	}
	
	@Test
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