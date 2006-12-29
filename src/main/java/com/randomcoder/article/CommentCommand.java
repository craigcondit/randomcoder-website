package com.randomcoder.article;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.randomcoder.content.ContentType;
import com.randomcoder.io.Producer;

/**
 * Command class for comment posting.
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
public class CommentCommand implements Serializable, Producer<Comment>
{
	private static final long serialVersionUID = -1245687879900306444L;

	private Article article;
	private boolean anonymous;

	private String anonymousUserName;
	private String anonymousEmailAddress;
	private String anonymousWebsite;
	
	private String title;
	private String content;	
	
	/**
	 * Gets the article associated with this comment command
	 * @return article
	 */
	public Article getArticle()
	{
		return article;
	}
	
	/**
	 * Determines if this comment is anonymous or not.
	 * @return true if anonymous, false otherwise
	 */
	public boolean isAnonymous()
	{
		return anonymous;
	}
	
	/**
	 * Binds non-request parameters to form.
	 * @param parent parent article
	 * @param isAnonymous true if anonymous, false otherwise
	 */
	public void bind(Article parent, boolean isAnonymous)	
	{
		this.article = parent;
		this.anonymous = isAnonymous;
	}
	
	/**
	 * Get the anonymous name of the comment poster.
	 * @return anonymous name
	 */
	public String getAnonymousUserName()
	{
		return anonymousUserName;
	}
	
	/**
	 * Set the anonymous name of the comment poster.
	 * @param anonymousUserName anonymous name
	 */
	public void setAnonymousUserName(String anonymousUserName)
	{
		this.anonymousUserName = StringUtils.trimToNull(anonymousUserName);
	}
	
	/**
	 * Gets the anonymous email address of the comment poster.
	 * @return anonymous email address
	 */
	public String getAnonymousEmailAddress()
	{
		return anonymousEmailAddress;
	}
	
	/**
	 * Sets the anonymous email address of the comment poster.
	 * @param anonymousEmailAddress email address
	 */
	public void setAnonymousEmailAddress(String anonymousEmailAddress)
	{
		this.anonymousEmailAddress = StringUtils.trimToNull(anonymousEmailAddress);
	}
	
	/**
	 * Gets the anonymous website associated with this user.
	 * @return anonymous web site
	 */
	public String getAnonymousWebsite()
	{
		return anonymousWebsite;
	}
	
	/**
	 * Sets the anonymous website associated with this user.
	 * @param anonymousWebsite
	 */
	public void setAnonymousWebsite(String anonymousWebsite)
	{
		this.anonymousWebsite = StringUtils.trimToNull(anonymousWebsite);
	}
	
	/**
	 * Gets the title of this comment.
	 * @return title
	 */
	public String getTitle()
	{
		return title;
	}
	
	/**
	 * Sets the title of this comment.
	 * @param title title
	 */
	public void setTitle(String title)
	{
		this.title = StringUtils.trimToNull(title);
	}
	
	/**
	 * Gets the text of the comment.
	 * @return comment text
	 */
	public String getContent()
	{
		return content;
	}
	
	/**
	 * Sets the text of the comment.
	 * @param content comment text
	 */
	public void setContent(String content)
	{
		this.content = StringUtils.trimToNull(content);
	}

	/**
	 * Populates a comment object with data.
	 */
	public void produce(Comment comment)
	{
		if (anonymous)
		{
			comment.setAnonymousUserName(anonymousUserName);
			comment.setAnonymousEmailAddress(anonymousEmailAddress);
			comment.setAnonymousWebsite(anonymousWebsite);
		}
		else
		{
			comment.setAnonymousUserName(null);
			comment.setAnonymousEmailAddress(null);
			comment.setAnonymousWebsite(null);
		}
		
		comment.setTitle(title);
		comment.setContent(content);
		
		// TODO allow other types
		comment.setContentType(ContentType.TEXT);
	}
	
}
