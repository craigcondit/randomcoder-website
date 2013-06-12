package org.randomcoder.log;

import org.eclipse.jetty.util.log.Logger;

/**
 * Adapter class to pursuade Jetty to use log4j for logging.
 */
public class JettyLog4jLog implements Logger
{
	private final org.apache.log4j.Logger logger;

	/**
	 * Default constructor.
	 */
	public JettyLog4jLog()
	{
		this("org.eclipse.jetty.util.log");
	}

	/**
	 * Constructor which creates a named logger.
	 * 
	 * @param name
	 *          logger name
	 */
	public JettyLog4jLog(String name)
	{
		logger = org.apache.log4j.Logger.getLogger(name);
	}

	@Override
	public String getName()
	{
		return logger.getName();
	}

	@Override
	public void warn(String msg, Object... args)
	{
		logger.warn(String.format(msg, args));
	}

	@Override
	public void warn(Throwable thrown)
	{
		logger.warn(thrown.getMessage(), thrown);
	}

	@Override
	public void warn(String msg, Throwable thrown)
	{
		logger.warn(msg, thrown);
	}

	@Override
	public void info(String msg, Object... args)
	{
		logger.info(String.format(msg, args));
	}

	@Override
	public void info(Throwable thrown)
	{
		logger.info(thrown.getMessage(), thrown);
	}

	@Override
	public void info(String msg, Throwable thrown)
	{
		logger.info(msg, thrown);
	}

	@Override
	public boolean isDebugEnabled()
	{
		return logger.isDebugEnabled();
	}

	@Override
	public void setDebugEnabled(boolean enabled)
	{}

	@Override
	public void debug(String msg, Object... args)
	{
		logger.debug(String.format(msg, args));
	}

	@Override
	public void debug(Throwable thrown)
	{
		logger.debug(thrown.getMessage(), thrown);
	}

	@Override
	public void debug(String msg, Throwable thrown)
	{
		logger.debug(msg, thrown);
	}

	@Override
	public Logger getLogger(String name)
	{
		return new JettyLog4jLog(name);
	}

	@Override
	public void ignore(Throwable ignored)
	{}
}