package com.randomcoder.article.comment;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.*;

import com.randomcoder.content.*;
import com.randomcoder.io.SequenceReader;
import com.randomcoder.validation.DataValidationUtils;

/**
 * Validator for posting comments.
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
public class CommentValidator implements Validator
{
	private static final String ERROR_COMMENT_NULL = "error.comment.null";
	private static final String ERROR_COMMENT_USER_NAME_FORBIDDEN = "error.comment.username.forbidden";
	private static final String ERROR_COMMENT_USER_NAME_REQUIRED = "error.comment.username.required";
	private static final String ERROR_COMMENT_EMAIL_ADDRESS_FORBIDDEN = "error.comment.emailaddress.forbidden";
	private static final String ERROR_COMMENT_EMAIL_ADDRESS_REQUIRED = "error.comment.emailaddress.required";
	private static final String ERROR_COMMENT_EMAIL_ADDRESS_INVALID = "error.comment.emailaddress.invalid";
	private static final String ERROR_COMMENT_WEBSITE_FORBIDDEN = "error.comment.website.forbidden";
	private static final String ERROR_COMMENT_WEBSITE_INVALID = "error.comment.website.invalid";
	private static final String ERROR_COMMENT_TITLE_REQUIRED = "error.comment.title.required";
	private static final String ERROR_COMMENT_CONTENT_REQUIRED = "error.comment.content.required";	
	private static final String ERROR_COMMENT_CONTENT_INVALID = "error.comment.content.invalid";	
	
	private static final Log logger = LogFactory.getLog(CommentValidator.class);
	
	/**
	 * Message resource for permalink exists message.
	 */
	protected static final String ERROR_ARTICLE_PERMALINK_EXISTS = "error.article.permalink.exists";

	private ContentFilter contentFilter;
	
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
	 * Determines if this validator supports the given class.
	 * <p>
	 * This class supports {@code CommentCommand} objects only.
	 * </p>
	 * @return true if supported, false otherwise
	 */
	@Override
	public boolean supports(Class givenClass)
	{
		return CommentCommand.class.equals(givenClass);
	}

	/**
	 * Validates the given command.
	 * 
	 * @param obj {@code CommentCommand} to validate
	 * @param errors Spring Errors object to populate
	 */
	@Override
	public void validate(Object obj, Errors errors)
	{
		CommentCommand command = (CommentCommand) obj;

		if (command == null)
		{
			errors.reject(ERROR_COMMENT_NULL, "Null data received");
			// do not continue processing, as this will lead to NPEs later
			return;
		}
		
		if (command.isAnonymous())
		{
			// validate anonymous input fields
			
			String username = command.getAnonymousUserName();
			if (username == null)
			{
				errors.rejectValue("anonymousUserName", ERROR_COMMENT_USER_NAME_REQUIRED, "user name required");
			}
			
			String emailAddress = command.getAnonymousEmailAddress();
			if (emailAddress == null)
			{
				errors.rejectValue("anonymousEmailAddress", ERROR_COMMENT_EMAIL_ADDRESS_REQUIRED, "email address required");
			}
			else if (!DataValidationUtils.isValidEmailAddress(emailAddress))
			{
				errors.rejectValue("anonymousEmailAddress", ERROR_COMMENT_EMAIL_ADDRESS_INVALID, "email address invalid");
			}
			
			String website = command.getAnonymousWebsite();
			if (website != null && !DataValidationUtils.isValidUrl(website))
			{
				errors.rejectValue("anonymousWebsite", ERROR_COMMENT_WEBSITE_INVALID, "web site invalid");				
			}
		}
		else
		{
			// make sure they aren't specified
			
			if (command.getAnonymousUserName() != null)
			{
				errors.reject(ERROR_COMMENT_USER_NAME_FORBIDDEN, "user name forbidden");
			}
			if (command.getAnonymousEmailAddress() != null)
			{
				errors.reject(ERROR_COMMENT_EMAIL_ADDRESS_FORBIDDEN, "email address forbidden");
			}
			if (command.getAnonymousWebsite() != null)
			{
				errors.reject(ERROR_COMMENT_WEBSITE_FORBIDDEN, "website forbidden");
			}
		}
		
		String title = command.getTitle();
		if (title == null || title.trim().length() == 0)
		{
			errors.rejectValue("title", ERROR_COMMENT_TITLE_REQUIRED, "title required");
		}
				
		// TODO implement support for other content types
		ContentType contentType = ContentType.TEXT;
		
		String content = command.getContent();
		if (content == null || content.trim().length() == 0)
		{
			errors.rejectValue("content", ERROR_COMMENT_CONTENT_REQUIRED, "content required");
		}
		else if (contentType != null)
		{			
			// validate the content
			
			String mimeType = contentType.getMimeType();
			
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
				
				errors.rejectValue("content", ERROR_COMMENT_CONTENT_INVALID, new Object[] { new Integer(line), new Integer(col), e.getMessage() }, "content invalid");
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
}
