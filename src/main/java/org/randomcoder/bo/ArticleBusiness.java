package org.randomcoder.bo;

import org.randomcoder.article.moderation.ModerationException;
import org.randomcoder.db.Article;
import org.randomcoder.db.Comment;
import org.randomcoder.db.Tag;
import org.randomcoder.io.Consumer;
import org.randomcoder.io.Producer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

/**
 * Business interface for managing articles.
 */
public interface ArticleBusiness {
    /**
     * Create a new article.
     *
     * @param producer article producer
     * @param userName user name
     */
    void createArticle(Producer<Article> producer, String userName);

    /**
     * Creates a new comment.
     *
     * @param comment   comment producer
     * @param articleId article id
     * @param userName  user name
     * @param referrer  HTTP referrer
     * @param ipAddress remote IP address
     * @param userAgent HTTP user-agent
     */
    void createComment(Producer<Comment> comment, Long articleId,
                              String userName, String referrer, String ipAddress, String userAgent);

    /**
     * Reads an existing article.
     *
     * @param articleId article id
     * @return article
     */
    Article readArticle(long articleId);

    /**
     * Loads an {@code Article} by its permalink
     *
     * @param permalink permalink name
     * @return article if found, or <code>null</code> if no match
     */
    Article findArticleByPermalink(String permalink);

    /**
     * Load an existing article for editing.
     *
     * @param consumer  article consumer
     * @param articleId article id
     * @param userName  user name
     */
    void loadArticleForEditing(Consumer<Article> consumer, Long articleId,
                                      String userName);

    /**
     * Update an existing article.
     *
     * @param producer  article producer
     * @param articleId article id
     * @param userName  user name
     */
    void updateArticle(Producer<Article> producer, Long articleId,
                              String userName);

    /**
     * Delete an article.
     *
     * @param userName  user name
     * @param articleId article id
     */
    void deleteArticle(String userName, Long articleId);

    /**
     * Deletes a comment.
     *
     * @param commentId comment id
     * @return Article which comment belongs to
     */
    Article deleteComment(Long commentId);

    /**
     * Approves a comment.
     *
     * @param commentId comment id
     * @return Article which comment belongs to
     * @throws ModerationException if moderation cannot be completed
     */
    Article approveComment(Long commentId) throws ModerationException;

    /**
     * Disapproves a comment.
     *
     * @param commentId comment id
     * @return Article which comment belongs to
     * @throws ModerationException if moderation cannot be completed
     */
    Article disapproveComment(Long commentId) throws ModerationException;

    /**
     * Moderate a batch of comments.
     *
     * @param count number of comments to moderate
     * @return true if comments were moderated, false otherwise
     * @throws ModerationException if moderation cannot be completed
     */
    boolean moderateComments(int count) throws ModerationException;

    /**
     * Lists {@code Article} objects within the range specified.
     *
     * @param limit maximum number of results to return
     * @return list of {@code Article} objects
     */
    List<Article> listRecentArticles(int limit);

    /**
     * Lists a page of {@code Article} objects created before the specified
     * date.
     *
     * @param endDate  upper bound of date range (exclusive)
     * @param pageable paging variables
     * @return page of {@code Article} objects
     */
    Page<Article> listArticlesBeforeDate(Date endDate, Pageable pageable);

    /**
     * Lists a page of {@code Article} objects created before the specified
     * date.
     *
     * @param tag      tag to restrict by
     * @param endDate  upper bound of date range (exclusive)
     * @param pageable paging variables
     * @return page of {@code Article} objects
     */
    Page<Article> listArticlesByTagBeforeDate(Tag tag, Date endDate,
                                                     Pageable pageable);

    /**
     * Lists {@code Article} objects created within the specified date range.
     *
     * @param startDate lower bound of date range (inclusive)
     * @param endDate   upper bound of date range (exclusive)
     * @return list of {@code Article} objects
     */
    List<Article> listArticlesBetweenDates(Date startDate, Date endDate);

    /**
     * Lists {@code Article} objects created within the specified date range.
     *
     * @param tag       tag to restrict by
     * @param startDate lower bound of date range (inclusive)
     * @param endDate   upper bound of date range (exclusive)
     * @return list of {@code Article} objects
     */
    List<Article> listArticlesByTagBetweenDates(Tag tag, Date startDate,
                                                       Date endDate);
}
