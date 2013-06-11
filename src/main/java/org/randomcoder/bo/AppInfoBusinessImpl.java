package org.randomcoder.bo;

import java.io.*;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * JavaBean which holds application information.
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
@Component("appInfoBusiness")
public class AppInfoBusinessImpl implements AppInfoBusiness
{
	private static final Log logger = LogFactory.getLog(AppInfoBusinessImpl.class);

	private static final String APP_NAME_PROPERTY = "application.name";
	private static final String APP_VERSION_PROPERTY = "application.version";
	private static final String DEFAULT_APP_NAME = "Randomcoder Website";
	private static final String DEFAULT_APP_VERSION = "Unknown";

	private String applicationName;
	private String applicationVersion;

	/**
	 * Sets the location of the property file.
	 * 
	 * @param propertyFile
	 *            property file location
	 * @throws IOException
	 *             if an error occurs
	 */
	@Inject
	@Value("${version.location}")
	public void setPropertyFile(Resource propertyFile) throws IOException
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
			if (is != null)
			{
				is.close();
			}
		}

		String appName = p.getProperty(APP_NAME_PROPERTY);
		if (appName == null || appName.contains("${"))
			appName = DEFAULT_APP_NAME;

		String appVersion = p.getProperty(APP_VERSION_PROPERTY);
		if (appVersion == null || appVersion.contains("${"))
			appVersion = DEFAULT_APP_VERSION;

		logger.info("Starting application: " + appName + "/" + appVersion);

		this.applicationName = appName;
		this.applicationVersion = appVersion;
	}

	@Override
	public String getApplicationName()
	{
		return applicationName;
	}

	@Override
	public String getApplicationVersion()
	{
		return applicationVersion;
	}
}