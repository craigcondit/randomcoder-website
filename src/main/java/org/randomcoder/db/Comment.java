package org.randomcoder.db;

import org.randomcoder.article.moderation.ModerationStatus;
import org.randomcoder.content.ContentType;

import java.io.Serializable;
import java.util.Date;

/**
 * Database entity representing an article comment.
 */
public class Comment implements Serializable {

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
    private String referrer;
    private String ipAddress;
    private String userAgent;

    /**
     * Gets the ID for this comment.
     *
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID for this comment.
     *
     * @param id id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the article this comment belongs to.
     *
     * @return article
     */
    public Article getArticle() {
        return article;
    }

    /**
     * Sets the article this comment belongs to.
     *
     * @param article article
     */
    public void setArticle(Article article) {
        this.article = article;
    }

    /**
     * Gets the content type for this comment.
     *
     * @return content type
     */
    public ContentType getContentType() {
        return contentType;
    }

    /**
     * Sets the content type of this comment.
     *
     * @param contentType content type
     */
    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    /**
     * Gets the User this comment was created by.
     *
     * @return user
     */
    public User getCreatedByUser() {
        return createdByUser;
    }

    /**
     * Sets the user this comment was created by.
     *
     * @param createdByUser user, or null if user no longer exists.
     */
    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    /**
     * Gets the creation date of this comment.
     *
     * @return creation date
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date of this comment.
     *
     * @param creationDate creation date
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets the user name to display for anonymous users.
     *
     * @return anonymous user name
     */
    public String getAnonymousUserName() {
        return anonymousUserName;
    }

    /**
     * Sets the user name to display for anonymous users.
     *
     * @param anonymousUserName anonymous user name
     */
    public void setAnonymousUserName(String anonymousUserName) {
        this.anonymousUserName = anonymousUserName;
    }

    /**
     * Gets the email address for anonymous users.
     *
     * @return anonymous email address
     */
    public String getAnonymousEmailAddress() {
        return anonymousEmailAddress;
    }

    /**
     * Sets the email address for anonymous users.
     *
     * @param anonymousEmailAddress anonymous email address
     */
    public void setAnonymousEmailAddress(String anonymousEmailAddress) {
        this.anonymousEmailAddress = anonymousEmailAddress;
    }

    /**
     * Gets the web site for anonymous users.
     *
     * @return anonymous web site
     */
    public String getAnonymousWebsite() {
        return anonymousWebsite;
    }

    /**
     * Sets the web site for anonymous users.
     *
     * @param anonymousWebsite anonymous web site
     */
    public void setAnonymousWebsite(String anonymousWebsite) {
        this.anonymousWebsite = anonymousWebsite;
    }

    /**
     * Gets the title of this comment.
     *
     * @return comment title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this comment.
     *
     * @param title comment title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the textual content of this comment.
     *
     * @return comment content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the textual content of this comment.
     *
     * @param content comment content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Determines if this comment is visible.
     *
     * @return true if visible, false otherwise
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Marks this comment as visible or not.
     *
     * @param visible true if visible, false otherwise
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Gets the moderation status of this comment.
     *
     * @return moderation status
     */
    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    /**
     * Sets the moderation status of this comment.
     *
     * @param moderationStatus moderation status
     */
    public void setModerationStatus(ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    /**
     * Gets the HTTP referrer sent when this comment was posted.
     *
     * @return HTTP referrer
     */
    public String getReferrer() {
        return referrer;
    }

    /**
     * Sets the HTTP referrer sent when this comment was posted.
     *
     * @param referrer HTTP referrer
     */
    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    /**
     * Gets the IP address of the user who posted this comment.
     *
     * @return IP address
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the IP address of the user who posted this comment.
     *
     * @param ipAddress IP address
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Gets the HTTP user agent of the user who posted this comment.
     *
     * @return HTTP user agent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Sets the HTTP user agent of the user who posted this comment.
     *
     * @param userAgent HTTP user agent
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
