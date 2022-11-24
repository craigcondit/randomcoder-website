package org.randomcoder.bo;

/**
 * Application information business interface.
 */
public interface AppInfoBusiness {
    /**
     * Gets the name of the application.
     *
     * @return application name
     */
    String getApplicationName();

    /**
     * Gets the version string of the application.
     *
     * @return version
     */
    String getApplicationVersion();
}