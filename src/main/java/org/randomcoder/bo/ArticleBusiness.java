package org.randomcoder.bo;

import java.util.*;

import org.randomcoder.article.moderation.ModerationException;
import org.randomcoder.db.*;
import org.randomcoder.io.*;

/**
 * Business interface for managing articles.
 */
public interface ArticleBusiness
{
	/**
	 * Create a new article.
	 * 
	 * @param producer
	 *            article producer
	 * @param userName
	 *            user name
	 */
	public void createArticle(Producer<Article> producer, String userName);

	/**
	 * Creates a new comment.
	 * 
	 * @param comment
	 *            comment producer
	 * @param articleId
	 *            article id
	 * @param userName
	 *            user name
	 * @param referrer
	 *            HTTP referrer
	 * @param ipAddress
	 *            remote IP address
	 * @param userAgent
	 *            HTTP user-agent
	 */
	public void createComment(Producer<Comment> comment, Long articleId, String userName, String referrer, String ipAddress, String userAgent);

	/**
	 * Reads an existing article.
	 * 
	 * @param articleId
	 *            article id
	 * @return article
	 */
	public Article readArticle(long articleId);

	/**
	 * Loads an {@code Article} by its permalink
	 * 
	 * @param permalink
	 *            permalink name
	 * @return article if found, or <code>null</code> if no match
	 */
	public Article findArticleByPermalink(String permalink);

	/**
	 * Load an existing article for editing.
	 * 
	 * @param consumer
	 *            article consumer
	 * @param articleId
	 *            article id
	 * @param userName
	 *            user name
	 */
	public void loadArticleForEditing(Consumer<Article> consumer, Long articleId, String userName);

	/**
	 * Update an existing article.
	 * 
	 * @param producer
	 *            article producer
	 * @param articleId
	 *            article id
	 * @param userName
	 *            user name
	 */
	public void updateArticle(Producer<Article> producer, Long articleId, String userName);

	/**
	 * Delete an article.
	 * 
	 * @param userName
	 *            user name
	 * @param articleId
	 *            article id
	 */
	public void deleteArticle(String userName, Long articleId);

	/**
	 * Deletes a comment.
	 * 
	 * @param commentId
	 *            comment id
	 * @return Article which comment belongs to
	 */
	public Article deleteComment(Long commentId);

	/**
	 * Approves a comment.
	 * 
	 * @param commentId
	 *            comment id
	 * @return Article which comment belongs to
	 * @throws ModerationException
	 *             if moderation cannot be completed
	 */
	public Article approveComment(Long commentId) throws ModerationException;

	/**
	 * Disapproves a comment.
	 * 
	 * @param commentId
	 *            comment id
	 * @return Article which comment belongs to
	 * @throws ModerationException
	 *             if moderation cannot be completed
	 */
	public Article disapproveComment(Long commentId) throws ModerationException;

	/**
	 * Moderate a batch of comments.
	 * 
	 * @param count
	 *            number of comments to moderate
	 * @return true if comments were moderated, false otherwise
	 * @throws ModerationException
	 *             if moderation cannot be completed
	 */
	public boolean moderateComments(int count) throws ModerationException;

	/**
	 * Counts all {@code Article} objects created before the specified date.
	 * 
	 * @param endDate
	 *            upper bound of date range (exclusive)
	 * @return count of articles
	 */
	public int countArticlesBeforeDate(Date endDate);

	/**
	 * Counts all {@code Article} objects created before the specified date.
	 * 
	 * @param tag
	 *            tag to restrict by
	 * @param endDate
	 *            upper bound of date range (exclusive)
	 * @return count of articles
	 */
	public int countArticlesByTagBeforeDate(Tag tag, Date endDate);

	/**
	 * Lists {@code Article} objects within the range specified.
	 * 
	 * @param start
	 *            starting result to return, from 0
	 * @param limit
	 *            maximum number of results to return
	 * @return list of {@code Article} objects
	 */
	public List<Article> listArticlesInRange(int start, int limit);

	/**
	 * Lists {@code Article} objects created before the specified date and
	 * within the range specified.
	 * 
	 * @param endDate
	 *            upper bound of date range (exclusive)
	 * @param start
	 *            starting result to return, from 0
	 * @param limit
	 *            maximum number of results to return
	 * @return list of {@code Article} objects
	 */
	public List<Article> listArticlesBeforeDateInRange(Date endDate, int start, int limit);

	/**
	 * Lists {@code Article} objects created before the specified date and
	 * within the range specified.
	 * 
	 * @param tag
	 *            tag to restrict by
	 * @param endDate
	 *            upper bound of date range (exclusive)
	 * @param start
	 *            starting result to return, from 0
	 * @param limit
	 *            maximum number of results to return
	 * @return list of {@code Article} objects
	 */
	public List<Article> listArticlesByTagBeforeDateInRange(Tag tag, Date endDate, int start, int limit);

	/**
	 * Lists {@code Article} objects created within the specified date range.
	 * 
	 * @param startDate
	 *            lower bound of date range (inclusive)
	 * @param endDate
	 *            upper bound of date range (exclusive)
	 * @return list of {@code Article} objects
	 */
	public List<Article> listArticlesBetweenDates(Date startDate, Date endDate);
	
	/**
	 * Lists {@code Article} objects created within the specified date range.
	 * 
	 * @param tag
	 *            tag to restrict by
	 * @param startDate
	 *            lower bound of date range (inclusive)
	 * @param endDate
	 *            upper bound of date range (exclusive)
	 * @return list of {@code Article} objects
	 */
	public List<Article> listArticlesByTagBetweenDates(Tag tag, Date startDate, Date endDate);	
}
