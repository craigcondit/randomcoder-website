package org.randomcoder.website.bo;

import org.glassfish.hk2.api.Immediate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.time.Instant;
import java.util.Properties;

@Immediate
public class AppInfoBusinessImpl implements AppInfoBusiness {

    private static final Logger logger = LoggerFactory.getLogger(AppInfoBusinessImpl.class);

    private static final String APP_NAME_PROPERTY = "application.name";
    private static final String APP_VERSION_PROPERTY = "application.version";
    private static final String APP_BUILD_DATE_PROPERTY = "application.build.date";
    private static final String DEFAULT_APP_NAME = "Randomcoder Website";
    private static final String DEFAULT_APP_VERSION = "Unknown";

    private final String applicationName;
    private final String applicationVersion;
    private final Instant buildDate;

    public AppInfoBusinessImpl() throws Exception {
        Properties p = new Properties();

        try (InputStream is = getClass().getResourceAsStream("/org/randomcoder/website/version.properties")) {
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

        String appBuildDate = p.getProperty(APP_BUILD_DATE_PROPERTY);
        Instant buildDate;
        try {
            buildDate = Instant.parse(appBuildDate);
        } catch (Exception e) {
            logger.debug("Unable to parse build date, using current time");
            buildDate = Instant.now();
        }

        logger.info("Starting application: {}/{} (build date {})", appName, appVersion, buildDate);

        this.buildDate = buildDate;
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

    @Override
    public Instant getBuildDate() {
        return buildDate;
    }

}