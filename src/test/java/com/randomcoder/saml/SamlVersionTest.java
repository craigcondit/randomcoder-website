package com.randomcoder.saml;

import junit.framework.TestCase;

public class SamlVersionTest extends TestCase
{
	public void testGetDescription()
	{		
		assertEquals("SAML 1.0", SamlVersion.SAML_1_0.getDescription());
		assertEquals("SAML 1.1", SamlVersion.SAML_1_1.getDescription());
		assertEquals("SAML 2.0", SamlVersion.SAML_2_0.getDescription());				
	}
	
	public void testValues()
	{
		SamlVersion[] versions = SamlVersion.values();
		assertNotNull(versions);
		assertEquals(3, versions.length);		
	}

	public void testValueOf()
	{
		assertEquals(SamlVersion.SAML_1_0, SamlVersion.valueOf("SAML_1_0"));
		assertEquals(SamlVersion.SAML_1_1, SamlVersion.valueOf("SAML_1_1"));
		assertEquals(SamlVersion.SAML_2_0, SamlVersion.valueOf("SAML_2_0"));
	}
}