package com.randomcoder.article;

import com.randomcoder.bean.*;
import com.randomcoder.io.Consumer;

/**
 * Command class used for updating articles.
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
public class ArticleEditCommand extends ArticleAddCommand implements Consumer<Article>
{
	private static final long serialVersionUID = 3328453271434578065L;

	private Long id;

	/**
	 * Sets the id of the article to edit.
	 * @param id article id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the id of the article to edit.
	 * @return article id
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Populates the form based on the supplied article
	 */
	public void consume(Article article)
	{
		setId(article.getId());
		setTitle(article.getTitle());
		setPermalink(article.getPermalink());
		setContentType(article.getContentType());
		setContent(article.getContent());
		setTags(new TagList(article.getTags()));
	}
}
