package com.randomcoder.saml.test;

import static org.junit.Assert.*;

import org.junit.*;

import com.randomcoder.saml.SamlVersion;

public class SamlVersionTest
{
	
	@Test
	public void testGetDescription()
	{		
		assertEquals("SAML 1.0", SamlVersion.SAML_1_0.getDescription());
		assertEquals("SAML 1.1", SamlVersion.SAML_1_1.getDescription());
		assertEquals("SAML 2.0", SamlVersion.SAML_2_0.getDescription());				
	}
	
	@Test
	public void testValues()
	{
		SamlVersion[] versions = SamlVersion.values();
		assertNotNull(versions);
		assertEquals(3, versions.length);		
	}

	@Test
	public void testValueOf()
	{
		assertEquals(SamlVersion.SAML_1_0, SamlVersion.valueOf("SAML_1_0"));
		assertEquals(SamlVersion.SAML_1_1, SamlVersion.valueOf("SAML_1_1"));
		assertEquals(SamlVersion.SAML_2_0, SamlVersion.valueOf("SAML_2_0"));
	}
	
}
