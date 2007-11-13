package com.randomcoder.article;

import java.io.IOException;
import java.util.*;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.randomcoder.article.comment.*;
import com.randomcoder.content.*;

/**
 * Helper class which "decorates" an {@code Article} instance by providing XHTML
 * formatting support.
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
public class ArticleDecorator
{
	private final Article article;
	private final ContentFilter filter;
	private final List<CommentDecorator> comments;

	/**
	 * Creates a new decorator using the given article and content filter.
	 * @param article article to decorate
	 * @param filter content filter to parse content with
	 */
	public ArticleDecorator(Article article, ContentFilter filter)
	{
		this.article = article;
		this.filter = filter;
		comments = new ArrayList<CommentDecorator>(article.getComments().size());
		for (Comment comment : article.getComments())
			comments.add(new CommentDecorator(comment, filter));		
	}
	
	/**
	 * Gets the wrapped article.
	 * @return article instance
	 */
	public Article getArticle()
	{
		return article;
	}

	/**
	 * Gets the comments for this article;
	 * @return comment list
	 */
	public List<CommentDecorator> getComments()
	{
		return comments;
	}
	
	/**
	 * Determines if a summary is present for this article.
	 * @return true if summary exists, false otherwise
	 */
	public boolean isSummaryPresent()
	{
		return article.getSummary() != null;
	}
	
	/**
	 * Gets article content after applying filters and HTML escaping.
	 * @return {@code String} containing the article content in XHTML.
	 * @throws TransformerException if filtering fails
	 * @throws IOException if an I/O error occurs
	 * @throws SAXException if parsing fails
	 */
	public String getFormattedText() throws TransformerException, IOException, SAXException
	{
		return ContentUtils.formatText(article.getContent(), null, article.getContentType(), filter);
	}
		
	/**
	 * Gets article summary after applying filters and HTML escaping.
	 * @return {@code String} containing the article summary in XHTML.
	 * @throws TransformerException if filtering fails
	 * @throws IOException if an I/O error occurs
	 * @throws SAXException if parsing fails
	 */
	public String getFormattedSummary() throws TransformerException, IOException, SAXException
	{
		String summary = article.getSummary();
		if (summary == null) return null;
		return ContentUtils.formatText(summary, null, article.getContentType(), filter);
	}
	
	
}
