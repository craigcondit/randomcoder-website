package org.randomcoder.website.validation;

import org.randomcoder.website.command.TagEditCommand;

public class TagEditValidator extends TagAddValidator {

    private static final String ERROR_TAG_ID_REQUIRED = "error.tag.id.required";

    public void validate(ValidatorContext context, TagEditCommand command) {
        validateCommon(context, command);

        Long id = command.getId();
        if (id == null) {
            context.reject("id", ERROR_TAG_ID_REQUIRED);
        }
    }

}
