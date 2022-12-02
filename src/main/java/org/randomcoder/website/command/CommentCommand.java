package org.randomcoder.website.command;

import jakarta.ws.rs.FormParam;
import org.apache.commons.lang3.StringUtils;
import org.randomcoder.website.data.Comment;
import org.randomcoder.website.data.ContentType;

import java.io.Serializable;
import java.util.function.Consumer;

public class CommentCommand implements Serializable, Consumer<Comment> {
    private static final long serialVersionUID = -1245687879900306444L;

    private boolean anonymous;

    private String anonymousUserName;

    private String anonymousEmailAddress;

    private String anonymousWebsite;

    private String title;

    private String content;

    public boolean isAnonymous() {
        return anonymous;
    }

    public void bind(boolean isAnonymous) {
        this.anonymous = isAnonymous;
    }

    public String getAnonymousUserName() {
        return anonymousUserName;
    }

    @FormParam("anonymousUserName")
    public void setAnonymousUserName(String anonymousUserName) {
        this.anonymousUserName = StringUtils.trimToNull(anonymousUserName);
    }

    public String getAnonymousEmailAddress() {
        return anonymousEmailAddress;
    }

    @FormParam("anonymousEmailAddress")
    public void setAnonymousEmailAddress(String anonymousEmailAddress) {
        this.anonymousEmailAddress = StringUtils.trimToNull(anonymousEmailAddress);
    }

    public String getAnonymousWebsite() {
        return anonymousWebsite;
    }

    @FormParam("anonymousWebsite")
    public void setAnonymousWebsite(String anonymousWebsite) {
        this.anonymousWebsite = StringUtils.trimToNull(anonymousWebsite);
    }

    public String getTitle() {
        return title;
    }

    @FormParam("title")
    public void setTitle(String title) {
        this.title = StringUtils.trimToNull(title);
    }

    public String getContent() {
        return content;
    }

    @FormParam("content")
    public void setContent(String content) {
        this.content = StringUtils.trimToNull(content);
    }

    @Override
    public void accept(Comment comment) {
        if (anonymous) {
            comment.setAnonymousUserName(anonymousUserName);
            comment.setAnonymousEmailAddress(anonymousEmailAddress);
            comment.setAnonymousWebsite(anonymousWebsite);
        } else {
            comment.setAnonymousUserName(null);
            comment.setAnonymousEmailAddress(null);
            comment.setAnonymousWebsite(null);
        }

        comment.setTitle(title);
        comment.setContent(content);

        // TODO allow other types
        comment.setContentType(ContentType.TEXT);
    }

}
