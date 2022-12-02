package org.randomcoder.website.validation;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.randomcoder.website.Config;
import org.randomcoder.website.command.ChangePasswordCommand;
import org.randomcoder.website.data.User;

public class ChangePasswordValidator {

    private static final int DEFAULT_MINIMUM_PASSWORD_LENGTH = 6;

    private static final String ERROR_OLD_PASSWORD_REQUIRED = "error.changepassword.oldpassword.required";
    private static final String ERROR_OLD_PASSWORD_NO_MATCH = "error.changepassword.oldpassword.nomatch";

    private static final String ERROR_PASSWORD_REQUIRED = "error.changepassword.password.required";
    private static final String ERROR_PASSWORD_TOO_SHORT = "error.changepassword.password.tooshort";
    private static final String ERROR_PASSWORD_NO_MATCH = "error.changepassword.password.nomatch";

    @Inject
    @Named(Config.PASSWORD_LENGTH_MINIMUM)
    Integer minimumPasswordLength = DEFAULT_MINIMUM_PASSWORD_LENGTH;

    public void validate(ValidatorContext context, ChangePasswordCommand command) {
        User user = command.getUser();
        String oldPassword = command.getOldPassword();
        String password = command.getPassword();
        String password2 = command.getPassword2();

        if (user.getPassword() == null) {
            // no password, so user must not enter one
            if (oldPassword != null && oldPassword.length() > 0)
                context.reject("oldPassword", ERROR_OLD_PASSWORD_NO_MATCH);
        } else {
            // validate old password
            if (oldPassword == null || oldPassword.trim().length() == 0) {
                context.reject("oldPassword", ERROR_OLD_PASSWORD_REQUIRED);
            } else {
                String oldHash = User.hashPassword(oldPassword);
                String userHash = user.getPassword();
                if (!oldHash.equals(userHash)) {
                    context.reject("oldPassword", ERROR_OLD_PASSWORD_NO_MATCH);
                }
            }
        }

        // validate new password
        if (password == null || password.trim().length() == 0) {
            context.reject("password", ERROR_PASSWORD_REQUIRED);
        } else if (password.length() < minimumPasswordLength) {
            context.reject("password", ERROR_PASSWORD_TOO_SHORT, minimumPasswordLength);
        }

        // validate new password match
        if (password2 == null || password2.trim().length() == 0) {
            context.reject("password2", ERROR_PASSWORD_REQUIRED);
        } else if (password != null && !password.equals(password2)) {
            context.reject("password2", ERROR_PASSWORD_NO_MATCH);
        }
    }

}
