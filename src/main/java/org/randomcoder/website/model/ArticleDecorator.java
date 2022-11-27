package org.randomcoder.website.model;

import org.apache.commons.codec.digest.DigestUtils;
import org.randomcoder.website.contentfilter.ContentFilter;
import org.randomcoder.website.contentfilter.ContentUtils;
import org.randomcoder.website.data.Article;
import org.randomcoder.website.data.Comment;
import org.randomcoder.website.data.User;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArticleDecorator {
    private final Article article;
    private final ContentFilter filter;
    private final List<CommentDecorator> comments;

    public ArticleDecorator(Article article, ContentFilter filter) {
        this.article = article;
        this.filter = filter;
        comments = new ArrayList<>(article.getComments().size());
        for (Comment comment : article.getComments()) {
            comments.add(new CommentDecorator(comment, filter));
        }
    }

    public String getAuthorAvatarImageUrl() {
        User createdBy = article.getCreatedByUser();
        if (createdBy == null) {
            return null;
        }
        String emailAddress = createdBy.getEmailAddress();
        if (emailAddress == null) {
            return null;
        }
        emailAddress = emailAddress.trim().toLowerCase(Locale.US);

        String hash = DigestUtils.md5Hex(emailAddress);

        return "https://secure.gravatar.com/avatar/" + hash + "?s=40&d=mm";
    }

    public Article getArticle() {
        return article;
    }

    public List<CommentDecorator> getComments() {
        return comments;
    }

    public boolean isSummaryPresent() {
        return article.getSummary() != null;
    }

    public String getFormattedText() throws TransformerException, IOException, SAXException {
        return ContentUtils.formatText(article.getContent(), null, article.getContentType(), filter);
    }

    public String getCommentCountText() {
        if (comments.size() == 1) {
            return "1 comment";
        }

        if (comments.size() > 1) {
            return new DecimalFormat("##########").format(comments.size())
                    + " comments";
        }

        if (article.isCommentsEnabled()) {
            return "Comment on this article";
        }

        return "0 comments";
    }

    public String getFormattedSummary() throws TransformerException, IOException, SAXException {
        String summary = article.getSummary();
        if (summary == null) {
            return null;
        }
        return ContentUtils.formatText(summary, null, article.getContentType(), filter);
    }

}
