package org.randomcoder.mvc.editor;

import org.apache.commons.lang.StringUtils;
import org.randomcoder.bo.TagBusiness;
import org.randomcoder.db.Tag;
import org.randomcoder.tag.TagList;
import org.randomcoder.validation.DataValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tag list property editor.
 */
public class TagListPropertyEditor extends PropertyEditorSupport
{
	private static final Logger logger = LoggerFactory.getLogger(TagListPropertyEditor.class);

	private TagBusiness tagBusiness;

	/**
	 * Creates a new TagList property editor.
	 * 
	 * @param tagBusiness
	 *          TagBusiness implementation to use
	 */
	public TagListPropertyEditor(TagBusiness tagBusiness)
	{
		this.tagBusiness = tagBusiness;
	}

	/**
	 * Gets the value of the current tag list as a string
	 */
	@Override
	public String getAsText()
	{
		logger.debug("getAsText()");

		Object value = getValue();
		if (value == null)
		{
			return "";
		}

		TagList tagList = (TagList) value;

		List<Tag> tags = tagList.getTags();

		StringBuilder buf = new StringBuilder();

		for (Tag tag : tags)
		{
			if (buf.length() > 0)
			{
				buf.append(", ");
			}
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

		Set<String> names = new HashSet<>();

		List<Tag> tags = new ArrayList<>();

		for (String tagName : tagNames)
		{
			tagName = tagName.replaceAll("\\s+", " ").trim();

			String name = DataValidationUtils.canonicalizeTagName(tagName);

			if (name != null && !names.contains(name))
			{
				// find tag in db
				Tag tag = tagBusiness.findTagByName(name);

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
