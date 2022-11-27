package org.randomcoder.website.bo;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

@Singleton
public class AppInfoBusinessImpl implements AppInfoBusiness {
    private static final Logger logger = LoggerFactory.getLogger(AppInfoBusinessImpl.class);

    private static final String APP_NAME_PROPERTY = "application.name";
    private static final String APP_VERSION_PROPERTY = "application.version";
    private static final String DEFAULT_APP_NAME = "Randomcoder Website";
    private static final String DEFAULT_APP_VERSION = "Unknown";

    private String applicationName;
    private String applicationVersion;

    public AppInfoBusinessImpl() throws Exception {
        Properties p = new Properties();

        try (InputStream is = getClass().getResourceAsStream("/version.properties")) {
            p.load(is);
        }

        String appName = p.getProperty(APP_NAME_PROPERTY);
        if (appName == null || appName.contains("${")) {
            appName = DEFAULT_APP_NAME;
        }

        String appVersion = p.getProperty(APP_VERSION_PROPERTY);
        if (appVersion == null || appVersion.contains("${")) {
            appVersion = DEFAULT_APP_VERSION;
        }

        logger.info("Starting application: " + appName + "/" + appVersion);

        this.applicationName = appName;
        this.applicationVersion = appVersion;
    }

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public String getApplicationVersion() {
        return applicationVersion;
    }

}