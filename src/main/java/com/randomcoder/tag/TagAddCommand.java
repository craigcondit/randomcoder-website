package com.randomcoder.tag;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.randomcoder.io.Producer;
import com.randomcoder.validation.DataValidationUtils;

/**
 * Command class for adding tags.
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
public class TagAddCommand implements Serializable, Producer<Tag>
{
	private static final long serialVersionUID = 7436171478771499999L;
	
	private String name;
	private String displayName;
	
	/**
	 * Gets the name for this tag.
	 * @return tag name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Sets the name for this tag.
	 * @param name tag name
	 */
	public void setName(String name)
	{
		if (name != null)
		{
			name = name.replaceAll("\\s+", " ").trim();
			name = DataValidationUtils.canonicalizeTagName(name);
			name = StringUtils.trimToNull(name);
		}
		
		this.name = name;
	}
	
	/**
	 * Gets the display name for this tag.
	 * @return display name
	 */
	public String getDisplayName()
	{
		return displayName;
	}
	
	/**
	 * Sets the display name for this tag.
	 * @param displayName display name
	 */
	public void setDisplayName(String displayName)
	{
		this.displayName = StringUtils.trimToNull(displayName);
	}

	@Override
	public void produce(Tag tag)
	{
		if (tag.getId() == null)
		{
			tag.setName(getName());
		}
		
		tag.setDisplayName(getDisplayName());
	}
	
}
