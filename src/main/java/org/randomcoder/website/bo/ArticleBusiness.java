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

    void createArticle(Consumer<Article> visitor, String userName);

    void createComment(Consumer<Comment> visitor, Long articleId, String userName, String referrer, String ipAddress, String userAgent);

    Article readArticle(long articleId);

    Article findArticleByPermalink(String permalink);

    void loadArticleForEditing(Consumer<Article> consumer, Long articleId, String userName);

    void updateArticle(Consumer<Article> visitor, Long articleId, String userName);

    void deleteArticle(String userName, Long articleId);

    Article deleteComment(Long commentId);

    Article approveComment(Long commentId) throws ModerationException;

    Article disapproveComment(Long commentId) throws ModerationException;

    boolean moderateComments(int count) throws ModerationException;

    List<Article> listRecentArticles(int limit);

    Page<Article> listArticlesBeforeDate(Date endDate, long offset, long length);

    Page<Article> listArticlesByTagBeforeDate(Tag tag, Date endDate, long offset, long length);

    List<Article> listArticlesBetweenDates(Date startDate, Date endDate);

    List<Article> listArticlesByTagBetweenDates(Tag tag, Date startDate, Date endDate);

}
