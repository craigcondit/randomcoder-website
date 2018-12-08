package org.randomcoder.mvc.command;

import org.randomcoder.db.User;

import java.io.Serializable;

/**
 * Command used to change a user's password.
 */
public class ChangePasswordCommand implements Serializable {
  private static final long serialVersionUID = -6349438305307982312L;

  private String oldPassword;
  private String password;
  private String password2;
  private User user;

  /**
   * Gets the value of the old password.
   *
   * @return old password
   */
  public String getOldPassword() {
    return oldPassword;
  }

  /**
   * Sets the value of the old password.
   *
   * @param oldPassword old password
   */
  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  /**
   * Gets the new password.
   *
   * @return new password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the new password.
   *
   * @param password new password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets the new password again for verification.
   *
   * @return new password
   */
  public String getPassword2() {
    return password2;
  }

  /**
   * Sets the new password again for verification.
   *
   * @param password2 new password
   */
  public void setPassword2(String password2) {
    this.password2 = password2;
  }

  /**
   * Gets the user associated with this request.
   *
   * @return user
   */
  public User getUser() {
    return user;
  }

  /**
   * Sets the user associated with this request.
   *
   * @param user user
   */
  public void setUser(User user) {
    this.user = user;
  }

}
