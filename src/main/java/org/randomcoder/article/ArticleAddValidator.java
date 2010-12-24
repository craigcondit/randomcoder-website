package com.randomcoder.article;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.*;

import com.randomcoder.content.*;
import com.randomcoder.io.SequenceReader;

/**
 * Validator for adding articles.
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
 *         
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *         
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *             
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS &quot;AS IS&quot;
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
public class ArticleAddValidator implements Validator
{
	private static final String ERROR_ARTICLE_CONTENT_INVALID = "error.article.content.invalid";
	private static final String ERROR_ARTICLE_CONTENT_REQUIRED = "error.article.content.required";
	private static final String ERROR_ARTICLE_SUMMARY_INVALID = "error.article.summary.invalid";
	private static final String ERROR_ARTICLE_SUMMARY_TOO_LONG = "error.article.summary.toolong";	
	private static final String ERROR_ARTICLE_CONTENT_TYPE_REQUIRED = "error.article.contentType.required";
	private static final String ERROR_ARTICLE_TITLE_REQUIRED = "error.article.title.required";
	private static final String ERROR_ARTICLE_NULL = "error.article.null";
	private static final String ERROR_ARTICLE_PERMALINK_INVALID = "error.article.permalink.invalid";
	private static final int DEFAULT_MAX_SUMMARY_LENGTH = 1000;
	
	private static final Log logger = LogFactory.getLog(ArticleAddValidator.class);
	
	/**
	 * Message resource for permalink exists message.
	 */
	protected static final String ERROR_ARTICLE_PERMALINK_EXISTS = "error.article.permalink.exists";

	private ContentFilter contentFilter;
	
	/**
	 * Article Dao.
	 */
	protected ArticleDao articleDao;

	/**
	 * Maximum summary length.
	 */
	protected int maximumSummaryLength = DEFAULT_MAX_SUMMARY_LENGTH;
	
	/**
	 * Sets the ContentFilter implementation to use.
	 * @param contentFilter content filter
	 */
	@Required
	public void setContentFilter(ContentFilter contentFilter)
	{
		this.contentFilter = contentFilter;
	}
	
	/**
	 * Sets the ArticleDao implementation to use
	 * 
	 * @param articleDao Article data access object
	 */
	@Required
	public void setArticleDao(ArticleDao articleDao)
	{
		this.articleDao = articleDao;
	}

	/**
	 * Sets the maximum length of the summary field.
	 * @param maximumSummaryLength max summary length
	 */
	public void setMaximumSummaryLength(int maximumSummaryLength)
	{
		this.maximumSummaryLength = maximumSummaryLength;
	}
	
	/**
	 * Determines if this validator supports the given class.
	 * 
	 * <p> This validator supports {@code ArticleAddCommand} only. </p>
	 * 
	 * @param givenClass class to check
	 */
	@Override
	public boolean supports(Class givenClass)
	{
		return ArticleAddCommand.class.equals(givenClass);
	}

	/**
	 * Validates the given command.
	 * 
	 * @param obj {@code ArticleAddCommand} to validate
	 * @param errors Spring Errors object to populate
	 */
	@Override
	public void validate(Object obj, Errors errors)
	{
		ArticleAddCommand command = (ArticleAddCommand) obj;

		if (!validateCommon(command, errors))
			return;

		if (errors.getFieldErrorCount("permalink") == 0)
		{
			String permalink = command.getPermalink();
			if (permalink != null)
			{
				// look for article with the same permalink
				Article prev = articleDao.findByPermalink(permalink);
				if (prev != null)
				{
					errors.rejectValue("permalink", ERROR_ARTICLE_PERMALINK_EXISTS, "permalink exists");
				}
			}
		}
	}

	/**
	 * Validate fields common to this class and subclasses.
	 * 
	 * @param command article add command (or subclass)
	 * @param errors spring errors object to populate
	 * @return true if validation completed, false if processing should stop
	 */
	protected final boolean validateCommon(ArticleAddCommand command, Errors errors)
	{

		if (command == null)
		{
			errors.reject(ERROR_ARTICLE_NULL, "Null data received");
			// do not continue processing, as this will lead to NPEs later
			return false;
		}

		String title = command.getTitle();
		if (title == null || title.trim().length() == 0)
		{
			errors.rejectValue("title", ERROR_ARTICLE_TITLE_REQUIRED, "title required");
		}

		String permalink = command.getPermalink();
		if (permalink != null && !permalink.matches("[a-z0-9\\-]+"))
		{
			errors.rejectValue("permalink", ERROR_ARTICLE_PERMALINK_INVALID, "permalink invalid");
		}

		ContentType contentType = command.getContentType();
		if (contentType == null)
		{
			errors.rejectValue("contentType", ERROR_ARTICLE_CONTENT_TYPE_REQUIRED, "content type required");
		}

		String content = command.getContent();
		if (content == null || content.trim().length() == 0)
		{
			errors.rejectValue("content", ERROR_ARTICLE_CONTENT_REQUIRED, "content required");
		}
		else if (contentType != null)
		{
			// validate the content
			validateContent(errors, content, contentType.getMimeType(), "content", ERROR_ARTICLE_CONTENT_INVALID, "content invalid"); 
		}

		String summary = command.getSummary();
		if (summary != null)
		{
			// validate summary
			if (summary.length() > maximumSummaryLength)
			{
				errors.rejectValue("summary", ERROR_ARTICLE_SUMMARY_TOO_LONG, new Object[] { new Integer(maximumSummaryLength) }, "summary too long");
			}
			else
			{				
				validateContent(errors, summary, contentType.getMimeType(), "summary", ERROR_ARTICLE_SUMMARY_INVALID, "summary invalid"); 
			}
		}
		
		return true;
	}

	private void validateContent(Errors errors, String content, String mimeType, String fieldName, String errorMessage, String fallbackMessage)	
	{
		String prefix = contentFilter.getPrefix(mimeType);
		String suffix = contentFilter.getSuffix(mimeType);
		
		List<Reader> readers = new ArrayList<Reader>();
		if (prefix != null) readers.add(new StringReader(prefix));
		readers.add(new StringReader(content));
		if (suffix != null) readers.add(new StringReader(suffix));
		
		SequenceReader reader = new SequenceReader(readers);
		
		try
		{
			contentFilter.validate(mimeType, reader);
		}
		catch (InvalidContentException e)
		{
			int line = e.getLineNumber();
			int col = e.getColumnNumber();
			
			errors.rejectValue(fieldName, errorMessage, new Object[] { new Integer(line), new Integer(col), e.getMessage() }, fallbackMessage);
		}
		catch (InvalidContentTypeException e)
		{
			logger.error("Caught exception", e);
			throw new RuntimeException("Invalid content type", e);
		}
		catch (IOException e)
		{
			// this shouldn't happen, since all readers are string readers
			logger.error("Caught exception", e);
			throw new RuntimeException("I/O error", e);
		}
	}
}
