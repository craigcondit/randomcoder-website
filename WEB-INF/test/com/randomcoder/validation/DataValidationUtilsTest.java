package com.randomcoder.validation;

import static com.randomcoder.validation.DataValidationUtils.*;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;

import org.junit.Test;

public class DataValidationUtilsTest
{
	private static final String MAX_DOMAIN_SEGMENT = "1234567890123456789012345678901234567890123456789012345678901234567";
	private static final String MAX_DOMAIN_LENGTH = "123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.1.com";
	
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
		assertTrue(MAX_DOMAIN_SEGMENT + ".com", isValidDomainName(MAX_DOMAIN_SEGMENT + ".com"));
		assertFalse(MAX_DOMAIN_SEGMENT + "x.com", isValidDomainName(MAX_DOMAIN_SEGMENT + "x.com"));
		assertTrue(MAX_DOMAIN_LENGTH, isValidDomainName(MAX_DOMAIN_LENGTH));
		assertFalse("x" + MAX_DOMAIN_LENGTH, isValidDomainName("x" + MAX_DOMAIN_LENGTH));
	}

	@Test
	public void testIsValidDomainWildcard()
	{
		assertFalse("null", isValidDomainWildcard(null));
		assertFalse("too long", isValidDomainWildcard("x" + MAX_DOMAIN_LENGTH));
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
		assertTrue("abcdefghijklmnopqrstuvwxyz78901234567890123456789012345678901234", isValidLocalEmailAccount("abcdefghijklmnopqrstuvwxyz78901234567890123456789012345678901234"));
		assertFalse("abcdefghijklmnopqrstuvwxyz789012345678901234567890123456789012345", isValidLocalEmailAccount("abcdefghijklmnopqrstuvwxyz789012345678901234567890123456789012345"));
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
		assertFalse("null", isValidIpAddress(null));
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
		String[] nullResults = splitEmailAddress(null);
		assertEquals("null length", 2, nullResults.length);
		assertNull("null local", nullResults[0]);
		assertNull("null domain", nullResults[1]);
		
		assertEquals("test", splitEmailAddress("test@example.com")[0]);
		assertEquals("example.com", splitEmailAddress("test@example.com")[1]);
		
		assertEquals("test\\@ing", splitEmailAddress("test\\@ing@example.com")[0]);
		assertEquals("example.com", splitEmailAddress("test\\@ing@example.com")[1]);
		
		assertEquals("\"quoted@address\"", splitEmailAddress("\"quoted@address\"@example.com")[0]);
		assertEquals("example.com", splitEmailAddress("\"quoted@address\"@example.com")[1]);
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
		assertFalse("test@-.example.com", isValidEmailAddress("test@-.example.com", false, false, true));
		

		// non-dns safe
		assertTrue("test\\@ing@example.com", isValidEmailAddress("test\\@ing@example.com"));
		assertFalse("test\\@ing@example.com", isValidEmailAddress("test\\@ing@example.com", true, false, false));
		
		// null
		assertFalse("null", isValidEmailAddress(null));
		
		// no domain
		assertFalse("no domain", isValidEmailAddress("local", false, false, false));
		assertTrue("no domain", isValidEmailAddress("local", false, true, false));
		
		// quoted
		assertTrue("\"test@local\"@domain.com", isValidEmailAddress("\"test@local\"@domain.com"));
		
		// escapes
		assertFalse("\\@domain.com", isValidEmailAddress("\\@domain.com"));
		assertTrue("\\\u0001@domain.com", isValidEmailAddress("\\\u0001@domain.com"));
		assertTrue("\\\u000b@domain.com", isValidEmailAddress("\\\u000b@domain.com"));
		assertTrue("\\\u000c@domain.com", isValidEmailAddress("\\\u000c@domain.com"));
		assertTrue("\\\u000f@domain.com", isValidEmailAddress("\\\u000f@domain.com"));
		assertFalse("\\\u0080@domain.com", isValidEmailAddress("\\\u0080@domain.com"));
		assertFalse("\\@domain.com", isValidEmailAddress("\\@domain.com"));
		assertFalse("\\@domain.com", isValidEmailAddress("\\@domain.com"));
		assertFalse("\"test\\\"@domain.com", isValidEmailAddress("\"test\\\"@domain.com"));
		assertTrue("\"test\u0008@local\"@domain.com", isValidEmailAddress("\"test\u0008@local\"@domain.com"));
		assertTrue("\"test\\\t@local\"@domain.com", isValidEmailAddress("\"test\\\t@local\"@domain.com"));
		assertTrue("\"test\u000b@local\"@domain.com", isValidEmailAddress("\"test\u000b@local\"@domain.com"));
		assertTrue("\"test\\\u000b@local\"@domain.com", isValidEmailAddress("\"test\\\u000b@local\"@domain.com"));
		assertTrue("\"test\u000c@local\"@domain.com", isValidEmailAddress("\"test\u000c@local\"@domain.com"));
		assertTrue("\"test\\\u000c@local\"@domain.com", isValidEmailAddress("\"test\\\u000c@local\"@domain.com"));
		assertTrue("\"test\u000e@local\"@domain.com", isValidEmailAddress("\"test\u000e@local\"@domain.com"));
		assertTrue("\"test\\\u000e@local\"@domain.com", isValidEmailAddress("\"test\\\u000e@local\"@domain.com"));
		assertTrue("\"test\u007f@local\"@domain.com", isValidEmailAddress("\"test\u007f@local\"@domain.com"));
		assertTrue("\"test\\\u007f@local\"@domain.com", isValidEmailAddress("\"test\\\u007f@local\"@domain.com"));
		assertFalse("\"test\u0080@local\"@domain.com", isValidEmailAddress("\"test\u0080@local\"@domain.com"));
		assertFalse("\"test\\\u0080@local\"@domain.com", isValidEmailAddress("\"test\\\u0080@local\"@domain.com"));
		}

	@Test
	public void testCanonicalizeTagName()
	{
		assertNull(canonicalizeTagName(null));
		assertEquals("testing-1", canonicalizeTagName("Testing 1"));
		assertEquals("testing-2", canonicalizeTagName("Testing_2"));
		assertEquals("testing-3", canonicalizeTagName("Testing/3"));
		assertEquals("testing-4", canonicalizeTagName("Testing\\4"));
		assertEquals("testing-5", canonicalizeTagName(" Testing  5 "));
		assertEquals("testing-6", canonicalizeTagName("Testing\t6"));
	}
	
	/**
	 * Not a test, but tickles the private constructor for full
	 * test coverage. 
	 */
	@Test public void coverDefaultConstructor() throws Exception
	{
		Constructor c = DataValidationUtils.class.getDeclaredConstructor(new Class[] {});
		c.setAccessible(true);
		c.newInstance(new Object[] {});
	}
}
