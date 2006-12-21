package com.randomcoder.saml;

import static com.randomcoder.test.TestObjectFactory.RESOURCE_SAML_ASSERTION_TEST;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.security.PrivateKey;
import java.util.*;

import org.junit.*;
import org.w3c.dom.*;

import com.randomcoder.test.TestObjectFactory;
import com.randomcoder.xml.security.XmlSecurityUtils;

public class SamlUtilsTest
{
	private Document encryptedData;
	private PrivateKey serverPrivateKey;
	
	@Before
	public void setUp() throws Exception
	{		
		serverPrivateKey = TestObjectFactory.getPrivateKey();
		encryptedData = TestObjectFactory.getXmlDocument(RESOURCE_SAML_ASSERTION_TEST);
	}

	@After
	public void tearDown() throws Exception
	{
		encryptedData = null;
		serverPrivateKey = null;
	}
	
	@Test
	public void testFindFirstSamlAssertion() throws Exception
	{
		// does not exist in encrypted data
		assertNull(SamlUtils.findFirstSamlAssertion(encryptedData));
		
		// decrypt token
		Element el = XmlSecurityUtils.findFirstEncryptedData(encryptedData);
		XmlSecurityUtils.decrypt(encryptedData, el, serverPrivateKey);
		
		assertNotNull(SamlUtils.findFirstSamlAssertion(encryptedData));
	}
	
	@Test
	public void testParseXsdDateTime() throws Exception
	{
		Date date = SamlUtils.parseXsdDateTime("2006-12-19T18:23:21.576Z");
		assertNotNull(date);
		
		Calendar result = Calendar.getInstance(Locale.US);
		result.setTime(date);
		result.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		assertEquals("Year", 2006, result.get(Calendar.YEAR));
		assertEquals("Month", Calendar.DECEMBER, result.get(Calendar.MONTH));
		assertEquals("Day", 19, result.get(Calendar.DAY_OF_MONTH));
		assertEquals("Hour", 18, result.get(Calendar.HOUR_OF_DAY));
		assertEquals("Minute", 23, result.get(Calendar.MINUTE));
		assertEquals("Second", 21, result.get(Calendar.SECOND));		
	}
	
	/**
	 * Not a test, but tickles the private constructor.
	 */
	@Test
	public void coverDefaultConstructor() throws Exception
	{
		Constructor c = SamlUtils.class.getDeclaredConstructor(new Class[] {});
		c.setAccessible(true);
		c.newInstance(new Object[] {});
	}		
}
