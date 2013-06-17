package org.randomcoder.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.*;

/**
 * JPA entity representing a comment IP address.
 */
@Entity
@Table(name = "comment_ips")
@SequenceGenerator(name = "comment_ips", sequenceName = "comment_ips_seq", allocationSize = 1)
public class CommentIp implements Serializable
{
	private static final long serialVersionUID = -8330136445379369299L;

	private Long id;
	private String ipAddress;
	private Date creationDate;

	/**
	 * Gets the ID for this IP address.
	 * 
	 * @return id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "comment_ips")
	@Column(name = "comment_ip_id")
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the ID for this IP address.
	 * 
	 * @param id
	 *          id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the remote IP address.
	 * 
	 * @return IP address
	 */
	@Column(name = "ip_address", nullable = false, length = 255)
	public String getIpAddress()
	{
		return ipAddress;
	}

	/**
	 * Sets the remote IP address.
	 * 
	 * @param ipAddress
	 *          IP address
	 */
	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	/**
	 * Gets the creation date of this IP address.
	 * 
	 * @return creation date
	 */
	@Column(name = "create_date", nullable = false)
	public Date getCreationDate()
	{
		return creationDate;
	}

	/**
	 * Sets the creation date of this IP address.
	 * 
	 * @param creationDate
	 *          creation date
	 */
	public void setCreationDate(Date creationDate)
	{
		this.creationDate = creationDate;
	}

	/**
	 * Gets the hash code of this IP address.
	 * 
	 * @return hash code
	 */
	@Override
	public int hashCode()
	{
		return StringUtils.trimToEmpty(getIpAddress()).hashCode();
	}

	/**
	 * Determines if two CommentIp objects are equal.
	 * 
	 * @return true if equal, false if not
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof CommentIp))
			return false;

		CommentIp ip = (CommentIp) obj;

		// equal if and only if ip addresses match
		String addr1 = StringUtils.trimToEmpty(getIpAddress());
		String addr2 = StringUtils.trimToEmpty(ip.getIpAddress());

		return addr1.equals(addr2);
	}

	/**
	 * Gets a string representation of this object, suitable for debugging.
	 * 
	 * @return string representation of this object
	 */
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
