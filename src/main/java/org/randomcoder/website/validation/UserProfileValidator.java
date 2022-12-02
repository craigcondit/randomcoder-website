package org.randomcoder.website.validation;

import org.randomcoder.website.command.UserProfileCommand;

public class UserProfileValidator {

    private static final String ERROR_PROFILE_EMAIL_ADDRESS_REQUIRED = "error.profile.emailaddress.required";
    private static final String ERROR_PROFILE_EMAIL_ADDRESS_INVALID = "error.profile.emailaddress.invalid";
    private static final String ERROR_WEBSITE_INVALID = "error.profile.website.invalid";

    public void validate(ValidatorContext context, UserProfileCommand command) {
        String emailAddress = command.getEmailAddress();
        if (emailAddress == null) {
            context.reject("emailAddress", ERROR_PROFILE_EMAIL_ADDRESS_REQUIRED);
        } else if (!DataValidationUtils.isValidEmailAddress(emailAddress)) {
            context.reject("emailAddress", ERROR_PROFILE_EMAIL_ADDRESS_INVALID);
        }

        String website = command.getWebsite();
        if (website != null) {
            if (website.length() > 255 || !DataValidationUtils.isValidUrl(website)) {
                context.reject("website", ERROR_WEBSITE_INVALID);
            }
        }
    }

}