package org.randomcoder.website.data;

import java.util.Date;

public class Comment {

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getAnonymousUserName() {
        return anonymousUserName;
    }

    public void setAnonymousUserName(String anonymousUserName) {
        this.anonymousUserName = anonymousUserName;
    }

    public String getAnonymousEmailAddress() {
        return anonymousEmailAddress;
    }

    public void setAnonymousEmailAddress(String anonymousEmailAddress) {
        this.anonymousEmailAddress = anonymousEmailAddress;
    }

    public String getAnonymousWebsite() {
        return anonymousWebsite;
    }

    public void setAnonymousWebsite(String anonymousWebsite) {
        this.anonymousWebsite = anonymousWebsite;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

}
