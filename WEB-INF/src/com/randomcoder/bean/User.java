package com.randomcoder.bean;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

import javax.persistence.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.builder.*;

/**
 * JavaBean representing a user.
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
@NamedQueries
({
	@NamedQuery(name = "User.All", query = "from User u order by u.userName"),
	@NamedQuery(name = "User.CountAll", query = "select count(u.id) from User u"),
	@NamedQuery(name = "User.Enabled", query = "from User u where u.enabled = true order by u.userName"),
	@NamedQuery(name = "User.ByUserName", query = "from User u where u.userName = ?"),
	@NamedQuery(name = "User.ByUserNameEnabled", query = "from User u where u.userName = ? and u.enabled = true")
})
@Entity
@Table(name = "users")
@SequenceGenerator(name = "users", sequenceName = "users_seq", allocationSize = 1)
public class User implements Serializable
{
	private static final long serialVersionUID = 2227663675676869070L;

	private Long id;
	private String userName;
	private String password;
	private String emailAddress;
	private boolean enabled;

	private List<Role> roles;

	/**
	 * Gets the id of this user.
	 * @return user id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users")
	@Column(name = "user_id")
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the id of this user.
	 * @param id user id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the roles which this user belongs to.
	 * @return Set of roles
	 */
	@OneToMany
	@JoinTable(name = "user_role_link", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = @JoinColumn(name = "role_id"))
	@OrderBy("description")
	public List<Role> getRoles()
	{
		return roles;
	}

	/**
	 * Sets the roles which this user belongs to.
	 * @param roles Set of roles
	 */
	public void setRoles(List<Role> roles)
	{
		this.roles = roles;
	}

	/**
	 * Gets the user name of this user.
	 * @return user name
	 */
	@Column(name = "username", unique = true, nullable = false, length = 30)
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets the user name of this user.
	 * @param userName user name
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Gets the password hash of this user.
	 * @return password hash
	 */
	@Column(name = "password", nullable = false, length = 255)
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the password hash of this user.
	 * @param password password hash
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Gets the email address of this user.
	 * @return email address
	 */
	@Column(name = "email", nullable = false, length = 320)
	public String getEmailAddress()
	{
		return emailAddress;
	}

	/**
	 * Sets the email address of this user.
	 * @param emailAddress email address
	 */
	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	/**
	 * Determines if this user is enabled.
	 * @return true if enabled, false otherwise
	 */
	@Column(name = "enabled", nullable = false)
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * Sets whether a user is enabled.
	 * @param enabled true if enabled, false otherwise
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	/**
	 * Gets a string representation of this object, suitable for debugging.
	 * @return string representation of this object
	 */
	@Override
	public String toString()
	{
		return (new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
		{
			@Override
			protected boolean accept(Field f)
			{
				return super.accept(f) && !f.getName().equals("password");
			}
		}).toString();
	}
	
	/**
	 * Hashes a password.
	 * @param password password to hash
	 * @return hashed password
	 */
	public static String hashPassword(String password)
	{
		return DigestUtils.shaHex(password).toLowerCase(Locale.US);
	}
	
}
