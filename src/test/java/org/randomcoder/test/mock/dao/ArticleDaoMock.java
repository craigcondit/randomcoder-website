package org.randomcoder.test.mock.dao;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import org.randomcoder.article.*;
import org.randomcoder.db.*;

@SuppressWarnings("javadoc")
public class ArticleDaoMock implements ArticleDao
{
	private final List<Article> articles = new ArrayList<Article>();
	private long primaryKey = 0;

	@Override
	public int countBeforeDate(Date endDate)
	{
		int count = 0;
		
		for (Article article : articles)
			if (article.getCreationDate().before(endDate)) count++;
		
		return count;
	}

	@Override
	public int countByTagBeforeDate(Tag tag, Date endDate)
	{
		int count = 0;
		
		for (Article article : articles)
			if (article.getCreationDate().before(endDate) && article.getTags().contains(tag)) count++;
		
		return count;
	}

	@Override
	public Article findByPermalink(String permalink)
	{
		for (Article article : articles)
			if (permalink.equals(article.getPermalink())) return article;
		
		return null;
	}

	@Override
	public List<Article> listByTag(Tag tag)
	{
		List<Article> list = new LinkedList<Article>();
		for (Article article : articles)
		{
			if (article.getTags().contains(tag)) list.add(article);
		}
		Collections.sort(list, new ArticleDateComparator());
		return list;
	}

	@Override
	public Iterator<Article> iterateByTag(Tag tag)
	{
		return listByTag(tag).iterator();
	}

	@Override
	public List<Article> listAllInRange(int start, int limit)
	{
		List<Article> list = listAll();
		
		// validate range
		if (start < 0 || start >= list.size()) return new ArrayList<Article>();
		
		int end = start + limit;
		if (limit < 1) return new ArrayList<Article>();
		
		if (end > list.size()) end = list.size();

		return list.subList(start, end);
	}

	@Override
	public List<Article> listBeforeDateInRange(Date endDate, int start, int limit)
	{
		List<Article> list = listAll();
		
		// filter
		for (Iterator<Article> it = list.iterator(); it.hasNext();)
		{
			if (!it.next().getCreationDate().before(endDate)) it.remove();
		}
		
		// validate range
		if (start < 0 || start >= list.size()) return new ArrayList<Article>();
		
		int end = start + limit;
		if (limit < 1) return new ArrayList<Article>();
		
		if (end > list.size()) end = list.size();

		return list.subList(start, end);
	}

	@Override
	public List<Article> listBetweenDates(Date startDate, Date endDate)
	{
		List<Article> list = listAll();
		
		// filter
		for (Iterator<Article> it = list.iterator(); it.hasNext();)
		{
			Date creationDate = it.next().getCreationDate();
			if (!creationDate.before(endDate) || creationDate.before(startDate)) it.remove();
		}
		
		return list;
	}

	@Override
	public List<Article> listByTagBeforeDateInRange(Tag tag, Date endDate, int start, int limit)
	{
		List<Article> list = listAll();
		
		// filter
		for (Iterator<Article> it = list.iterator(); it.hasNext();)
		{
			Article article = it.next();
			if (!article.getCreationDate().before(endDate) || !article.getTags().contains(tag)) it.remove();
		}
		
		// validate range
		if (start < 0 || start >= list.size()) return new ArrayList<Article>();
		
		int end = start + limit;
		if (limit < 1) return new ArrayList<Article>();
		
		if (end > list.size()) end = list.size();

		return list.subList(start, end);
	}

	@Override
	public List<Article> listByTagBetweenDates(Tag tag, Date startDate, Date endDate)
	{
		List<Article> list = listAll();
		
		// filter
		for (Iterator<Article> it = list.iterator(); it.hasNext();)
		{
			Article article = it.next();
			Date creationDate = article.getCreationDate();
			if (!creationDate.before(endDate) || creationDate.before(startDate) || !article.getTags().contains(tag)) it.remove();
		}
		
		return list;
	}

	@Override
	public Long create(Article newInstance)
	{
		validateRequiredFields(newInstance);
		
		String permalink = newInstance.getPermalink();
		if (permalink != null && findByPermalink(permalink) != null)
			throw new IllegalArgumentException("UNIQUE violation: permalink = " + permalink);
				
		newInstance.setId(primaryKey++);
		articles.add(newInstance);
		
		return newInstance.getId();
	}

	@Override
	public void delete(Article persistentObject)
	{
		Long id = persistentObject.getId();
		
		for (Iterator<Article> it = articles.iterator(); it.hasNext();)
		{
			if (it.next().getId().equals(id))
			{
				it.remove();
				return;
			}
		}
		throw new IllegalArgumentException("NOT FOUND: id = " + id);
	}

	@Override
	public void update(Article transientObject)
	{
		Long id = transientObject.getId();
		
		Article loaded = read(id);
		if (loaded == null)
			throw new IllegalArgumentException("NOT FOUND: id = " + id);
		
		validateRequiredFields(transientObject);
		
		delete(loaded);
		
		articles.add(transientObject);
	}

	@Override
	public Article read(Long id)
	{
		for (Article article : articles)
			if (article.getId().equals(id))
				return article;
		
		return null;
	}

	private List<Article> listAll()
	{
		List<Article> list = new ArrayList<Article>(articles);
		Collections.sort(list, new ArticleDateComparator());
		return list;
	}
	
	private void validateRequiredFields(Article article)
	{
		if (article.getContentType() == null)
			throw new IllegalArgumentException("contentType required");
		
		if (article.getCreationDate() == null)
			throw new IllegalArgumentException("creationDate required");
				
		if (StringUtils.trimToNull(article.getTitle()) == null)
			throw new IllegalArgumentException("title required");
		
		if (StringUtils.trimToNull(article.getContent()) == null)
			throw new IllegalArgumentException("content required");	
	}
	
	protected class ArticleDateComparator implements Comparator<Article>
	{
		@Override
		public int compare(Article a1, Article a2)
		{
			return a2.getCreationDate().compareTo(a1.getCreationDate());
		}		
	}
}
