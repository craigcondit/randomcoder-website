package org.randomcoder.article.comment;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.*;

/**
 * JavaBean representing a comment referrer.
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
	@NamedQuery(name = "CommentReferrer.ByUri", query = "from CommentReferrer cr where cr.referrerUri = ?")
})
@Entity
@Table(name = "comment_referrers")
@SequenceGenerator(name = "comment_referrers", sequenceName = "comment_referrers_seq", allocationSize = 1)
public class CommentReferrer implements Serializable
{
	private static final long serialVersionUID = 4101138502746346499L;
	
	private Long id;
	private String referrerUri;
	private Date creationDate;
	
	/**
	 * Gets the ID for this referrer.
	 * @return id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "comment_referrers")
	@Column(name = "comment_referrer_id")
	public Long getId()
	{
		return id;
	}
	
	/**
	 * Sets the ID for this referrer.
	 * @param id id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}
	
	/**
	 * Gets the URI sent as the HTTP referrer.
	 * @return referrer URI
	 */
	@Column(name = "referrer", nullable = false, length = 1024)
	public String getReferrerUri()
	{
		return referrerUri;
	}
	
	/**
	 * Sets the URI sent as the HTTP referrer. 
	 * @param referrerUri referrer URI
	 */
	public void setReferrerUri(String referrerUri)
	{
		this.referrerUri = referrerUri;
	}
	
	/**
	 * Gets the creation date of this referrer.
	 * @return creation date
	 */
	@Column(name = "create_date", nullable = false)
	public Date getCreationDate()
	{
		return creationDate;
	}

	/**
	 * Sets the creation date of this referrer.
	 * @param creationDate creation date
	 */
	public void setCreationDate(Date creationDate)
	{
		this.creationDate = creationDate;
	}	
	
	/**
	 * Gets the hash code of this referrer.
	 * @return hash code
	 */
	@Override
	public int hashCode()
	{
		return StringUtils.trimToEmpty(getReferrerUri()).hashCode();
	}
	
	/**
	 * Determines if two CommentReferrer objects are equal.
	 * @return true if equal, false if not
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof CommentReferrer)) return false;
		
		CommentReferrer ref = (CommentReferrer) obj;
			
		// two referrers are equal if and only if their uris match
		String uri1 = StringUtils.trimToEmpty(getReferrerUri());
		String uri2 = StringUtils.trimToEmpty(ref.getReferrerUri());
		
		return uri1.equals(uri2);
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
