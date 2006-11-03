package com.randomcoder.article;

import com.randomcoder.io.*;


/**
 * Business interface for managing articles.
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
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
	 */
	public void createComment(Producer<Comment> comment, Long articleId, String userName);
	
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
}
