package org.randomcoder.article;

import org.randomcoder.db.Article;
import org.randomcoder.io.Consumer;
import org.randomcoder.tag.TagList;

/**
 * Command class used for updating articles.
 */
public class ArticleEditCommand extends ArticleAddCommand implements Consumer<Article>
{
	private static final long serialVersionUID = 3328453271434578065L;

	private Long id;

	/**
	 * Sets the id of the article to edit.
	 * 
	 * @param id
	 *          article id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the id of the article to edit.
	 * 
	 * @return article id
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Populates the form based on the supplied article
	 */
	@Override
	public void consume(Article article)
	{
		setId(article.getId());
		setTitle(article.getTitle());
		setPermalink(article.getPermalink());
		setContentType(article.getContentType());
		setContent(article.getContent());
		setSummary(article.getSummary());
		setTags(new TagList(article.getTags()));
	}
}
