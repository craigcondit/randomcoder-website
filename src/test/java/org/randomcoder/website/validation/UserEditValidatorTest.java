package org.randomcoder.website.validation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.website.command.UserEditCommand;
import org.randomcoder.website.data.Role;
import org.randomcoder.website.data.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class UserEditValidatorTest {
    private UserEditValidator validator;

    @Before
    public void setUp() {
        validator = new UserEditValidator();
        validator.minimumPasswordLength = 6;
        validator.minimumUsernameLength = 6;
    }

    @After
    public void tearDown() {
        validator = null;
    }

    @Test
    public void testValidate() {
        ValidatorContext context;
        String err;

        // setup
        UserEditCommand command = new UserEditCommand();

        User user = new User();
        user.setUserName("existing-user");
        user.setEmailAddress("existing@example.com");
        user.setPassword(User.hashPassword("Password1"));
        user.setRoles(new ArrayList<Role>());
        user.setEnabled(true);
        user.setId(1L);

        command.setId(1L);
        command.load(user);

        // no data supplied
        command.setId(null);
        command.setEmailAddress(null);
        command.setPassword(null);
        command.setPassword2(null);
        command.setRoles(List.of());
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong number of errors occurred", 2, context.getErrors().size());
        assertEquals("Wrong error count for emailAddress", 1, context.getErrors().get("emailAddress").size());

        command.setId(1L);

        // email address invalid
        command.setEmailAddress("bogus email address");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong error count for emailAddress", 1, context.getErrors().get("emailAddress").size());
        err = context.getErrors().get("emailAddress").get(0);
        assertEquals("Wrong error", "Email address is invalid.", err);

        // email address valid
        command.setEmailAddress("valid@example.com");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertFalse("Errors exist for emailAddress", context.getErrors().containsKey("emailAddress"));

        // password too short
        command.setPassword("short");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong error count for password", 1, context.getErrors().get("password").size());
        err = context.getErrors().get("password").get(0);
        assertEquals("Wrong error", "Password must be at least 6 characters long.", err);

        // password valid
        command.setPassword("Password1");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertFalse("Errors exist for password", context.getErrors().containsKey("password"));

        // password 2 doesn't match
        command.setPassword2("Password2");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong error count for password2", 1, context.getErrors().get("password2").size());
        err = context.getErrors().get("password2").get(0);
        assertEquals("Wrong error code", "Passwords do not match.", err);

        // all data valid
        command.setPassword("Password2");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Errors occurred", 0, context.getErrors().size());
    }

}