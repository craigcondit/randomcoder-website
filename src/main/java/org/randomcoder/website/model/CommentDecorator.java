package org.randomcoder.website.model;

import org.apache.commons.codec.digest.DigestUtils;
import org.randomcoder.website.contentfilter.ContentFilter;
import org.randomcoder.website.contentfilter.ContentUtils;
import org.randomcoder.website.data.Comment;
import org.randomcoder.website.data.User;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Locale;

public class CommentDecorator {

    private final Comment comment;
    private final ContentFilter filter;

    public CommentDecorator(Comment comment, ContentFilter filter) {
        this.comment = comment;
        this.filter = filter;
    }

    public Comment getComment() {
        return comment;
    }

    public String getAuthorAvatarImageUrl() {
        String emailAddress = null;
        User createdBy = comment.getCreatedByUser();
        if (createdBy == null) {
            emailAddress = comment.getAnonymousEmailAddress();
        } else {
            emailAddress = createdBy.getEmailAddress();
        }
        if (emailAddress == null) {
            return null;
        }
        emailAddress = emailAddress.trim().toLowerCase(Locale.US);

        String hash = DigestUtils.md5Hex(emailAddress);

        return "https://secure.gravatar.com/avatar/" + hash + "?s=40&d=mm";
    }

    public String getFormattedText() throws TransformerException, IOException, SAXException {
        return ContentUtils.formatText(comment.getContent(), null, comment.getContentType(), filter);
    }

    public String getAuthor() {
        if (comment.getCreatedByUser() != null) {
            return comment.getCreatedByUser().getUserName();
        }
        if (comment.getAnonymousUserName() != null) {
            return comment.getAnonymousUserName();
        }
        return null;
    }

    public String getCommentLink() {
        if (comment.getCreatedByUser() != null) {
            return comment.getCreatedByUser().getWebsite();
        }
        if (comment.getAnonymousUserName() != null) {
            return comment.getAnonymousWebsite();
        }
        return null;
    }

    public boolean isCommentExternal() {
        if (comment.getCreatedByUser() != null) {
            return true;
        }
        return comment.getAnonymousUserName() != null;
    }

}
