package org.randomcoder.website.bo;

import org.glassfish.jersey.spi.Contract;

/**
 * Application information business interface.
 */
@Contract
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