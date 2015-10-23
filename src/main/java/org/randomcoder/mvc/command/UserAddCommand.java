package org.randomcoder.mvc.command;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.*;
import org.randomcoder.db.*;
import org.randomcoder.io.Producer;

/**
 * Command class for adding users.
 */
public class UserAddCommand implements Serializable, Producer<User>
{
	private static final long serialVersionUID = -4063217084413700225L;

	private String userName;
	private String emailAddress;
	private String website;
	private boolean enabled;
	private String password;
	private String password2;

	private Role[] roles;

	/**
	 * Gets the username of this user.
	 * 
	 * @return user name
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets the username of this user.
	 * 
	 * @param userName
	 *          user name
	 */
	public void setUserName(String userName)
	{
		this.userName = StringUtils.trimToNull(userName);
	}

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
	 * Sets the web site for this user.
	 * 
	 * @param website
	 *          web site
	 */
	public void setWebsite(String website)
	{
		this.website = StringUtils.trimToNull(website);
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
	 * Determines if this user is enabled.
	 * 
	 * @return true if enabled, false otherwise
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * Sets whether this user is enabled.
	 * 
	 * @param enabled
	 *          true if enabled, false otherwise
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	/**
	 * Gets the password associated with this user.
	 * 
	 * @return password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the password associated with this user.
	 * 
	 * @param password
	 *          password
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Gets the password for this user again for validation.
	 * 
	 * @return password
	 */
	public String getPassword2()
	{
		return password2;
	}

	/**
	 * Sets the password for this user again for validation.
	 * 
	 * @param password2
	 *          password
	 */
	public void setPassword2(String password2)
	{
		this.password2 = password2;
	}

	/**
	 * Gets the roles associated with this user.
	 * 
	 * @return array of roles
	 */
	public Role[] getRoles()
	{
		return roles;
	}

	/**
	 * Sets the roles associated with this user.
	 * 
	 * @param roles
	 *          array of roles
	 */
	public void setRoles(Role[] roles)
	{
		this.roles = roles;
	}

	/**
	 * Writes out the contents of the current form to the given user.
	 */
	@Override
	public void produce(User user)
	{
		if (user.getId() == null)
		{
			user.setUserName(userName); // only for new users
		}

		user.setEmailAddress(emailAddress);
		user.setWebsite(website);
		user.setEnabled(enabled);

		if (password != null && password.trim().length() > 0)
		{
			user.setPassword(User.hashPassword(password));
		}

		if (user.getRoles() == null)
		{
			user.setRoles(new ArrayList<Role>());
		}

		Set<Role> currentRoles = new HashSet<>(user.getRoles());
		Set<Role> selectedRoles = new HashSet<>();
		if (roles != null)
		{
			selectedRoles.addAll(Arrays.asList(roles));
		}
		
		// get list of deleted roles (current - selected)
		Set<Role> deletedRoles = new HashSet<>(currentRoles);
		deletedRoles.removeAll(selectedRoles);

		// get list of added roles (selected - current)
		Set<Role> addedRoles = new HashSet<>(selectedRoles);
		addedRoles.removeAll(currentRoles);

		// remove deleted roles
		user.getRoles().removeAll(deletedRoles);

		// add new roles
		user.getRoles().addAll(addedRoles);
	}

	/**
	 * Gets a string representation of this object, suitable for debugging.
	 * 
	 * @return string representation of this object
	 */
	@Override
	public String toString()
	{
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
}
