package org.randomcoder.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class UserTest {
  private User user;

  @Before public void setUp() {
    user = new User();
    user.setPassword(User.hashPassword("Password1"));
    user.setWebsite("http://localhost/");
    user.setEmailAddress("test@example.com");
    user.setUserName("test");
  }

  @After public void tearDown() {
    user = null;
  }

  @Test public void testToString() {
    String value = user.toString();
    assertFalse("User has password", value.contains("password"));
    assertFalse("User has email address", value.contains("emailAddress"));
    assertFalse("User has website", value.contains("website"));
    assertFalse("User does not have username",
        value.contains("userName='test'"));
  }
}
