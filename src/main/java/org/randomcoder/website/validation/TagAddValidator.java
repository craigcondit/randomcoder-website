package org.randomcoder.website.validation;

import jakarta.inject.Inject;
import org.randomcoder.website.bo.TagBusiness;
import org.randomcoder.website.command.TagAddCommand;

public class TagAddValidator {

    private static final String ERROR_NAME_REQUIRED = "error.tag.name.required";
    private static final String ERROR_NAME_EXISTS = "error.tag.name.exists";
    private static final String ERROR_DISPLAY_NAME_REQUIRED = "error.tag.displayname.required";

    @Inject
    TagBusiness tagBusiness;

    public void validate(ValidatorContext context, TagAddCommand command) {
        validateCommon(context, command);

        String name = command.getName();
        if (name == null) {
            context.reject("name", ERROR_NAME_REQUIRED);
        } else if (tagBusiness.findTagByName(name) != null) {
            context.reject("name", ERROR_NAME_EXISTS);
        }
    }

    protected void validateCommon(ValidatorContext context, TagAddCommand command) {
        String displayName = command.getDisplayName();
        if (displayName == null) {
            context.reject("displayName", ERROR_DISPLAY_NAME_REQUIRED);
        }
    }

}
