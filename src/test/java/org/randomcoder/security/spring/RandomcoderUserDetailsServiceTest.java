package org.randomcoder.security.spring;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.bo.UserBusiness;
import org.randomcoder.db.Role;
import org.randomcoder.db.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RandomcoderUserDetailsServiceTest {
  private RandomcoderUserDetailsService svc = null;

  private IMocksControl control;
  private UserBusiness ub;

  @Before public void setUp() {
    control = createControl();
    ub = control.createMock(UserBusiness.class);
    svc = new RandomcoderUserDetailsService();
    svc.setUserBusiness(ub);
  }

  @After public void tearDown() {
    control = null;
    ub = null;
    svc = null;
  }

  private Role createTestRole() {
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_TEST");
    role.setDescription("Test role");
    return role;
  }

  private User createTestUser() {
    List<Role> roles = new ArrayList<>();
    roles.add(createTestRole());

    User user = new User();
    user.setId(1L);
    user.setUserName("test");
    user.setEnabled(true);
    user.setPassword(User.hashPassword("Password1"));
    user.setEmailAddress("test@example.com");
    user.setRoles(roles);
    return user;
  }

  @Test public void testLoadUserByUsername() {
    expect(ub.findUserByName("test")).andReturn(createTestUser());
    control.replay();

    UserDetails details = svc.loadUserByUsername("test");
    assertNotNull(details);
    assertEquals("test", details.getUsername());

    assertEquals(User.hashPassword("Password1"), details.getPassword());

    List<GrantedAuthority> authorities =
        new ArrayList<GrantedAuthority>(details.getAuthorities());
    assertNotNull(authorities);
    assertEquals(1, authorities.size());
    assertEquals("ROLE_TEST", authorities.get(0).getAuthority());

    assertTrue(details.isAccountNonExpired());
    assertTrue(details.isAccountNonLocked());
    assertTrue(details.isCredentialsNonExpired());
    assertTrue(details.isEnabled());

    control.verify();
  }

  @Test(expected = UsernameNotFoundException.class)
  public void testLoadUserByUsernameNotFound() throws Exception {
    svc.loadUserByUsername("bogus");
  }

  @Test(expected = UsernameNotFoundException.class)
  public void testLoadUserByUsernameNoPassword() throws Exception {
    svc.loadUserByUsername("test-no-password");
  }
}