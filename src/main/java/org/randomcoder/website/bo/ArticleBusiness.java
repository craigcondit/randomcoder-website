package org.randomcoder.website.bo;

import org.randomcoder.website.data.Article;
import org.randomcoder.website.data.Comment;
import org.randomcoder.website.data.ModerationException;
import org.randomcoder.website.data.Page;
import org.randomcoder.website.data.Tag;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public interface ArticleBusiness {

    Article createArticle(Consumer<Article> visitor, String userName);

    void createComment(Consumer<Comment> visitor, long articleId, String userName, String referrer, String ipAddress, String userAgent);

    Article readArticle(long articleId);

    Article findArticleByPermalink(String permalink);

    void loadArticleForEditing(Consumer<Article> consumer, long articleId, String userName);

    Article updateArticle(Consumer<Article> visitor, long articleId, String userName);

    void deleteArticle(String userName, long articleId);

    Article deleteComment(long commentId);

    Article approveComment(long commentId) throws ModerationException;

    Article disapproveComment(long commentId) throws ModerationException;

    boolean moderateComments(int count) throws ModerationException;

    List<Article> listRecentArticles(int limit);

    Page<Article> listArticlesBeforeDate(Date endDate, long offset, long length);

    Page<Article> listArticlesByTagBeforeDate(Tag tag, Date endDate, long offset, long length);

    List<Article> listArticlesBetweenDates(Date startDate, Date endDate);

    List<Article> listArticlesByTagBetweenDates(Tag tag, Date startDate, Date endDate);

}
