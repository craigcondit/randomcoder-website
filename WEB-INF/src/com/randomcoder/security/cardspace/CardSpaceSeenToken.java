package com.randomcoder.security.cardspace;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

import javax.persistence.*;

import org.apache.commons.lang.builder.*;

/**
 * JavaBean representing a seen CardSpace token.
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
	@NamedQuery(name = "CardSpaceSeenToken.ByKey", query = "from CardSpaceSeenToken t where t.assertionId = ? and t.privatePersonalIdentifier = ? and t.issuerHash = ?"),
	@NamedQuery(name = "CardSpaceSeenToken.DeleteBefore", query = "delete from CardSpaceSeenToken t where t.creationDate < ?")
})
@Entity
@Table(name="cardspace_seen_tokens")
@SequenceGenerator(name = "cardspace_seen_tokens", sequenceName = "cardspace_seen_tokens_seq", allocationSize = 1)
public class CardSpaceSeenToken implements Serializable
{
	private static final long serialVersionUID = -3063450944650542366L;
	
	private Long id;
	private String assertionId;
	private String privatePersonalIdentifier;
	private String issuerHash;
	private Date creationDate;
	
	/**
	 * Gets the id of this token.
	 * @return token id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cardspace_seen_tokens")
	@Column(name = "cardspace_seen_token_id")
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
	 * Gets the assertion id of this token.
	 * @return assertion id
	 */
	@Column(name = "assertion_id", unique = false, nullable = false, length = 1024)
	public String getAssertionId()
	{
		return assertionId;
	}
	
	/**
	 * Sets the assertion id of this token.
	 * @param assertionId assertion id
	 */
	public void setAssertionId(String assertionId)
	{
		this.assertionId = assertionId;
	}
	
	/**
	 * Gets the private personal identifier of this token.
	 * @return ppid
	 */
	@Column(name = "ppid", unique = false, nullable = false, length = 1024)
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
