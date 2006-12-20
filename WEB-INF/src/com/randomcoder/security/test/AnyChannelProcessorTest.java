package com.randomcoder.security.test;

import static org.junit.Assert.*;

import org.acegisecurity.ConfigAttribute;
import org.junit.*;

import com.randomcoder.security.AnyChannelProcessor;

public class AnyChannelProcessorTest
{
	private AnyChannelProcessor processor = null;
	
	@Before
	public void setUp() throws Exception
	{
		processor = new AnyChannelProcessor();
	}

	@After
	public void tearDown() throws Exception
	{
		processor = null;
	}

	@Test
	public void testDecide() throws Exception
	{
		// this class does nothing on decide anyway...
		processor.decide(null, null);
	}

	@Test
	public void testSupports()
	{
		assertTrue(processor.supports(new ConfigAttributeMock("REQUIRES_ANY")));
		assertFalse(processor.supports(new ConfigAttributeMock("REQUIRES_INSECURE_CHANNEL")));
		assertFalse(processor.supports(new ConfigAttributeMock("REQUIRES_SECURE_CHANNEL")));
	}
	
	private class ConfigAttributeMock implements ConfigAttribute
	{
		private static final long serialVersionUID = -174801702398598227L;
		
		private final String attribute;
		
		public ConfigAttributeMock(String attribute)
		{
			this.attribute = attribute;
		}
		
		public String getAttribute() { return attribute; }
	}
}
