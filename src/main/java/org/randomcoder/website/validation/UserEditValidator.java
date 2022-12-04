package org.randomcoder.website.validation;

import org.randomcoder.website.command.UserEditCommand;

public class UserEditValidator extends UserAddValidator {

    private static final String ERROR_USER_ID_REQUIRED = "error.user.id.required";

    public void validate(ValidatorContext context, UserEditCommand command) {
        validateCommon(context, command);

        Long id = command.getId();
        if (id == null) {
            context.reject("id", ERROR_USER_ID_REQUIRED);
        }
    }

}
