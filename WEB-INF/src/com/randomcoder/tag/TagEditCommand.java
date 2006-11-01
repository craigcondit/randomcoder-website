package com.randomcoder.tag;

import com.randomcoder.bean.Tag;
import com.randomcoder.io.Consumer;

public class TagEditCommand extends TagAddCommand implements Consumer<Tag>
{
	private static final long serialVersionUID = -4674274359838467817L;
	
	private Long id;
	
	/**
	 * Gets the id for this tag.
	 * @return id
	 */
	public Long getId()
	{
		return id;
	}
	
	/**
	 * Sets the id for this tag.
	 * @param id id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	public void consume(Tag tag)
	{
		setId(tag.getId());
		setName(tag.getName());
		setDisplayName(tag.getDisplayName());
	}
	
}
