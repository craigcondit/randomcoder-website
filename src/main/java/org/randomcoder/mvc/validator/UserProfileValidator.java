package org.randomcoder.mvc.validator;

import org.randomcoder.mvc.command.UserProfileCommand;
import org.randomcoder.validation.DataValidationUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.*;

/**
 * Validator for UserProfileCommand objects.
 */
@Component("userProfileValidator")
public class UserProfileValidator implements Validator {
  private static final String ERROR_PROFILE_NULL = "error.profile.null";
  private static final String ERROR_PROFILE_EMAIL_ADDRESS_REQUIRED = "error.profile.emailaddress.required";
  private static final String ERROR_PROFILE_EMAIL_ADDRESS_INVALID = "error.profile.emailaddress.invalid";
  private static final String ERROR_WEBSITE_INVALID = "error.profile.website.invalid";

  /**
   * Determines if the target class is supported by this validator.
   * 
   * @param targetClass
   *          target class
   * @return true if target class is UserProfileCommand, false otherwise
   */
  @Override
  public boolean supports(Class<?> targetClass) {
    return UserProfileCommand.class.equals(targetClass);
  }

  /**
   * Validates the given object.
   * 
   * @param target
   *          target object to validate
   * @param errors
   *          errors object to populate
   */
  @Override
  public void validate(Object target, Errors errors) {
    UserProfileCommand command = (UserProfileCommand) target;

    if (command == null) {
      errors.reject(ERROR_PROFILE_NULL, "Null data received");
      // do not continue processing, as this will lead to NPEs later
      return;
    }

    String emailAddress = command.getEmailAddress();
    if (emailAddress == null) {
      errors.rejectValue("emailAddress", ERROR_PROFILE_EMAIL_ADDRESS_REQUIRED, "email address required");
    } else if (!DataValidationUtils.isValidEmailAddress(emailAddress)) {
      errors.rejectValue("emailAddress", ERROR_PROFILE_EMAIL_ADDRESS_INVALID, "email address invalid");
    }

    String website = command.getWebsite();
    if (website != null) {
      if (website.length() > 255 || !DataValidationUtils.isValidUrl(website)) {
        errors.rejectValue("website", ERROR_WEBSITE_INVALID, "Website invalid.");
      }
    }
  }
}