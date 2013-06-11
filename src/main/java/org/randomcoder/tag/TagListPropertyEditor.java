package org.randomcoder.tag;

import java.beans.PropertyEditorSupport;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.randomcoder.db.TagDao;
import org.randomcoder.validation.DataValidationUtils;

/**
 * Tag list property editor.
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
public class TagListPropertyEditor extends PropertyEditorSupport
{
	private static final Log logger = LogFactory.getLog(TagListPropertyEditor.class);
	
	private TagDao tagDao;
	
	/**
	 * Creates a new TagList property editor.
	 * @param tagDao TagDao implementation to use
	 */
	public TagListPropertyEditor(TagDao tagDao)
	{
		this.tagDao = tagDao;
	}

	/**
	 * Gets the value of the current tag list as a string
	 */
	@Override
	public String getAsText()
	{
		logger.debug("getAsText()");
		
		Object value = getValue();
		if (value == null) return "";
		
		TagList tagList = (TagList) value;
		
		List<Tag> tags = tagList.getTags();
		
		StringBuilder buf = new StringBuilder();
		
		for (Tag tag : tags)
		{
			if (buf.length() > 0) buf.append(", ");
			buf.append(tag.getName());
		}
		
		return buf.toString();
	}

	/**
	 * Creates a tag list from a comma-separated string
	 */
	@Override
	public void setAsText(String value) throws IllegalArgumentException
	{
		logger.debug("setAsText(" + value + ")");

		value = StringUtils.trimToEmpty(value);
		
		String[] tagNames = value.split(",");
		
		Set<String> names = new HashSet<String>();
		
		List<Tag> tags = new ArrayList<Tag>();
		
		for (String tagName : tagNames)
		{
			tagName = tagName.replaceAll("\\s+", " ").trim();
			
			String name = DataValidationUtils.canonicalizeTagName(tagName);
			
			if (name != null && !names.contains(name))
			{
				// find tag in db
				Tag tag = tagDao.findByName(name);
				
				if (tag == null)
				{
					// create a new one
					tag = new Tag();
					tag.setName(name);
					tag.setDisplayName(tagName);
				}
				
				tags.add(tag);
				names.add(name);
			}			
		}
		
		// sort tags
		Collections.sort(tags, Tag.DISPLAY_NAME_COMPARATOR);
		
		setValue(new TagList(tags));
	}
}
