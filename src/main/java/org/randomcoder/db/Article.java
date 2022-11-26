package org.randomcoder.db;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.randomcoder.content.ContentType;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Database entity representing an article.
 */
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

    private List<Tag> tags = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();

    /**
     * Gets the id of this article.
     *
     * @return article id
     */
    public Long getId() {
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
    public List<Tag> getTags() {
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
    public String getContent() {
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
    public String getSummary() {
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
    public String getPermalinkUrl() {
        String perm = getPermalink();
        if (perm != null) {
            return "/articles/" + URLEncoder.encode(perm, StandardCharsets.UTF_8);
        }

        DecimalFormat df = new DecimalFormat("####################");
        return "/articles/id/" + df.format(id);
    }

    /**
     * Gets a string representation of this object, suitable for debugging.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return (new ReflectionToStringBuilder(this,
                ToStringStyle.SHORT_PREFIX_STYLE) {
            @Override
            protected boolean accept(Field f) {
                String fName = f.getName();
                if (fName.equals("content")) {
                    return false;
                }
                return super.accept(f);
            }
        }).toString();
    }
}
