package org.randomcoder.security.spring;

import org.randomcoder.db.Role;
import org.randomcoder.db.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Randomcoder UserDetails implementation.
 */
public final class RandomcoderUserDetails implements UserDetails {
  private static final long serialVersionUID = 8725581950129430004L;

  private final String username;
  private String password;
  private final boolean enabled;
  private final List<GrantedAuthority> authorities;

  /**
   * Creates a new RandomcoderUserDetails.
   *
   * @param user User to read properties from.
   */
  public RandomcoderUserDetails(User user) {
    this(user, user.getPassword());
  }

  /**
   * Creates a new RandomcoderUserDetails with an explicit password.
   *
   * <p>
   * This is most often used for specifying non-password tokens.
   * </p>
   *
   * @param user     User to read properties from.
   * @param password overriden password
   */
  public RandomcoderUserDetails(User user, String password) {
    username = user.getUserName();
    this.password = password;
    enabled = user.isEnabled();

    List<GrantedAuthority> auth = new ArrayList<>();
    for (Role role : user.getRoles()) {
      auth.add(new SimpleGrantedAuthority(role.getName()));
    }
    authorities = Collections.unmodifiableList(auth);
  }

  /**
   * Returns the authorities granted to the user. Cannot return
   * <code>null</code>.
   *
   * @return the authorities, sorted by natural key (never <code>null</code>)
   */
  @Override public List<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  /**
   * Gets the password for this user.
   *
   * @return password (never null)
   */
  @Override public String getPassword() {
    return password;
  }

  /**
   * Gets the username for this user.
   *
   * @return user name (never null)
   */
  @Override public String getUsername() {
    return username;
  }

  /**
   * Always returns true because randomcoder.org users do not expire.
   *
   * @return always true
   */
  @Override public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Always returns true because randomcoder.org users are not locked.
   *
   * @return always true
   */
  @Override public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Always returns true because randomcoder.org credentials do not expire.
   *
   * @return always true
   */
  @Override public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Determines if the current user is enabled.
   *
   * @return true if enabled, false otherwise
   */
  @Override public boolean isEnabled() {
    return enabled;
  }

}
