package com.randomcoder.test.mock.dao;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.randomcoder.article.*;

public class CommentDaoMock implements CommentDao
{
	private final List<Comment> comments = new ArrayList<Comment>();
	private long primaryKey;
	
	public Long create(Comment newInstance)
	{
		validateRequiredFields(newInstance);		
		newInstance.setId(primaryKey++);
		comments.add(newInstance);		
		return newInstance.getId();
	}

	public void delete(Comment persistentObject)
	{
		Long id = persistentObject.getId();
		
		for (Iterator<Comment> it = comments.iterator(); it.hasNext();)
		{
			if (it.next().getId().equals(id))
			{
				it.remove();
				return;
			}
		}
		throw new IllegalArgumentException("NOT FOUND: id = " + id);
	}

	public Comment read(Long id)
	{
		for (Comment comment : comments)
			if (comment.getId().equals(id))
				return comment;
		
		return null;
	}

	private void validateRequiredFields(Comment comment)
	{
		if (comment.getArticle() == null)
			throw new IllegalArgumentException("article required");
		
		if (comment.getContentType() == null)
			throw new IllegalArgumentException("contentType required");
		
		if (comment.getCreationDate() == null)
			throw new IllegalArgumentException("creationDate required");
		
		if (StringUtils.trimToNull(comment.getTitle()) == null)
			throw new IllegalArgumentException("title required");
		
		if (StringUtils.trimToNull(comment.getContent()) == null)
			throw new IllegalArgumentException("content required");
	}
}
