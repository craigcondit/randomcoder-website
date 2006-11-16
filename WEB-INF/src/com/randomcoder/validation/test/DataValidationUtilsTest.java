package com.randomcoder.validation.test;

import static com.randomcoder.validation.DataValidationUtils.*;
import static org.junit.Assert.*;

import org.junit.*;

public class DataValidationUtilsTest
{
	@Test
	public void testCanonicalizeDomainName()
	{
		assertEquals("test.com", canonicalizeDomainName("TEST.COM"));
		assertEquals("test.com", canonicalizeDomainName("TeSt.CoM"));
		assertNull(canonicalizeDomainName(null));
	}

	@Test
	public void testIsValidDomainName()
	{
		assertTrue("test.com", isValidDomainName("test.com"));
		assertTrue("test.co.uk", isValidDomainName("test.co.uk"));
		assertFalse("-test.com", isValidDomainName("-test.com"));
		assertFalse(".test.com", isValidDomainName(".test.com"));
		assertFalse("test", isValidDomainName("test"));
		assertFalse("*.test.com", isValidDomainName("*.test.com"));
	}

	@Test
	public void testIsValidDomainWildcard()
	{
		assertTrue("*.test.com", isValidDomainWildcard("*.test.com"));
		assertFalse("-.test.com", isValidDomainWildcard("-.test.com"));
		assertFalse("*.", isValidDomainWildcard("*."));
	}

	@Test
	public void testIsValidLocalEmailAccount()
	{
		assertTrue("test", isValidLocalEmailAccount("test"));
		assertTrue("test-123", isValidLocalEmailAccount("test-123"));
		assertTrue("test_123", isValidLocalEmailAccount("test_123"));
		assertTrue("test.123", isValidLocalEmailAccount("test.123"));
		assertFalse("test!123", isValidLocalEmailAccount("test!123"));
		assertFalse("test!123", isValidLocalEmailAccount("test!123"));
		assertFalse("test@123", isValidLocalEmailAccount("test@123"));
		assertFalse("test#123", isValidLocalEmailAccount("test#123"));
		assertFalse("test$123", isValidLocalEmailAccount("test$123"));
		assertFalse("test%123", isValidLocalEmailAccount("test%123"));
	}

	@Test	
	public void testIsValidIpAddress()
	{
		for (int i = 0; i < 256; i++)
		{
			String ip = i + "." + i + "." + i + "." + i;
			assertTrue(ip, isValidIpAddress(ip));
		}
		assertFalse("256.256.256.256", isValidIpAddress("256.256.256.256"));
		assertFalse("0", isValidIpAddress("0"));
		assertFalse("0.", isValidIpAddress("0."));
		assertFalse("0.0", isValidIpAddress("0.0"));
		assertFalse("0.0.", isValidIpAddress("0.0."));
		assertFalse("0.0.0", isValidIpAddress("0.0.0"));
		assertFalse("0.0.0.", isValidIpAddress("0.0.0."));
		assertFalse("0.0.0.0.", isValidIpAddress("0.0.0.0."));
		assertFalse("test.com", isValidIpAddress("test.com"));
		assertFalse("a.b.c.d", isValidIpAddress("a.b.c.d"));
	}

	@Test
	public void testIsValidUrl()
	{
		assertTrue("http:/www.example.com/", isValidUrl("http://www.example.com/"));
		assertTrue("https://www.example.com/", isValidUrl("https://www.example.com/"));
		assertFalse("//www.example.com", isValidUrl("//www.example.com"));
		assertFalse("www.example.com", isValidUrl("www.example.com"));
		assertFalse("ftp://www.example.com/", isValidUrl("ftp://www.example.com/"));
		assertTrue("ftp://www.example.com/", isValidUrl("ftp://www.example.com/", "ftp"));
	}

	@Test
	public void testSplitEmailAddress()
	{
		assertEquals("test", splitEmailAddress("test@example.com")[0]);
		assertEquals("example.com", splitEmailAddress("test@example.com")[1]);
		
		assertEquals("test\\@ing", splitEmailAddress("test\\@ing@example.com")[0]);
		assertEquals("example.com", splitEmailAddress("test\\@ing@example.com")[1]);
	}

	@Test
	public void testIsValidEmailAddress()
	{
		// valid
		assertTrue("test@example.com", isValidEmailAddress("test@example.com"));
		
		// invalid
		assertFalse("test@example.com.", isValidEmailAddress("test@example.com."));

		// local-only
		assertFalse("test", isValidEmailAddress("test"));
		assertTrue("test", isValidEmailAddress("test", false, true, false));
		
		// wildcard
		assertFalse("test@*.example.com", isValidEmailAddress("test@*.example.com"));		
		assertTrue("test@*.example.com", isValidEmailAddress("test@*.example.com", false, false, true));

		// non-dns safe
		assertTrue("test\\@ing@example.com", isValidEmailAddress("test\\@ing@example.com"));
		assertFalse("test\\@ing@example.com", isValidEmailAddress("test\\@ing@example.com", true, false, false));
	}

	@Test
	public void testCanonicalizeTagName()
	{
		assertEquals("testing-1", canonicalizeTagName("Testing 1"));
		assertEquals("testing-2", canonicalizeTagName("Testing_2"));
		assertEquals("testing-3", canonicalizeTagName("Testing/3"));
		assertEquals("testing-4", canonicalizeTagName("Testing\\4"));
		assertEquals("testing-5", canonicalizeTagName(" Testing  5 "));
		assertEquals("testing-6", canonicalizeTagName("Testing\t6"));
	}
}
