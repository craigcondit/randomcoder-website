package org.randomcoder.about;

import junit.framework.TestCase;

import org.springframework.core.io.*;

public class ApplicationInformationFactoryBeanTest extends TestCase
{
	private ApplicationInformationFactoryBean bean; 
	
	@Override
	protected void setUp() throws Exception
	{
		bean = new ApplicationInformationFactoryBean();
		bean.setPropertyFile(new ClassPathResource("/version-test.properties"));
		bean.afterPropertiesSet();
	}

	@Override
	protected void tearDown() throws Exception
	{
		bean = null;
	}

	public void testGetObject() throws Exception
	{
		Object obj = bean.getObject();
		assertNotNull("Null object", obj);
		assertTrue("Wrong object class", obj instanceof ApplicationInformation);
		ApplicationInformation info = (ApplicationInformation) obj;
		assertEquals("Test-Application", info.getApplicationName());
		assertEquals("1.0.0", info.getApplicationVersion());
	}

	public void testGetObjectType()
	{
		assertEquals("Wrong object type", ApplicationInformation.class, bean.getObjectType());
	}

	public void testIsSingleton()
	{
		assertTrue("Not singleton", bean.isSingleton());
	}
}
