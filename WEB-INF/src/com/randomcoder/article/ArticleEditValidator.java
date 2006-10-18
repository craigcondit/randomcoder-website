package com.randomcoder.article;

import org.springframework.validation.Errors;

import com.randomcoder.bean.Article;

/**
 * Validator for editing articles.
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
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
public class ArticleEditValidator extends ArticleAddValidator
{
	private static final String ERROR_ARTICLE_ID_REQUIRED = "error.article.id.required";

	/**
	 * Determines if this validator supports the given class.
	 * 
	 * <p> This validator supports {@code ArticleEditCommand} only. </p>
	 * 
	 * @param givenClass class to check
	 */
	@Override
	public boolean supports(Class givenClass)
	{
		return ArticleEditCommand.class.equals(givenClass);
	}

	/**
	 * Validates the given command.
	 * 
	 * <p> This method delegates to
	 * {@link ArticleAddValidator#validateCommon(ArticleAddCommand, Errors)} for
	 * processing common to both classes. </p>
	 * 
	 * @param obj {@code ArticleEditCommand} to validate
	 * @param errors Spring Errors object to populate
	 * @see ArticleAddValidator#validate(Object, Errors)
	 */
	@Override
	public void validate(Object obj, Errors errors)
	{
		ArticleEditCommand command = (ArticleEditCommand) obj;

		if (!validateCommon(command, errors))
			return;

		Long id = command.getId();

		if (id == null)
		{
			errors.rejectValue("id", ERROR_ARTICLE_ID_REQUIRED, "id required");
		}

		if (errors.getFieldErrorCount("permalink") == 0)
		{
			String permalink = command.getPermalink();
			if (permalink != null)
			{
				// look for article with the same permalink
				Article prev = articleDao.findByPermalink(permalink);
				
				if (prev != null && !prev.getId().equals(id))
				{
					errors.rejectValue("permalink", ERROR_ARTICLE_PERMALINK_EXISTS, "permalink exists");
				}
			}
		}
	}
}
