package org.randomcoder.mvc.command;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.randomcoder.db.Tag;
import org.randomcoder.io.Producer;
import org.randomcoder.validation.DataValidationUtils;

/**
 * Command class for adding tags.
 */
public class TagAddCommand implements Serializable, Producer<Tag>
{
	private static final long serialVersionUID = 7436171478771499999L;

	private String name;
	private String displayName;

	/**
	 * Gets the name for this tag.
	 * 
	 * @return tag name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name for this tag.
	 * 
	 * @param name
	 *          tag name
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
	 * 
	 * @return display name
	 */
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * Sets the display name for this tag.
	 * 
	 * @param displayName
	 *          display name
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
