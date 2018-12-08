package org.randomcoder.mvc.editor;

import org.randomcoder.bo.UserBusiness;
import org.randomcoder.db.Role;

import java.beans.PropertyEditorSupport;

/**
 * Property editor for roles.
 */
public class RolePropertyEditor extends PropertyEditorSupport {
  private final UserBusiness userBusiness;

  /**
   * Creates a new property editor for Role objects.
   *
   * @param userBusiness UserBusiness implementation to use
   */
  public RolePropertyEditor(UserBusiness userBusiness) {
    this.userBusiness = userBusiness;
  }

  /**
   * Gets the value of the associated object as a text string.
   *
   * @return text representation of object
   */
  @Override public String getAsText() {
    Role role = (Role) getValue();
    String result = (role == null) ? "" : role.getName();
    return result;
  }

  /**
   * Sets the value of the associated object as a text string.
   *
   * @param string text value
   * @throws IllegalArgumentException if parsing fails
   */
  @Override public void setAsText(String string)
      throws IllegalArgumentException {
    Role role = userBusiness.findRoleByName(string);
    if (role == null) {
      throw new IllegalArgumentException("No such role: " + string);
    }
    setValue(role);
  }
}