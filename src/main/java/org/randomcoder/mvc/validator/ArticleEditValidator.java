package org.randomcoder.mvc.validator;

import org.randomcoder.db.Article;
import org.randomcoder.mvc.command.*;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/**
 * Validator for editing articles.
 */
@Component("articleEditValidator")
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
	public boolean supports(Class<?> givenClass)
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
				Article prev = articleBusiness.findArticleByPermalink(permalink);
				
				if (prev != null && !prev.getId().equals(id))
				{
					errors.rejectValue("permalink", ERROR_ARTICLE_PERMALINK_EXISTS, "permalink exists");
				}
			}
		}
	}
}
