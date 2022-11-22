package org.randomcoder;

import org.randomcoder.config.JettyContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.File;

/**
 * Startup class for the randomcoder website.
 */
public class WebSite {
  /**
   * Configuration file.
   */
  public static final String CONFIG_FILE = "randomcoder-website.conf";

  private final AnnotationConfigApplicationContext context;

  /**
   * Creates an instance of the website using default parameters.
   *
   * @throws Exception if an error occurs
   */
  public WebSite() throws Exception {
    this(JettyContext.class);
  }

  /**
   * Create the website with a custom log4j config file prefix and
   * configuration class.
   *
   * @param configClass configuration class
   * @throws Exception if an error occurs
   */
  public WebSite(Class<?> configClass) throws Exception {
    context = new AnnotationConfigApplicationContext();

    ConfigurableEnvironment env = new StandardEnvironment();
    MutablePropertySources propertySources = env.getPropertySources();

    // make sure at least the dev profile is active
    if (env.getActiveProfiles().length == 0) {
      env.addActiveProfile("dev");
    }

    // add profile-specific config files
    for (String profile : env.getActiveProfiles()) {
      File profileConfigFile = new File( CONFIG_FILE + "." + profile);
      if (profileConfigFile.exists()) {
        propertySources.addLast(new ResourcePropertySource(
            new FileSystemResource(profileConfigFile)));
      }

      ClassPathResource res =
          new ClassPathResource("/profiles/" + profile + ".properties");
      if (res.exists()) {
        propertySources.addLast(new ResourcePropertySource(res));
      }
    }

    // add default config file (if it exists)
    File configFile = new File(CONFIG_FILE);
    if (configFile.exists()) {
      propertySources.addLast(
          new ResourcePropertySource(new FileSystemResource(configFile)));
    }

    // add default built-in properties (if they exist)
    propertySources.addLast(new ResourcePropertySource(
        new ClassPathResource("/profiles/default.properties")));

    context.setEnvironment(env);
    context.register(configClass);
  }

  public void run() {
    context.refresh();
    context.registerShutdownHook();
  }

  /**
   * Main method. Simple starts a new website instance.
   *
   * @param args ignored
   * @throws Exception if an error occurs
   */
  public static void main(String[] args) throws Exception {
    WebSite site = new WebSite();
    site.run();
  }
}
