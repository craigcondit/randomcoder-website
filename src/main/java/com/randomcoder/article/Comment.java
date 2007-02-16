package com.randomcoder.article;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import com.randomcoder.article.moderation.ModerationStatus;
import com.randomcoder.content.ContentType;
import com.randomcoder.user.User;

/**
 * JavaBean representing an article comment.
 * 
 * <pre>
 * Copyright (c) 2006-2007, Craig Condit. All rights reserved.
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
	@NamedQuery(name = "Comment.ForModeration", query = "from Comment c where c.moderationStatus = 'PENDING' order by c.creationDate")
})
@Entity
@Table(name = "comments")
@SequenceGenerator(name = "comments", sequenceName = "comments_seq", allocationSize = 1)
public class Comment implements Serializable
{
	private static final long serialVersionUID = 7444605318685376170L;
	
	private Long id;
	private Article article;
	private ContentType contentType;
	private User createdByUser;
	private Date creationDate;
	private String anonymousUserName;
	private String anonymousEmailAddress;
	private String anonymousWebsite;
	private String title;
	private String content;
	private boolean visible;
	private ModerationStatus moderationStatus;
	private CommentReferrer referrer;
	private CommentIp ipAddress;
	private CommentUserAgent userAgent;
	
	/**
	 * Gets the ID for this comment.
	 * @return id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "comments")
	@Column(name = "comment_id")
	public Long getId()
	{
		return id;
	}
	
	/**
	 * Sets the ID for this comment.
	 * @param id id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}
	
	/**
	 * Gets the article this comment belongs to.
	 * @return article
	 */
	@ManyToOne(cascade = { CascadeType.PERSIST }, fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name = "article_id")
	public Article getArticle()
	{
		return article;
	}
	
	/**
	 * Sets the article this comment belongs to.
	 * @param article article
	 */
	public void setArticle(Article article)
	{
		this.article = article;
	}
	
	/**
	 * Gets the content type for this comment.
	 * @return content type
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "content_type", nullable = false, length = 255)
	public ContentType getContentType()
	{
		return contentType;
	}

	/**
	 * Sets the content type of this comment.
	 * @param contentType content type
	 */
	public void setContentType(ContentType contentType)
	{
		this.contentType = contentType;
	}
	
	/**
	 * Gets the User this comment was created by.
	 * @return user
	 */
	@ManyToOne(cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "create_user_id", nullable = true)
	public User getCreatedByUser()
	{
		return createdByUser;
	}

	/**
	 * Sets the user this comment was created by.
	 * @param createdByUser user, or null if user no longer exists.
	 */
	public void setCreatedByUser(User createdByUser)
	{
		this.createdByUser = createdByUser;
	}

	/**
	 * Gets the creation date of this comment.
	 * @return creation date
	 */
	@Column(name = "create_date", nullable = false)
	public Date getCreationDate()
	{
		return creationDate;
	}

	/**
	 * Sets the creation date of this comment.
	 * @param creationDate creation date
	 */
	public void setCreationDate(Date creationDate)
	{
		this.creationDate = creationDate;
	}
	
	/**
	 * Gets the user name to display for anonymous users.
	 * @return anonymous user name
	 */
	@Column(name = "anonymous_user_name", nullable = true, length = 30)
	public String getAnonymousUserName()
	{
		return anonymousUserName;
	}
	
	/**
	 * Sets the user name to display for anonymous users.
	 * @param anonymousUserName anonymous user name
	 */
	public void setAnonymousUserName(String anonymousUserName)
	{
		this.anonymousUserName = anonymousUserName;
	}
	
	/**
	 * Gets the email address for anonymous users. 
	 * @return anonymous email address
	 */
	@Column(name = "anonymous_email_address", nullable = true, length = 320)	
	public String getAnonymousEmailAddress()
	{
		return anonymousEmailAddress;
	}
	
	/**
	 * Sets the email address for anonymous users.
	 * @param anonymousEmailAddress anonymous email address
	 */
	public void setAnonymousEmailAddress(String anonymousEmailAddress)
	{
		this.anonymousEmailAddress = anonymousEmailAddress;
	}
	
	/**
	 * Gets the web site for anonymous users.
	 * @return anonymous web site
	 */
	@Column(name = "anonymous_website", nullable = true, length = 255)
	public String getAnonymousWebsite()
	{
		return anonymousWebsite;
	}
	
	/**
	 * Sets the web site for anonymous users.
	 * @param anonymousWebsite anonymous web site
	 */
	public void setAnonymousWebsite(String anonymousWebsite)
	{
		this.anonymousWebsite = anonymousWebsite;
	}
	
	/**
	 * Gets the title of this comment.
	 * @return comment title
	 */
	@Column(name = "title", nullable = false, length = 255)
	public String getTitle()
	{
		return title;
	}

	/**
	 * Sets the title of this comment.
	 * @param title comment title
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * Gets the textual content of this comment.
	 * @return comment content
	 */
	@Column(name = "content", nullable = false)
	public String getContent()
	{
		return content;
	}

	/**
	 * Sets the textual content of this comment.
	 * @param content comment content
	 */
	public void setContent(String content)
	{
		this.content = content;
	}
	
	/**
	 * Determines if this comment is visible.
	 * @return true if visible, false otherwise
	 */
	@Column(name = "visible", nullable = false)
	public boolean isVisible()
	{
		return visible;
	}
	
	/**
	 * Marks this comment as visible or not.
	 * @param visible true if visible, false otherwise
	 */
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	/**
	 * Gets the moderation status of this comment.
	 * @return moderation status
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "moderation_status", nullable = false, length = 255)
	public ModerationStatus getModerationStatus()
	{
		return moderationStatus;
	}
	
	/**
	 * Sets the moderation status of this comment.
	 * @param moderationStatus moderation status
	 */
	public void setModerationStatus(ModerationStatus moderationStatus)
	{
		this.moderationStatus = moderationStatus;
	}
	
	/**
	 * Gets the HTTP referrer sent when this comment was posted.
	 * @return HTTP referrer
	 */
	@ManyToOne(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "comment_referrer_id", nullable = true)
	public CommentReferrer getReferrer()
	{
		return referrer;
	}
	
	/**
	 * Sets the HTTP referrer sent when this comment was posted.
	 * @param referrer HTTP referrer
	 */
	public void setReferrer(CommentReferrer referrer)
	{
		this.referrer = referrer;
	}
	
	/**
	 * Gets the IP address of the user who posted this comment.
	 * @return IP address
	 */
	@ManyToOne(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "comment_ip_id", nullable = true)
	public CommentIp getIpAddress()
	{
		return ipAddress;
	}
	
	/**
	 * Sets the IP address of the user who posted this comment.
	 * @param ipAddress IP address
	 */
	public void setIpAddress(CommentIp ipAddress)
	{
		this.ipAddress = ipAddress;
	}
	
	/**
	 * Gets the HTTP user agent of the user who posted this comment.
	 * @return HTTP user agent
	 */
	@ManyToOne(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "comment_useragent_id", nullable = true)	
	public CommentUserAgent getUserAgent()
	{
		return userAgent;
	}
	
	/**
	 * Sets the HTTP user agent of the user who posted this comment.
	 * @param userAgent HTTP user agent
	 */
	public void setUserAgent(CommentUserAgent userAgent)
	{
		this.userAgent = userAgent;
	}
}
