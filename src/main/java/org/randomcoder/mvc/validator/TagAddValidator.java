package org.randomcoder.mvc.validator;

import jakarta.inject.Inject;
import org.randomcoder.bo.TagBusiness;
import org.randomcoder.mvc.command.TagAddCommand;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator used for adding tags.
 */
@Component("tagAddValidator")
public class TagAddValidator
        implements Validator {
    private static final String ERROR_TAG_NULL = "error.tag.null";
    private static final String ERROR_NAME_REQUIRED = "error.tag.name.required";
    private static final String ERROR_NAME_EXISTS = "error.tag.name.exists";
    private static final String ERROR_DISPLAY_NAME_REQUIRED =
            "error.tag.displayname.required";

    private TagBusiness tagBusiness;

    /**
     * Sets the TagBusiness implementation to use.
     *
     * @param tagBusiness TagBusiness implementation
     */
    @Inject
    public void setTagBusiness(TagBusiness tagBusiness) {
        this.tagBusiness = tagBusiness;
    }

    /**
     * Determines if this validator supports the given class.
     *
     * @param targetClass class to check
     * @return true if class is {@code TagAddCommand}, false otherwise
     */
    @Override
    public boolean supports(Class<?> targetClass) {
        return TagAddCommand.class.equals(targetClass);
    }

    /**
     * Validates the given object.
     *
     * @param target object to validate
     * @param errors errors object to hold resulting validation errors
     */
    @Override
    public void validate(Object target, Errors errors) {
        TagAddCommand command = (TagAddCommand) target;

        if (!validateCommon(command, errors)) {
            return;
        }

        // tag name
        String name = command.getName();
        if (name == null) {
            errors.rejectValue("name", ERROR_NAME_REQUIRED, "Name required.");
        } else if (tagBusiness.findTagByName(name) != null) {
            errors.rejectValue("name", ERROR_NAME_EXISTS, "Name exists.");
        }
    }

    /**
     * Validate errors common to this class and subclasses.
     *
     * @param command command to validate
     * @param errors  errors
     * @return true if validation should continue, false otherwise
     */
    protected boolean validateCommon(TagAddCommand command, Errors errors) {
        if (command == null) {
            errors.reject(ERROR_TAG_NULL, "Null data received");
            // do not continue processing, as this will lead to NPEs later
            return false;
        }

        // display name
        String displayName = command.getDisplayName();
        if (displayName == null) {
            errors.rejectValue("displayName", ERROR_DISPLAY_NAME_REQUIRED,
                    "Display name required.");
        }

        return true;
    }
}
