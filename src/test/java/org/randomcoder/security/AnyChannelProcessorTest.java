package org.randomcoder.security;

import junit.framework.TestCase;

import org.randomcoder.test.mock.acegisecurity.ConfigAttributeMock;

public class AnyChannelProcessorTest extends TestCase
{
	private AnyChannelProcessor processor = null;
	
	@Override
	public void setUp() throws Exception
	{
		processor = new AnyChannelProcessor();
	}

	@Override
	public void tearDown() throws Exception
	{
		processor = null;
	}

	public void testDecide() throws Exception
	{
		// this class does nothing on decide anyway...
		processor.decide(null, null);
	}

	public void testSupports()
	{
		assertTrue(processor.supports(new ConfigAttributeMock("REQUIRES_ANY")));
		assertFalse(processor.supports(new ConfigAttributeMock("REQUIRES_INSECURE_CHANNEL")));
		assertFalse(processor.supports(new ConfigAttributeMock("REQUIRES_SECURE_CHANNEL")));
	}	
}
