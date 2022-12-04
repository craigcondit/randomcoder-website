package org.randomcoder.website.validation;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.randomcoder.website.Config;
import org.randomcoder.website.bo.UserBusiness;
import org.randomcoder.website.command.AccountCreateCommand;

public class AccountCreateValidator {
    private static final int DEFAULT_MINIMUM_USERNAME_LENGTH = 3;
    private static final int DEFAULT_MINIMUM_PASSWORD_LENGTH = 6;

    private static final String ERROR_USERNAME_REQUIRED = "error.user.username.required";
    private static final String ERROR_USERNAME_TOO_SHORT = "error.user.username.tooshort";
    private static final String ERROR_USERNAME_EXISTS = "error.user.username.exists";
    private static final String ERROR_EMAIL_ADDRESS_REQUIRED = "error.user.emailaddress.required";
    private static final String ERROR_EMAIL_ADDRESS_INVALID = "error.user.emailaddress.invalid";
    private static final String ERROR_WEBSITE_INVALID = "error.user.website.invalid";
    private static final String ERROR_PASSWORD_REQUIRED = "error.user.password.required";
    private static final String ERROR_PASSWORD_TOO_SHORT = "error.user.password.tooshort";
    private static final String ERROR_PASSWORD_NO_MATCH = "error.user.password.nomatch";

    @Inject
    @Named(Config.PASSWORD_LENGTH_MINIMUM)
    int minimumPasswordLength = DEFAULT_MINIMUM_PASSWORD_LENGTH;

    @Inject
    @Named(Config.USERNAME_LENGTH_MINIMUM)
    int minimumUsernameLength = DEFAULT_MINIMUM_USERNAME_LENGTH;

    @Inject
    UserBusiness userBusiness;

    public void validate(ValidatorContext context, AccountCreateCommand command) {
        validateUsername(context, command);
        validatePassword(context, command);
        validateEmailAddress(context, command);
        validateWebsite(context, command);
    }

    private void validateWebsite(ValidatorContext context, AccountCreateCommand command) {
        String website = command.getWebsite();
        if (website != null) {
            if (website.length() > 255 || !DataValidationUtils.isValidUrl(website)) {
                context.reject("website", ERROR_WEBSITE_INVALID);
            }
        }
    }

    private void validateEmailAddress(ValidatorContext context, AccountCreateCommand command) {
        String emailAddress = command.getEmailAddress();
        if (emailAddress == null) {
            context.reject("emailAddress", ERROR_EMAIL_ADDRESS_REQUIRED);
        } else if (!DataValidationUtils.isValidEmailAddress(emailAddress)) {
            context.reject("emailAddress", ERROR_EMAIL_ADDRESS_INVALID);
        }
    }

    private void validatePassword(ValidatorContext context, AccountCreateCommand command) {
        String password = StringUtils.defaultIfEmpty(command.getPassword(), "");
        String password2 = StringUtils.defaultIfEmpty(command.getPassword2(), "");

        if (password.trim().length() == 0) {
            context.reject("password", ERROR_PASSWORD_REQUIRED);
        } else if (password.trim().length() < minimumPasswordLength) {
            context.reject("password", ERROR_PASSWORD_TOO_SHORT, minimumPasswordLength);
        }

        // compare passwords if at least one is specified
        if (password.length() > 0 || password2.length() > 0) {
            if (!password.equals(password2)) {
                context.reject("password2", ERROR_PASSWORD_NO_MATCH);
            }
        }

        // password2, but only if no other errors
        if (!context.getErrors().containsKey("password2")) {
            if (password2 == null || password2.trim().length() == 0) {
                context.reject("password2", ERROR_PASSWORD_REQUIRED);
            }
        }
    }

    private void validateUsername(ValidatorContext context, AccountCreateCommand command) {
        String userName = command.getUserName();
        if (userName == null) {
            context.reject("userName", ERROR_USERNAME_REQUIRED);
        } else if (userName.length() < minimumUsernameLength) {
            context.reject("userName", ERROR_USERNAME_TOO_SHORT, minimumUsernameLength);
        } else if (userBusiness.findUserByName(userName) != null) {
            context.reject("userName", ERROR_USERNAME_EXISTS);
        }
    }

}