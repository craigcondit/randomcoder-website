package org.randomcoder.bo;

/**
 * Application information business interface.
 */
public interface AppInfoBusiness
{
	/**
	 * Gets the name of the application.
	 * 
	 * @return application name
	 */
	public String getApplicationName();

	/**
	 * Gets the version string of the application.
	 * 
	 * @return version
	 */
	public String getApplicationVersion();
}