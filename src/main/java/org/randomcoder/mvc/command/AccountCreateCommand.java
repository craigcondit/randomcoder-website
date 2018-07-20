package org.randomcoder.mvc.command;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.randomcoder.db.Role;
import org.randomcoder.db.User;
import org.randomcoder.io.Producer;

/**
 * Command class for adding users.
 */
public class AccountCreateCommand implements Serializable, Producer<User> {
	private static final long serialVersionUID = 7346261261522108772L;

	private String userName;
	private String emailAddress;
	private String website;
	private String password;
	private String password2;

	/**
	 * Gets the username of this user.
	 * 
	 * @return user name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Sets the username of this user.
	 * 
	 * @param userName
	 *            user name
	 */
	public void setUserName(String userName) {
		this.userName = StringUtils.trimToNull(userName);
	}

	/**
	 * Gets the email address of this user.
	 * 
	 * @return email address
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Sets the email address of this user.
	 * 
	 * @param emailAddress
	 *            email address
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = StringUtils.trimToNull(emailAddress);
	}

	/**
	 * Sets the web site for this user.
	 * 
	 * @param website
	 *            web site
	 */
	public void setWebsite(String website) {
		this.website = StringUtils.trimToNull(website);
	}

	/**
	 * Gets the website for this user.
	 * 
	 * @return web site
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * Gets the password associated with this user.
	 * 
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password associated with this user.
	 * 
	 * @param password
	 *            password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the password for this user again for validation.
	 * 
	 * @return password
	 */
	public String getPassword2() {
		return password2;
	}

	/**
	 * Sets the password for this user again for validation.
	 * 
	 * @param password2
	 *            password
	 */
	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	/**
	 * Writes out the contents of the current form to the given user.
	 */
	@Override
	public void produce(User user) {
		user.setUserName(userName);
		user.setEmailAddress(emailAddress);
		user.setWebsite(website);
		user.setEnabled(true);
		user.setPassword(User.hashPassword(password));
		user.setRoles(new ArrayList<Role>());
	}

	/**
	 * Gets a string representation of this object, suitable for debugging.
	 * 
	 * @return string representation of this object
	 */
	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
}
