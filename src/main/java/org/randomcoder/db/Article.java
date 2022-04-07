package org.randomcoder.db;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.randomcoder.content.ContentType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * JPA entity representing an article.
 */
@Entity @Table(name = "articles")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SequenceGenerator(name = "articles", sequenceName = "articles_seq", allocationSize = 1)
public class Article implements Serializable {
  private static final long serialVersionUID = 4017235474814138625L;

  private Long id;
  private ContentType contentType;
  private String permalink;
  private User createdByUser;
  private Date creationDate;
  private User modifiedByUser;
  private Date modificationDate;
  private String title;
  private String content;
  private String summary;
  private boolean commentsEnabled = true;

  private List<Tag> tags;
  private List<Comment> comments;

  /**
   * Gets the id of this article.
   *
   * @return article id
   */
  @Id @GeneratedValue(strategy = GenerationType.AUTO, generator = "articles")
  @Column(name = "article_id") public Long getId() {
    return id;
  }

  /**
   * Sets the id of this article
   *
   * @param id article id
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the tags associated with this article.
   *
   * @return List of {@code Tag} objects
   */
  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(name = "article_tag_link", joinColumns = {
      @JoinColumn(name = "article_id") }, inverseJoinColumns = @JoinColumn(name = "tag_id"))
  @OrderBy("displayName") public List<Tag> getTags() {
    return tags;
  }

  /**
   * Sets the tags associated with this article.
   *
   * @param tags List of {@code Tag} objects
   */
  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }

  /**
   * Gets the list of comments for this article.
   *
   * @return list of comments
   */
  @OneToMany(mappedBy = "article", cascade = { CascadeType.PERSIST,
      CascadeType.MERGE, CascadeType.REMOVE }) @OrderBy()
  public List<Comment> getComments() {
    return comments;
  }

  /**
   * Sets the list of comments for this article.
   *
   * @param comments list of comments
   */
  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  /**
   * Gets the content type for this article.
   *
   * @return content type
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "content_type", nullable = false, length = 255)
  public ContentType getContentType() {
    return contentType;
  }

  /**
   * Sets the content type of this article.
   *
   * @param contentType content type
   */
  public void setContentType(ContentType contentType) {
    this.contentType = contentType;
  }

  /**
   * Gets the permalink for this article.
   *
   * @return permalink
   */
  @Column(name = "permalink", nullable = true, unique = true, length = 100)
  public String getPermalink() {
    return permalink;
  }

  /**
   * Sets the permalink for this article.
   *
   * @param permalink permalink
   */
  public void setPermalink(String permalink) {
    this.permalink = permalink;
  }

  /**
   * Gets the User this article was created by.
   *
   * @return user
   */
  @ManyToOne(cascade = {
      CascadeType.PERSIST }, fetch = FetchType.EAGER, optional = true)
  @JoinColumn(name = "create_user_id", nullable = true)
  public User getCreatedByUser() {
    return createdByUser;
  }

  /**
   * Sets the user this article was created by.
   *
   * @param createdByUser user, or null if user no longer exists.
   */
  public void setCreatedByUser(User createdByUser) {
    this.createdByUser = createdByUser;
  }

  /**
   * Gets the creation date of this article.
   *
   * @return creation date
   */
  @Column(name = "create_date", nullable = false)
  public Date getCreationDate() {
    return creationDate;
  }

  /**
   * Sets the creation date of this article.
   *
   * @param creationDate creation date
   */
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * Gets the user who last modified this article.
   *
   * @return user, or null if not modified, or user doesn't exist.
   */
  @ManyToOne(cascade = {
      CascadeType.PERSIST }, fetch = FetchType.EAGER, optional = true)
  @JoinColumn(name = "modify_user_id", nullable = true)
  public User getModifiedByUser() {
    return modifiedByUser;
  }

  /**
   * Sets the user who last modified this article.
   *
   * @param modifiedByUser user
   */
  public void setModifiedByUser(User modifiedByUser) {
    this.modifiedByUser = modifiedByUser;
  }

  /**
   * Gets the modification date of this article.
   *
   * @return modification date, or null if article has not been modified
   */
  @Column(name = "modify_date", nullable = true)
  public Date getModificationDate() {
    return modificationDate;
  }

  /**
   * Sets the modification date of this article.
   *
   * @param modificationDate modification date
   */
  public void setModificationDate(Date modificationDate) {
    this.modificationDate = modificationDate;
  }

  /**
   * Gets the title of this article.
   *
   * @return article title
   */
  @Column(name = "title", nullable = false, length = 255)
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title of this article.
   *
   * @param title article title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Gets the textual content of this article.
   *
   * @return article content
   */
  @Column(name = "content", nullable = false) public String getContent() {
    return content;
  }

  /**
   * Sets the textual content of this article.
   *
   * @param content article content
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * Gets the summary text for this article.
   *
   * @return article summyar
   */
  @Column(name = "summary", nullable = true) public String getSummary() {
    return summary;
  }

  /**
   * Sets the summary text for this article.
   *
   * @param summary summary text
   */
  public void setSummary(String summary) {
    this.summary = summary;
  }

  /**
   * Determines if comments are enabled for this article.
   *
   * @return <code>true</code> if comments are enabled
   */
  @Column(name = "comments_enabled", nullable = false)
  public boolean isCommentsEnabled() {
    return commentsEnabled;
  }

  /**
   * Sets whether comments should be enabled for this article.
   *
   * @param commentsEnabled <code>true</code> if comments should be enabled
   */
  public void setCommentsEnabled(boolean commentsEnabled) {
    this.commentsEnabled = commentsEnabled;
  }

  /**
   * Builds a context-relative permalink for the selected article.
   *
   * @return permalink
   */
  @Transient public String getPermalinkUrl() {
    String perm = getPermalink();
    try {
      if (perm != null) {
        return "/articles/" + URLEncoder.encode(perm, "UTF-8");
      }
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Unsupported encoding", e);
    }

    DecimalFormat df = new DecimalFormat("####################");
    return "/articles/id/" + df.format(id);
  }

  /**
   * Gets a string representation of this object, suitable for debugging.
   *
   * @return string representation of this object
   */
  @Override public String toString() {
    return (new ReflectionToStringBuilder(this,
        ToStringStyle.SHORT_PREFIX_STYLE) {
      @Override protected boolean accept(Field f) {
        String fName = f.getName();
        if (fName.equals("content")) {
          return false;
        }
        return super.accept(f);
      }
    }).toString();
  }
}
