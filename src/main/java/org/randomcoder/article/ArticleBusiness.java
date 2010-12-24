package org.randomcoder.article;

import org.randomcoder.article.comment.Comment;
import org.randomcoder.article.moderation.ModerationException;
import org.randomcoder.io.*;

/**
 * Business interface for managing articles.
 * 
 * <pre>
 * Copyright (c) 2006-2007, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
public interface ArticleBusiness
{
	/**
	 * Create a new article.
	 * @param producer article producer
	 * @param userName user name
	 */
	public void createArticle(Producer<Article> producer, String userName);
	
	/**
	 * Creates a new comment.
	 * @param comment comment producer
	 * @param articleId article id
	 * @param userName user name
	 * @param referrer HTTP referrer
	 * @param ipAddress remote IP address
	 * @param userAgent HTTP user-agent
	 */
	public void createComment(Producer<Comment> comment, Long articleId, String userName, String referrer, String ipAddress, String userAgent);
	
	/**
	 * Load an existing article for editing.
	 * @param consumer article consumer
	 * @param articleId article id
	 * @param userName user name
	 */
	public void loadArticleForEditing(Consumer<Article> consumer, Long articleId, String userName);
	
	/**
	 * Update an existing article.
	 * @param producer article producer
	 * @param articleId article id
	 * @param userName user name
	 */
	public void updateArticle(Producer<Article> producer, Long articleId, String userName);
	
	/**
	 * Delete an article.
	 * @param userName user name
	 * @param articleId article id
	 */
	public void deleteArticle(String userName, Long articleId);
	
	/**
	 * Deletes a comment.
	 * @param commentId comment id
	 * @return Article which comment belongs to
	 */
	public Article deleteComment(Long commentId);
	
	/**
	 * Approves a comment.
	 * @param commentId comment id
	 * @return Article which comment belongs to
	 * @throws ModerationException if moderation cannot be completed
	 */
	public Article approveComment(Long commentId) throws ModerationException;
	
	/**
	 * Disapproves a comment.
	 * @param commentId comment id
	 * @return Article which comment belongs to
	 * @throws ModerationException if moderation cannot be completed
	 */
	public Article disapproveComment(Long commentId) throws ModerationException;
	
	/**
	 * Moderate a batch of comments.
	 * @param count number of comments to moderate
	 * @return true if comments were moderated, false otherwise
	 * @throws ModerationException if moderation cannot be completed
	 */
	public boolean moderateComments(int count) throws ModerationException;
}
