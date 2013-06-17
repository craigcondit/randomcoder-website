package org.randomcoder.db;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.*;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

/**
 * JPA entity representing an article tag or category.
 */
@Entity
@Table(name = "tags")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SequenceGenerator(name = "tags", sequenceName = "tags_seq", allocationSize = 1)
public class Tag implements Serializable, Comparable<Tag>
{
	private static final long serialVersionUID = 3814891608527889241L;

	/**
	 * Tag Comparator (by name).
	 */
	public static final Comparator<Tag> NAME_COMPARATOR = new Comparator<Tag>()
	{
		@Override
		public int compare(Tag t1, Tag t2)
		{
			String s1 = StringUtils.trimToEmpty(t1.getName());
			String s2 = StringUtils.trimToEmpty(t2.getName());
			return s1.compareTo(s2);
		}
	};

	/**
	 * Tag Comparator (by display name).
	 */
	public static final Comparator<Tag> DISPLAY_NAME_COMPARATOR = new Comparator<Tag>()
	{
		@Override
		public int compare(Tag t1, Tag t2)
		{
			String s1 = StringUtils.trimToEmpty(t1.getDisplayName());
			String s2 = StringUtils.trimToEmpty(t2.getDisplayName());
			return s1.compareTo(s2);
		}
	};

	private Long id;
	private String name;
	private String displayName;

	private transient List<Article> articles;

	/**
	 * Gets the id for this tag.
	 * 
	 * @return tag id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "tags")
	@Column(name = "tag_id")
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the id for this tag.
	 * 
	 * @param id
	 *          tag id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the name of this tag.
	 * 
	 * @return tag name
	 */
	@Column(name = "name", unique = true, nullable = false, length = 255)
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this tag.
	 * 
	 * @param name
	 *          tag name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the display name of this tag.
	 * 
	 * @return display name
	 */
	@Column(name = "display_name", nullable = false, length = 255)
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * Sets the display name of this tag.
	 * 
	 * @param displayName
	 *          display name
	 */
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	/**
	 * Gets the articles which belong to this tag.
	 * 
	 * @return article list
	 */
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "tags")
	@OrderBy("creationDate DESC")
	public List<Article> getArticles()
	{
		return articles;
	}

	/**
	 * Sets the articles which belong to this tag.
	 * 
	 * @param articles
	 *          article list
	 */
	public void setArticles(List<Article> articles)
	{
		this.articles = articles;
	}

	/**
	 * Determines if two Tag objects are equal.
	 * 
	 * @return true if equal, false if not
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Tag))
			return false;

		Tag tag = (Tag) obj;

		// two tags are equal if and only if their names match
		String name1 = StringUtils.trimToEmpty(getName());
		String name2 = StringUtils.trimToEmpty(tag.getName());

		return name1.equals(name2);
	}

	/**
	 * Gets the hash code of this tag.
	 * 
	 * @return hash code
	 */
	@Override
	public int hashCode()
	{
		return StringUtils.trimToEmpty(getName()).hashCode();
	}

	/**
	 * Compares this Tag to another Tag by name.
	 * 
	 * @return 0 if equal, -1 if this is before, 1 if this is after
	 */
	@Override
	public int compareTo(Tag o)
	{
		return NAME_COMPARATOR.compare(this, o);
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
