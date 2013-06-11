package org.randomcoder.article.comment;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.randomcoder.content.*;
import org.xml.sax.SAXException;

/**
 * Helper class which "decorates" an {@code Comment} instance by providing XHTML
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
public class CommentDecorator
{
	private final Comment comment;
	private final ContentFilter filter;
	
	/**
	 * Creates a new comment decorator using the given comment and filter.
	 * @param comment comment
	 * @param filter content filter
	 */
	public CommentDecorator(Comment comment, ContentFilter filter)
	{
		this.comment = comment;
		this.filter = filter;
	}
	
	/**
	 * Gets the comment wrapped by this decorator.
	 * @return comment
	 */
	public Comment getComment()
	{
		return comment;
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
		return ContentUtils.formatText(comment.getContent(), null, comment.getContentType(), filter);
	}	
}
