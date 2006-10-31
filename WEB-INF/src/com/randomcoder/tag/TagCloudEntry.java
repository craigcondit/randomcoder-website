package com.randomcoder.tag;

import org.apache.commons.logging.*;


/**
 * Statisitics for Tag instances with extensions for creating tag clouds.
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
public class TagCloudEntry extends TagStatistics
{
	private static final long serialVersionUID = 7928407251644710555L;
	
	private static final Log logger = LogFactory.getLog(TagCloudEntry.class);
	
	private int scale;
	
	/**
	 * Constructs an empty TagCloudEntry.
	 */
	public TagCloudEntry() { super(); }

	/**
	 * Creates a Tag cloud entry from the given statistics
	 * @param stat Tag statistics
	 * @param maximumArticleCount ceiling value for article count
	 */
	public TagCloudEntry(TagStatistics stat, int maximumArticleCount)
	{
		super(stat.getTag(), stat.getArticleCount());
		
		if (maximumArticleCount <= 0)
		{
			scale = 0;
		}
		else
		{
			if (logger.isDebugEnabled())
			{
				logger.debug("Tag: " + getTag().getName() + " -> " + getArticleCount() + " / " + maximumArticleCount + " = " + ((getArticleCount() * 10) / maximumArticleCount));
			}
			
			setScale((getArticleCount() * 10) / maximumArticleCount);
		}
	}
	
	/**
	 * Gets the scale factor (0-9) for this tag.
	 * @return scale factor
	 */
	public int getScale()
	{
		return scale;
	}
	
	/**
	 * Sets the scale factor (0-9) for this tag.
	 * <p>
	 * A scale factor of 0 indicates the minimum size. A scale factor of 9
	 * indicates the largest size (most popular). Negative values will be
	 * translated to zero; Values greater than 9 will become 9.
	 * </p>
	 * @param scale scale factor
	 */
	public void setScale(int scale)
	{
		if (scale < 0) scale = 0;
		if (scale > 9) scale = 9;
		this.scale = scale;		
	}
}
