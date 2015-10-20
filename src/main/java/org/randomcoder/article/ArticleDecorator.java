package org.randomcoder.article;

import org.apache.commons.codec.digest.DigestUtils;
import org.randomcoder.article.comment.CommentDecorator;
import org.randomcoder.content.ContentFilter;
import org.randomcoder.content.ContentUtils;
import org.randomcoder.db.Article;
import org.randomcoder.db.Comment;
import org.randomcoder.db.User;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.TransformerException;

/**
 * Helper class which "decorates" an {@code Article} instance by providing XHTML
 * formatting support.
 */
public class ArticleDecorator
{
	private final Article article;
	private final ContentFilter filter;
	private final List<CommentDecorator> comments;

	/**
	 * Creates a new decorator using the given article and content filter.
	 * 
	 * @param article
	 *            article to decorate
	 * @param filter
	 *            content filter to parse content with
	 */
	public ArticleDecorator(Article article, ContentFilter filter)
	{
		this.article = article;
		this.filter = filter;
		comments = new ArrayList<>(article.getComments().size());
		for (Comment comment : article.getComments())
		{
			comments.add(new CommentDecorator(comment, filter));
		}
	}

	/**
	 * Gets the avatar image URL for the article author.
	 * 
	 * @return image URL or <code>null</code> if not present
	 */
	public String getAuthorAvatarImageUrl()
	{
		User createdBy = article.getCreatedByUser();
		if (createdBy == null)
		{
			return null;
		}
		String emailAddress = createdBy.getEmailAddress();
		if (emailAddress == null)
		{
			return null;
		}
		emailAddress = emailAddress.trim().toLowerCase(Locale.US);

		String hash = DigestUtils.md5Hex(emailAddress);

		return "https://secure.gravatar.com/avatar/" + hash + "?s=40&d=mm";
	}

	/**
	 * Gets the wrapped article.
	 * 
	 * @return article instance
	 */
	public Article getArticle()
	{
		return article;
	}

	/**
	 * Gets the comments for this article;
	 * 
	 * @return comment list
	 */
	public List<CommentDecorator> getComments()
	{
		return comments;
	}

	/**
	 * Determines if a summary is present for this article.
	 * 
	 * @return true if summary exists, false otherwise
	 */
	public boolean isSummaryPresent()
	{
		return article.getSummary() != null;
	}

	/**
	 * Gets article content after applying filters and HTML escaping.
	 * 
	 * @return {@code String} containing the article content in XHTML.
	 * @throws TransformerException
	 *             if filtering fails
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws SAXException
	 *             if parsing fails
	 */
	public String getFormattedText() throws TransformerException, IOException, SAXException
	{
		return ContentUtils.formatText(article.getContent(), null, article.getContentType(), filter);
	}

	public String getCommentCountText()
	{
	  if (comments.size() == 1) {
	    return "1 comment";
	  }
	  
	  if (comments.size() > 1) {
	    return new DecimalFormat("##########").format(comments.size()) + " comments";
	  }
	  
	  if (article.isCommentsEnabled()) {
	    return "Comment on this article";
	  }
	  
	  return "0 comments";
	}
	
	/**
	 * Gets article summary after applying filters and HTML escaping.
	 * 
	 * @return {@code String} containing the article summary in XHTML.
	 * @throws TransformerException
	 *             if filtering fails
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws SAXException
	 *             if parsing fails
	 */
	public String getFormattedSummary() throws TransformerException, IOException, SAXException
	{
		String summary = article.getSummary();
		if (summary == null)
		{
			return null;
		}
		return ContentUtils.formatText(summary, null, article.getContentType(), filter);
	}

}
