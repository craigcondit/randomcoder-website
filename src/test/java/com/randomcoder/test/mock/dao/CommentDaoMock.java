package com.randomcoder.test.mock.dao;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.randomcoder.article.comment.*;
import com.randomcoder.article.moderation.ModerationStatus;

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

	public void update(Comment transientObject)
	{
		Long id = transientObject.getId();
		
		Comment loaded = read(id);
		if (loaded == null)
			throw new IllegalArgumentException("NOT FOUND: id = " + id);
		
		validateRequiredFields(transientObject);
		
		delete(loaded);
		
		comments.add(transientObject);
	}

	public Iterator<Comment> iterateForModerationInRange(int start, int limit)
	{
		List<Comment> list = new ArrayList<Comment>(comments);
		for (Iterator<Comment> i = list.iterator(); i.hasNext();)
		{
			Comment c = i.next();
			if (!c.getModerationStatus().equals(ModerationStatus.PENDING)) i.remove();
		}		
		Collections.sort(list, new CreationDateComparator());
		
		// validate range
		if (start < 0 || start >= list.size()) return new ArrayList<Comment>().iterator();
		
		int end = start + limit;
		if (limit < 1) return new ArrayList<Comment>().iterator();
		
		if (end > list.size()) end = list.size();

		return list.subList(start, end).iterator();
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
	
	static class CreationDateComparator implements Comparator<Comment>
	{
		public int compare(Comment o1, Comment o2)
		{
			return o1.getCreationDate().compareTo(o2.getCreationDate());
		}
		
	}
}
