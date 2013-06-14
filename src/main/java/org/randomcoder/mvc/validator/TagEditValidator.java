package org.randomcoder.mvc.validator;

import org.randomcoder.mvc.command.TagEditCommand;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/**
 * Validator used for editing tags.
 */
@Component("tagEditValidator")
public class TagEditValidator extends TagAddValidator
{
	private static final String ERROR_TAG_ID_REQUIRED = "error.tag.id.required";

	/**
	 * Determines if this validator supports the given class.
	 * 
	 * @param targetClass
	 *            class to check
	 * @return true if targetClass is {@code TagEditCommand}, false otherwise
	 */
	@Override
	public boolean supports(Class<?> targetClass)
	{
		return TagEditCommand.class.equals(targetClass);
	}

	/**
	 * Validates the given object.
	 * 
	 * @param target
	 *            object to validate
	 * @param errors
	 *            error object to hold validation errors
	 */
	@Override
	public void validate(Object target, Errors errors)
	{
		TagEditCommand command = (TagEditCommand) target;

		if (!validateCommon(command, errors))
			return;

		Long id = command.getId();

		if (id == null)
		{
			errors.rejectValue("id", ERROR_TAG_ID_REQUIRED, "id required");
		}
	}

}
