package com.randomcoder.about;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;

/**
 * Spring FactoryBean to generate ApplicationInformation instances.
 * 
 * <pre>
 * Copyright (c) 2007, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
public class ApplicationInformationFactoryBean implements FactoryBean, InitializingBean
{
	private static final Log logger = LogFactory.getLog(ApplicationInformationFactoryBean.class);
	
	private static final String APP_NAME_PROPERTY = "application.name";
	private static final String APP_VERSION_PROPERTY = "application.version";
	private static final String DEFAULT_APP_NAME = "Randomcoder Website";
	private static final String DEFAULT_APP_VERSION = "Unknown";
	
	private Resource propertyFile;
	private ApplicationInformation info;
	
	/**
	 * Sets the location of the property file
	 * @param propertyFile
	 */
	@Required
	public void setPropertyFile(Resource propertyFile)
	{
		this.propertyFile = propertyFile;
	}
	
	/**
	 * Initializes the factory.
	 * @throws Exception if an error occurs
	 */
	@Override
	public void afterPropertiesSet() throws Exception
	{
		Properties p = new Properties();
		InputStream is = null;
		
		try
		{
			is = propertyFile.getInputStream(); 
			p.load(is);			
		}
		finally
		{
			if (is != null) is.close();
		}
		
		String appName = p.getProperty(APP_NAME_PROPERTY);
		if (appName == null || appName.contains("${"))
			appName = DEFAULT_APP_NAME;
		
		String appVersion = p.getProperty(APP_VERSION_PROPERTY);
		if (appVersion == null || appVersion.contains("${"))
			appVersion = DEFAULT_APP_VERSION;
			
		logger.info("Starting application: " + appName + "/" + appVersion);
		
		info = new ApplicationInformation(appName, appVersion);
	}

	/**
	 * Gets an instance of the object from the factory.
	 * @throws Exception if an error occurs
	 * @return ApplicationInformation instance
	 */
	@Override
	public Object getObject() throws Exception
	{
		return info;
	}

	/**
	 * Gets the class of the returned object.
	 * @return ApplicationInformation.class
	 */
	@Override
	public Class getObjectType()
	{
		return ApplicationInformation.class;
	}

	/**
	 * Determines if the returned object is a singleton.
	 * @return true always
	 */
	@Override
	public boolean isSingleton() { return true; }
}
