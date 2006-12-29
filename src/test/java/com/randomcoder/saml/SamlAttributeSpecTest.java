package com.randomcoder.saml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SamlAttributeSpecTest
{
	@Test
	public void testEqualsAndHashCode()
	{
		SamlAttributeSpec spec1 = new SamlAttributeSpec("ns", "local");
		SamlAttributeSpec spec2 = new SamlAttributeSpec("ns", "local");		
		assertEquals(spec1.hashCode(), spec2.hashCode());
		assertEquals(spec1, spec2);
	}

	@Test
	public void testConstructor()
	{
		SamlAttributeSpec spec = new SamlAttributeSpec("ns", "local");
		assertEquals("ns", spec.getNamespace());
		assertEquals("local", spec.getLocal());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConstructorMissingNs()
	{
		new SamlAttributeSpec(null, "local");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConstructorMissingLocal()
	{
		new SamlAttributeSpec("ns", null);
	}
	
	@Test
	public void testToString()
	{
		SamlAttributeSpec spec = new SamlAttributeSpec("ns", "local");
		assertEquals("ns:local", spec.toString());
	}

}
