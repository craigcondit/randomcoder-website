package org.randomcoder.mvc.command;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.randomcoder.db.User;
import org.randomcoder.io.Producer;

/**
 * Command class for updating a user profile.
 */
public class UserProfileCommand implements Serializable, Producer<User>
{
	private static final long serialVersionUID = 8464807327958297647L;

	private String emailAddress;
	private String website;

	/**
	 * Gets the email address of this user.
	 * 
	 * @return email address
	 */
	public String getEmailAddress()
	{
		return emailAddress;
	}

	/**
	 * Sets the email address of this user.
	 * 
	 * @param emailAddress
	 *          email address
	 */
	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = StringUtils.trimToNull(emailAddress);
	}

	/**
	 * Gets the website for this user.
	 * 
	 * @return web site
	 */
	public String getWebsite()
	{
		return website;
	}

	/**
	 * Sets the website for this user.
	 * 
	 * @param website
	 *          seb site
	 */
	public void setWebsite(String website)
	{
		this.website = StringUtils.trimToNull(website);
	}

	@Override
	public void produce(User target)
	{
		target.setWebsite(website);
		target.setEmailAddress(emailAddress);
	}
}
