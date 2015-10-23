package org.randomcoder;

import java.io.File;
import java.util.Arrays;

import org.randomcoder.config.JettyContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.*;
import org.springframework.core.io.*;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.util.Log4jConfigurer;

/**
 * Startup class for the randomcoder website.
 */
public class WebSite
{
	/**
	 * Configuration directory.
	 */
	public static final String CONFIG_DIR = "/srv/randomcoder-website/etc";

	/**
	 * Configuration file.
	 */
	public static final String CONFIG_FILE = "randomcoder-website.conf";

	private final AnnotationConfigApplicationContext context;

	/**
	 * Creates an instance of the website using default parameters.
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	public WebSite() throws Exception
	{
		this("log4j-", JettyContext.class);
	}

	/**
	 * Create the website with a custom log4j config file prefix and
	 * configuration class.
	 * 
	 * @param log4jPrefix
	 *            log4j prefix
	 * @param configClass
	 *            configuration class
	 * @throws Exception
	 *             if an error occurs
	 */
	public WebSite(String log4jPrefix, Class<?> configClass) throws Exception
	{
		context = new AnnotationConfigApplicationContext();

		ConfigurableEnvironment env = new StandardEnvironment();
		MutablePropertySources propertySources = env.getPropertySources();

		// make sure at least the dev profile is active
		if (env.getActiveProfiles().length == 0)
		{
			env.addActiveProfile("dev");
		}

		boolean dev = Arrays.asList(env.getActiveProfiles()).contains("dev");
		Log4jConfigurer.initLogging("classpath:" + log4jPrefix + (dev ? "dev" : "prod") + ".xml");

		File configDir = new File(CONFIG_DIR);

		// add profile-specific config files
		for (String profile : env.getActiveProfiles())
		{
			File profileConfigFile = new File(configDir, CONFIG_FILE + "." + profile);
			if (profileConfigFile.exists())
			{
				propertySources.addLast(new ResourcePropertySource(new FileSystemResource(profileConfigFile)));
			}

			ClassPathResource res = new ClassPathResource("/profiles/" + profile + ".properties");
			if (res.exists())
			{
				propertySources.addLast(new ResourcePropertySource(res));
			}
		}

		// add default config file (if it exists)
		File configFile = new File(configDir, CONFIG_FILE);
		if (configFile.exists())
		{
			propertySources.addLast(new ResourcePropertySource(new FileSystemResource(configFile)));
		}

		// add default built-in properties (if they exist)
		propertySources.addLast(new ResourcePropertySource(new ClassPathResource("/profiles/default.properties")));

		context.setEnvironment(env);
		context.register(configClass);
		context.refresh();
		context.registerShutdownHook();
	}

	/**
	 * Main method. Simple starts a new web site instance.
	 * 
	 * @param args
	 *            ignored
	 * @throws Exception
	 *             if an error occurs
	 */
	public static void main(String[] args) throws Exception
	{
		new WebSite();
	}
}