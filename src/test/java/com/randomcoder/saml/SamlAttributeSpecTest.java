package com.randomcoder.saml;

import junit.framework.TestCase;

public class SamlAttributeSpecTest extends TestCase
{
	public void testEqualsAndHashCode()
	{
		SamlAttributeSpec spec1 = new SamlAttributeSpec("ns", "local");
		SamlAttributeSpec spec2 = new SamlAttributeSpec("ns", "local");		
		assertEquals(spec1.hashCode(), spec2.hashCode());
		assertEquals(spec1, spec2);
	}

	public void testConstructor()
	{
		SamlAttributeSpec spec = new SamlAttributeSpec("ns", "local");
		assertEquals("ns", spec.getNamespace());
		assertEquals("local", spec.getLocal());
	}

	public void testConstructorMissingNs()
	{
		try
		{
			new SamlAttributeSpec(null, "local");
			fail("IllegalArgumentException expected");
		}
		catch (IllegalArgumentException e)
		{
			// pass
		}
	}

	public void testConstructorMissingLocal()
	{
		try
		{
			new SamlAttributeSpec("ns", null);
			fail("IllegalArgumentException expected");
		}
		catch (IllegalArgumentException e)
		{
			// pass
		}
	}
	
	public void testToString()
	{
		SamlAttributeSpec spec = new SamlAttributeSpec("ns", "local");
		assertEquals("ns:local", spec.toString());
	}
}
