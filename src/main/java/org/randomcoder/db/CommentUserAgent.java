package org.randomcoder.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * JPA entity representing a comment user agent.
 */
@Entity
@Table(name = "comment_useragents")
@SequenceGenerator(name = "comment_useragents", sequenceName = "comment_useragents_seq", allocationSize = 1)
public class CommentUserAgent implements Serializable {
	private static final long serialVersionUID = 4101138502746346499L;

	private Long id;
	private String userAgentName;
	private Date creationDate;

	/**
	 * Gets the ID for this user agent.
	 * 
	 * @return id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "comment_useragents")
	@Column(name = "comment_useragent_id")
	public Long getId() {
		return id;
	}

	/**
	 * Sets the ID for this user agent.
	 * 
	 * @param id
	 *            id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the user agent name.
	 * 
	 * @return user agent name
	 */
	@Column(name = "user_agent", nullable = false, length = 255)
	public String getUserAgentName() {
		return userAgentName;
	}

	/**
	 * Sets the user agent name.
	 * 
	 * @param userAgentName
	 *            user agent name
	 */
	public void setUserAgentName(String userAgentName) {
		this.userAgentName = userAgentName;
	}

	/**
	 * Gets the creation date of this user agent.
	 * 
	 * @return creation date
	 */
	@Column(name = "create_date", nullable = false)
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Sets the creation date of this user agent.
	 * 
	 * @param creationDate
	 *            creation date
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Gets the hash code of this user agent.
	 * 
	 * @return hash code
	 */
	@Override
	public int hashCode() {
		return StringUtils.trimToEmpty(getUserAgentName()).hashCode();
	}

	/**
	 * Determines if two CommentUserAgent objects are equal.
	 * 
	 * @return true if equal, false if not
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CommentUserAgent))
			return false;

		CommentUserAgent ua = (CommentUserAgent) obj;

		// equal if and only if user agent names match
		String name1 = StringUtils.trimToEmpty(getUserAgentName());
		String name2 = StringUtils.trimToEmpty(ua.getUserAgentName());

		return name1.equals(name2);
	}

	/**
	 * Gets a string representation of this object, suitable for debugging.
	 * 
	 * @return string representation of this object
	 */
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
