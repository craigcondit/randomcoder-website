package org.randomcoder.website;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    // config properties
    public static final String HTTP_ADDRESS = "http.address";
    public static final String HTTP_PORT = "http.port";
    public static final String HTTP_SECURE_PORT = "http.secure.port";
    public static final String HTTP_THREADS = "http.threads";
    public static final String HTTP_THREADS_MIN = "http.threads.min";
    public static final String HTTP_FORWARDED = "http.forwarded";
    public static final String HTTP_PROXIED = "http.proxied";
    public static final String HTTPS_FORCED = "https.forced";
    public static final String EXPOSE_EXCEPTION_DETAILS = "expose.exception.details";
    public static final String VERSION_LOCATION = "version.location";
    public static final String DOWNLOAD_MAX_VERSIONS_PER_PACKAGE = "download.max.versions.per.package";
    public static final String MODERATION_BATCH_SIZE = "moderation.batch.size";
    public static final String ARTICLE_MAX_SUMMARY_LENGTH = "article.max.summary.length";
    public static final String USERNAME_LENGTH_MINIMUM = "username.length.minimum";
    public static final String PASSWORD_LENGTH_MINIMUM = "password.length.minimum";
    public static final String TAG_PAGESIZE_MAX = "tag.pagesize.max";
    public static final String ARTICLE_PAGESIZE_MAX = "article.pagesize.max";
    public static final String USER_PAGESIZE_MAX = "user.pagesize.max";
    public static final String DATABASE_URL = "database.url";
    public static final String DATABASE_USERNAME = "database.username";
    public static final String DATABASE_PASSWORD = "database.password";
    public static final String REMEMBERME_KEY = "rememberme.key";
    public static final String AKISMET_SITE_KEY = "akismet.site.key";
    public static final String AKISMET_SITE_URL = "akismet.site.url";
    private static final String ENV_CONFIG_FILE = "CONFIG_FILE";
    private static final String DEFAULT_CONFIG_FILE = "randomcoder-website.conf";
    private static final String RESOURCE_DEFAULT_CONF = "/profiles/default.properties";
    private final Properties props;

    private Config(Properties props) {
        this.props = props;
    }

    public static Config load() throws IOException {
        return new Config(loadConfiguration());
    }

    private static Properties loadConfiguration() throws IOException {
        // load defaults
        Properties defaults = new Properties();
        try (InputStream is = Config.class.getResourceAsStream(RESOURCE_DEFAULT_CONF)) {
            defaults.load(is);
        }

        // get config file
        var configFileName = System.getenv(ENV_CONFIG_FILE);
        if (configFileName == null) {
            configFileName = DEFAULT_CONFIG_FILE;
        }

        File configFile = new File(configFileName);
        if (!configFile.exists()) {
            return defaults;
        }

        Properties conf = new Properties(defaults);
        try (InputStream is = new FileInputStream(configFile)) {
            conf.load(is);
        }

        return conf;
    }

    public String getString(String key) throws IllegalArgumentException {
        var result = props.getProperty(key);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Missing required configuration '%s", key));
        }
        return result;
    }

    public String getStringOrDefault(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public int getInt(String key) throws IllegalArgumentException {
        var string = getString(key);
        return Integer.parseInt(string, 10);
    }

    public long getLong(String key) throws IllegalArgumentException {
        var string = getString(key);
        return Long.parseLong(string, 10);
    }

    public int getIntOrDefault(String key, int defaultValue) throws NumberFormatException {
        var string = props.getProperty(key);
        if (string == null) {
            return defaultValue;
        }
        return Integer.parseInt(string, 10);
    }

    public long getLongOrDefault(String key, long defaultValue) throws NumberFormatException {
        var string = props.getProperty(key);
        if (string == null) {
            return defaultValue;
        }
        return Long.parseLong(string, 10);
    }
    public boolean getBoolean(String key) throws IllegalArgumentException {
        var string = getString(key);
        return Boolean.valueOf(string);
    }

    public boolean getBooleanOrDefault(String key, boolean defaultValue) {
        var string = props.getProperty(key);
        if (string == null) {
            return defaultValue;
        }
        return Boolean.valueOf(string);
    }

}
