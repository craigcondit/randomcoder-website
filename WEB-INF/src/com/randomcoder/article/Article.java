package com.randomcoder.article;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

import javax.persistence.*;

import org.apache.commons.lang.builder.*;

import com.randomcoder.content.ContentType;
import com.randomcoder.tag.Tag;
import com.randomcoder.user.User;

/**
 * JavaBean representing an article.
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
	@NamedQuery(name = "Article.All", query = "from Article a order by a.creationDate desc"),
	@NamedQuery(name = "Article.ByTag", query = "from Article a where ? in elements(a.tags) order by a.creationDate desc"),
	@NamedQuery(name = "Article.BeforeDate", query = "from Article a where a.creationDate < ? order by a.creationDate desc"),
	@NamedQuery(name = "Article.ByTagBeforeDate", query = "from Article a where ? in elements(a.tags) and a.creationDate < ? order by a.creationDate desc"),
	@NamedQuery(name = "Article.CountBeforeDate", query = "select count(a.id) from Article a where a.creationDate < ?"),
	@NamedQuery(name = "Article.CountByTagBeforeDate", query = "select count(a.id) from Article a where ? in elements(a.tags) and a.creationDate < ?"),
	@NamedQuery(name = "Article.BetweenDates", query = "from Article a where a.creationDate >= ? and a.creationDate < ? order by a.creationDate desc"),
	@NamedQuery(name = "Article.ByTagBetweenDates", query = "from Article a where ? in elements(a.tags) and a.creationDate >= ? and a.creationDate < ? order by a.creationDate desc"),
	@NamedQuery(name = "Article.ByPermalink", query = "from Article a where a.permalink = ?")
})
@Entity
@Table(name = "articles")
@SequenceGenerator(name = "articles", sequenceName = "articles_seq", allocationSize = 1)
public class Article implements Serializable
{
	private static final long serialVersionUID = -2673436829347272277L;

	private Long id;
	private ContentType contentType;
	private String permalink;
	private User createdByUser;
	private Date creationDate;
	private User modifiedByUser;
	private Date modificationDate;
	private String title;
	private String content;

	private List<Tag> tags;
	private List<Comment> comments;

	/**
	 * Gets the id of this article.
	 * @return article id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "articles")
	@Column(name = "article_id")
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the id of this article
	 * @param id article id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the tags associated with this article.
	 * @return List of {@code Tag} objects
	 */
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "article_tag_link", joinColumns = { @JoinColumn(name = "article_id") }, inverseJoinColumns = @JoinColumn(name = "tag_id"))
	@OrderBy("displayName")
	public List<Tag> getTags()
	{
		return tags;
	}

	/**
	 * Sets the tags associated with this article.
	 * @param tags List of {@code Tag} objects
	 */
	public void setTags(List<Tag> tags)
	{
		this.tags = tags;
	}

	/**
	 * Gets the list of comments for this article.
	 * @return list of comments
	 */
	@OneToMany(mappedBy="article", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
  @OrderBy()	
	public List<Comment> getComments()
	{
		return comments;
	}
	
	/**
	 * Sets the list of comments for this article.
	 * @param comments list of comments
	 */
	public void setComments(List<Comment> comments)
	{
		this.comments = comments;
	}
	
	/**
	 * Gets the content type for this article.
	 * @return content type
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "content_type", nullable = false, length = 255)
	public ContentType getContentType()
	{
		return contentType;
	}

	/**
	 * Sets the content type of this article.
	 * @param contentType content type
	 */
	public void setContentType(ContentType contentType)
	{
		this.contentType = contentType;
	}

	/**
	 * Gets the permalink for this article.
	 * @return permalink
	 */
	@Column(name = "permalink", nullable = true, unique = true, length = 100)
	public String getPermalink()
	{
		return permalink;
	}

	/**
	 * Sets the permalink for this article.
	 * @param permalink permalink
	 */
	public void setPermalink(String permalink)
	{
		this.permalink = permalink;
	}

	/**
	 * Gets the User this article was created by.
	 * @return user
	 */
	@ManyToOne(cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "create_user_id", nullable = true)
	public User getCreatedByUser()
	{
		return createdByUser;
	}

	/**
	 * Sets the user this article was created by.
	 * @param createdByUser user, or null if user no longer exists.
	 */
	public void setCreatedByUser(User createdByUser)
	{
		this.createdByUser = createdByUser;
	}

	/**
	 * Gets the creation date of this article.
	 * @return creation date
	 */
	@Column(name = "create_date", nullable = false)
	public Date getCreationDate()
	{
		return creationDate;
	}

	/**
	 * Sets the creation date of this article.
	 * @param creationDate creation date
	 */
	public void setCreationDate(Date creationDate)
	{
		this.creationDate = creationDate;
	}

	/**
	 * Gets the user who last modified this article.
	 * @return user, or null if not modified, or user doesn't exist.
	 */
	@ManyToOne(cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "modify_user_id", nullable = true)
	public User getModifiedByUser()
	{
		return modifiedByUser;
	}

	/**
	 * Sets the user who last modified this article.
	 * @param modifiedByUser user
	 */
	public void setModifiedByUser(User modifiedByUser)
	{
		this.modifiedByUser = modifiedByUser;
	}

	/**
	 * Gets the modification date of this article.
	 * @return modification date, or null if article has not been modified
	 */
	@Column(name = "modify_date", nullable = true)
	public Date getModificationDate()
	{
		return modificationDate;
	}

	/**
	 * Sets the modification date of this article.
	 * @param modificationDate modification date
	 */
	public void setModificationDate(Date modificationDate)
	{
		this.modificationDate = modificationDate;
	}

	/**
	 * Gets the title of this article.
	 * @return article title
	 */
	@Column(name = "title", nullable = false, length = 255)
	public String getTitle()
	{
		return title;
	}

	/**
	 * Sets the title of this article.
	 * @param title article title
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * Gets the textual content of this article.
	 * @return article content
	 */
	@Column(name = "content", nullable = false)
	public String getContent()
	{
		return content;
	}

	/**
	 * Sets the textual content of this article.
	 * @param content article content
	 */
	public void setContent(String content)
	{
		this.content = content;
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
				String fName = f.getName();
				if (fName.equals("content"))
					return false;
				return super.accept(f);
			}
		}).toString();
	}
}
