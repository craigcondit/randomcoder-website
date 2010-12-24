package com.randomcoder.article.comment;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.*;

/**
 * JavaBean representing a comment user agent.
 * 
 * <pre>
 * Copyright (c) 2007, Craig Condit. All rights reserved.
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
	@NamedQuery(name = "CommentUserAgent.ByName", query = "from CommentUserAgent cu where cu.userAgentName = ?")
})
@Entity
@Table(name = "comment_useragents")
@SequenceGenerator(name = "comment_useragents", sequenceName = "comment_useragents_seq", allocationSize = 1)
public class CommentUserAgent implements Serializable
{
	private static final long serialVersionUID = 4101138502746346499L;
	
	private Long id;
	private String userAgentName;
	private Date creationDate;
	
	/**
	 * Gets the ID for this user agent.
	 * @return id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "comment_useragents")
	@Column(name = "comment_useragent_id")
	public Long getId()
	{
		return id;
	}
	
	/**
	 * Sets the ID for this user agent.
	 * @param id id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}
	
	/**
	 * Gets the user agent name.
	 * @return user agent name
	 */
	@Column(name = "user_agent", nullable = false, length = 255)
	public String getUserAgentName()
	{
		return userAgentName;
	}
	
	/**
	 * Sets the user agent name.
	 * @param userAgentName user agent name
	 */
	public void setUserAgentName(String userAgentName)
	{
		this.userAgentName = userAgentName;
	}
	
	/**
	 * Gets the creation date of this user agent.
	 * @return creation date
	 */
	@Column(name = "create_date", nullable = false)
	public Date getCreationDate()
	{
		return creationDate;
	}

	/**
	 * Sets the creation date of this user agent.
	 * @param creationDate creation date
	 */
	public void setCreationDate(Date creationDate)
	{
		this.creationDate = creationDate;
	}	
	
	/**
	 * Gets the hash code of this user agent.
	 * @return hash code
	 */
	@Override
	public int hashCode()
	{
		return StringUtils.trimToEmpty(getUserAgentName()).hashCode();
	}	
	
	/**
	 * Determines if two CommentUserAgent objects are equal.
	 * @return true if equal, false if not
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof CommentUserAgent)) return false;
		
		CommentUserAgent ua = (CommentUserAgent) obj;
			
		// equal if and only if user agent names match
		String name1 = StringUtils.trimToEmpty(getUserAgentName());
		String name2 = StringUtils.trimToEmpty(ua.getUserAgentName());
		
		return name1.equals(name2);
	}
	
	/**
	 * Gets a string representation of this object, suitable for debugging.
	 * @return string representation of this object
	 */
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
