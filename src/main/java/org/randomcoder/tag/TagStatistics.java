package com.randomcoder.tag;

import java.io.Serializable;


/**
 * Statisitics for Tag instances (article count, etc).
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
public class TagStatistics implements Serializable
{
	private static final long serialVersionUID = 7563892982366083919L;
	
	private Tag tag;
	private int articleCount;
		
	/**
	 * Default constructor.
	 */
	public TagStatistics() {}
	
	/**
	 * Creates a new TagStatistics object with the given tag and article count.
	 * @param tag tag
	 * @param articleCount article count
	 */
	public TagStatistics(Tag tag, int articleCount)
	{
		this.tag = tag;
		this.articleCount = articleCount;
	}
	
	/**
	 * Gets the tag to which statistics apply.
	 * @return Tag instance
	 */
	public Tag getTag()
	{
		return tag;
	}
	
	/**
	 * Sets the tag to which statistics apply.
	 * @param tag Tag instance
	 */
	public void setTag(Tag tag)
	{
		this.tag = tag;
	}
	
	/**
	 * Gets the number of articles which this tag contains.
	 * @return article count
	 */
	public int getArticleCount()
	{
		return articleCount;
	}
	
	/**
	 * Sets the number of articles which this tag contains.
	 * @param articleCount article count
	 */
	public void setArticleCount(int articleCount)
	{
		this.articleCount = articleCount;
	}
	
}
