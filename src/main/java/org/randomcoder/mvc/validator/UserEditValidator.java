package org.randomcoder.mvc.validator;

import org.randomcoder.mvc.command.UserEditCommand;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/**
 * Validator used for editing users.
 */
@Component("userEditValidator") public class UserEditValidator
    extends UserAddValidator {
  private static final String ERROR_USER_ID_REQUIRED = "error.user.id.required";

  /**
   * Determines if this validator supports the given class.
   *
   * @param targetClass class to check
   * @return true if targetClass is {@code UserEditCommand}, false otherwise
   */
  @Override public boolean supports(Class<?> targetClass) {
    return UserEditCommand.class.equals(targetClass);
  }

  /**
   * Validates the given object.
   *
   * @param target object to validate
   * @param errors error object to populate with validation errors
   */
  @Override public void validate(Object target, Errors errors) {
    UserEditCommand command = (UserEditCommand) target;

    if (!validateCommon(command, errors)) {
      return;
    }

    Long id = command.getId();

    if (id == null) {
      errors.rejectValue("id", ERROR_USER_ID_REQUIRED, "id required");
    }
  }
}
