package com.randomcoder.user;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

import javax.persistence.*;

import org.apache.commons.lang.builder.*;

/**
 * JavaBean representing a CardSpace token.
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
	@NamedQuery(name = "CardSpaceToken.ByPrivatePersonalIdentifier", query = "from CardSpaceToken t where t.privatePersonalIdentifier = ? and t.issuerHash = ?"),
	@NamedQuery(name = "CardSpaceToken.ByUser", query = "from CardSpaceToken t where t.user = ? order by t.lastLoginDate desc")
})
@Entity
@Table(name="cardspace_tokens")
@SequenceGenerator(name = "cardspace_tokens", sequenceName = "cardspace_tokens_seq", allocationSize = 1)
public class CardSpaceToken implements Serializable
{
	private static final long serialVersionUID = 7138869768477410560L;
	
	private Long id;
	private String privatePersonalIdentifier;
	private String issuerHash;
	private String emailAddress;
	private User user;
	private Date creationDate;
	private Date lastLoginDate;
	
	/**
	 * Gets the id of this token.
	 * @return token id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cardspace_tokens")
	@Column(name = "cardspace_token_id")
	public Long getId()
	{
		return id;
	}
	
	/**
	 * Sets the id of this token.
	 * @param id token id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}
	
	/**
	 * Gets the user this token belongs to.
	 * @return user
	 */
	@ManyToOne(cascade = { CascadeType.PERSIST }, fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name = "user_id")
	public User getUser()
	{
		return user;
	}
	
	/**
	 * Sets the user this token belongs to.
	 * @param user user
	 */
	public void setUser(User user)
	{
		this.user = user;
	}
	
	/**
	 * Gets the private personal identifier of this token.
	 * @return ppid
	 */
	@Column(name = "ppid", unique = true, nullable = false, length = 1024)
	public String getPrivatePersonalIdentifier()
	{
		return privatePersonalIdentifier;
	}
	
	/**
	 * Sets the private personal identifier of this token.
	 * @param privatePersonalIdentifier ppid
	 */
	public void setPrivatePersonalIdentifier(String privatePersonalIdentifier)
	{
		this.privatePersonalIdentifier = privatePersonalIdentifier;
	}
	
	/**
	 * Gets the SHA-1 hash of the Issuer's public key.
	 * @return issuer hash
	 */
	@Column(name="issuer_hash", unique = false, nullable = false, length = 40)
	public String getIssuerHash()
	{
		return issuerHash;
	}
	
	/**
	 * Sets the SHA-1 hash of the Issuer's public key
	 * @param issuerHash issuer hash
	 */
	public void setIssuerHash(String issuerHash)
	{
		this.issuerHash = issuerHash;
	}
	
	/**
	 * Gets the creation date of this token
	 * @return creation date
	 */
	@Column(name="create_date", unique = false, nullable = false)
	public Date getCreationDate()
	{
		return creationDate;
	}
	
	/**
	 * Sets the creation date of this token
	 * @param creationDate creation date
	 */
	public void setCreationDate(Date creationDate)
	{
		this.creationDate = creationDate;
	}
	
	/**
	 * Gets the last time this token was used for login.
	 * @return last login date
	 */
	@Column(name="login_date", unique = false, nullable = false)
	public Date getLastLoginDate()
	{
		return lastLoginDate;
	}
	
	/**
	 * Sets the last login date for this token.
	 * @param lastLoginDate last login date
	 */
	public void setLastLoginDate(Date lastLoginDate)
	{
		this.lastLoginDate = lastLoginDate;
	}
	
	/**
	 * Gets the email address for this token.
	 * @return email address
	 */
	@Column(name="email_address", unique = false, nullable = false, length = 320)
	public String getEmailAddress()
	{
		return emailAddress;
	}
	
	/**
	 * Sets the email address for this token.
	 * @param emailAddress email address
	 */
	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
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
				return super.accept(f); // && !f.getName().equals("password");
			}
		}).toString();
	}	
}
