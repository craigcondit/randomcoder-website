package org.randomcoder.article.comment;

import java.io.IOException;
import java.util.Locale;

import javax.xml.transform.TransformerException;

import org.apache.commons.codec.digest.DigestUtils;
import org.randomcoder.content.*;
import org.randomcoder.db.*;
import org.xml.sax.SAXException;

/**
 * Helper class which "decorates" an {@code Comment} instance by providing XHTML
 * formatting support.
 */
public class CommentDecorator
{
	private final Comment comment;
	private final ContentFilter filter;

	/**
	 * Creates a new comment decorator using the given comment and filter.
	 * 
	 * @param comment
	 *          comment
	 * @param filter
	 *          content filter
	 */
	public CommentDecorator(Comment comment, ContentFilter filter)
	{
		this.comment = comment;
		this.filter = filter;
	}

	/**
	 * Gets the comment wrapped by this decorator.
	 * 
	 * @return comment
	 */
	public Comment getComment()
	{
		return comment;
	}

	/**
	 * Gets the avatar image URL for the comment author.
	 * 
	 * @return image URL or <code>null</code> if not present
	 */
	public String getAuthorAvatarImageUrl()
	{
		String emailAddress = null;
		User createdBy = comment.getCreatedByUser();
		if (createdBy == null)
		{
			emailAddress = comment.getAnonymousEmailAddress(); 
		}
		else
		{
			emailAddress = createdBy.getEmailAddress();
		}
		if (emailAddress == null)
		{
			return null;
		}
		emailAddress = emailAddress.trim().toLowerCase(Locale.US);

		String hash = DigestUtils.md5Hex(emailAddress);

		return "https://secure.gravatar.com/avatar/" + hash + "?s=40&d=mm";
	}
	
	/**
	 * Gets article content after applying filters and HTML escaping.
	 * 
	 * @return {@code String} containing the article content in XHTML.
	 * @throws TransformerException
	 *           if filtering fails
	 * @throws IOException
	 *           if an I/O error occurs
	 * @throws SAXException
	 *           if parsing fails
	 */
	public String getFormattedText() throws TransformerException, IOException, SAXException
	{
		return ContentUtils.formatText(comment.getContent(), null, comment.getContentType(), filter);
	}
}
