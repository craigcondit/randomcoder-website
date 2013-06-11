package org.randomcoder.bo;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;

@SuppressWarnings("javadoc")
public class AppInfoBusinessImplTest extends TestCase
{
	private AppInfoBusinessImpl info;

	@Override
	protected void setUp() throws Exception
	{
		info = new AppInfoBusinessImpl();
		info.setPropertyFile(new ClassPathResource("/version-test.properties"));
	}

	@Override
	protected void tearDown() throws Exception
	{
		info = null;
	}

	public void testGetObject() throws Exception
	{
		assertEquals("Test-Application", info.getApplicationName());
		assertEquals("1.0.0", info.getApplicationVersion());
	}
}