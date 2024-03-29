package org.randomcoder.website.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.website.data.User;

import static org.junit.Assert.assertEquals;

public class UserProfileCommandTest {
    private UserProfileCommand command;

    @Before
    public void setUp() {
        command = new UserProfileCommand();
    }

    @After
    public void tearDown() {
        command = null;
    }

    @Test
    public void testGetEmailAddress() {
        command.setEmailAddress("test@example.com");
        assertEquals("test@example.com", command.getEmailAddress());
    }

    @Test
    public void testGetWebsite() {
        command.setWebsite("http://test.com/");
        assertEquals("http://test.com/", command.getWebsite());
    }

    @Test
    public void testAccept() {
        command.setEmailAddress("test@example.com");
        command.setWebsite("http://test.com/");

        User user = new User();
        command.accept(user);

        assertEquals("Wrong website", "http://test.com/", user.getWebsite());
        assertEquals("Wrong email address", "test@example.com", user.getEmailAddress());
    }

}