package org.randomcoder.article;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.*;
import org.randomcoder.content.ContentType;
import org.randomcoder.db.*;
import org.randomcoder.io.Producer;
import org.randomcoder.tag.TagList;

/**
 * Command class used for adding articles.
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
public class ArticleAddCommand implements Producer<Article>, Serializable
{
	private static final long serialVersionUID = -6429429692732106826L;

	/**
	 * Article title.
	 */
	protected String title;

	/**
	 * Content type.
	 */
	protected ContentType contentType;

	/**
	 * Permalink.
	 */
	protected String permalink;

	/**
	 * Tags.
	 */
	protected TagList tags;
	
	/**
	 * Textual content.
	 */
	protected String content;

	/**
	 * Article summary.
	 */
	protected String summary;
	
	/**
	 * Sets the title of the article.
	 * @param title article title
	 */
	public void setTitle(String title)
	{
		this.title = StringUtils.trimToNull(title);
	}

	/**
	 * Gets the title of the article.
	 * @return article title
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * Sets the content type of the article.
	 * @param contentType content type
	 */
	public void setContentType(ContentType contentType)
	{
		this.contentType = contentType;
	}

	/**
	 * Gets the content type of the article.
	 * @return content type
	 */
	public ContentType getContentType()
	{
		return contentType;
	}

	/**
	 * Sets the permalink for this article.
	 * @param permalink permalink
	 */
	public void setPermalink(String permalink)
	{
		this.permalink = StringUtils.stripToNull(StringUtils.defaultString(permalink).toLowerCase(Locale.US));
	}

	/**
	 * Gets the permalink for this article.
	 * @return permalink
	 */
	public String getPermalink()
	{
		return permalink;
	}

	/**
	 * Sets the tags for this article.
	 * @param tags tags
	 */
	public void setTags(TagList tags)
	{
		this.tags = tags;
	}
	
	/**
	 * Gets the tags for this article.
	 * @return tags
	 */
	public TagList getTags()
	{
		return tags;
	}
	
	/**
	 * Sets the textual content of the article.
	 * @param content article content
	 */
	public void setContent(String content)
	{
		this.content = StringUtils.trimToNull(content);
	}

	/**
	 * Gets the textual content of the article.
	 * @return article content
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * Gets the summary text for this article.
	 * @return summary text
	 */
	public String getSummary()
	{
		return summary;
	}
	
	/**
	 * Sets the summary text for this article.
	 * @param summary summary text
	 */
	public void setSummary(String summary)
	{
		this.summary = StringUtils.trimToNull(summary);
	}
	
	/**
	 * Writes out the contents of the form to the given article.
	 */
	@Override
	public void produce(Article article)
	{
		article.setTitle(title);
		article.setContentType(contentType);
		article.setPermalink(permalink);
		article.setContent(content);
		article.setSummary(summary);
		
		if (article.getTags() == null) article.setTags(new ArrayList<Tag>());
		
		Set<Tag> currentTags = new HashSet<Tag>(article.getTags());
		Set<Tag> selectedTags = new HashSet<Tag>(tags.getTags());
		
		// get list of deleted tags (current - selected)
		Set<Tag> deletedTags = new HashSet<Tag>(currentTags);
		deletedTags.removeAll(selectedTags);
		
		// get list of added tags (selected - current)
		Set<Tag> addedTags = new HashSet<Tag>(selectedTags);
		addedTags.removeAll(currentTags);
		
		// remove deleted tags 
		article.getTags().removeAll(deletedTags);
		
		// add new tags
		article.getTags().addAll(addedTags);		
	}

	/**
	 * Gets a string representation of this object, suitable for debugging.
	 * @return string representation of this object
	 */
	@Override
	public String toString()
	{
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
}
