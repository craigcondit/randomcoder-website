package org.randomcoder.website.validation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.website.command.ChangePasswordCommand;
import org.randomcoder.website.data.User;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ChangePasswordValidatorTest {
    private ChangePasswordValidator validator;

    @Before
    public void setUp() {
        validator = new ChangePasswordValidator();
        validator.minimumPasswordLength = 6;
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
        ChangePasswordCommand command = new ChangePasswordCommand();
        User user = new User();
        user.setUserName("validate-user");
        command.setUser(user);

        // old password supplied when user doesn't have one
        user.setPassword(null);
        command.setOldPassword("Password1");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong error count for oldPassword", 1, context.getErrors().get("oldPassword").size());

        // no data supplied
        user.setPassword(User.hashPassword("Password1"));
        command.setOldPassword(null);
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong number of errors occurred", 3, context.getErrors().size());
        assertEquals("Wrong error count for oldPassword", 1, context.getErrors().get("oldPassword").size());
        assertEquals("Wrong error count for password", 1, context.getErrors().get("password").size());
        assertEquals("Wrong error count for password2", 1, context.getErrors().get("password2").size());

        // incorrect old password
        command.setOldPassword("bogus1");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong error count for oldPassword", 1, context.getErrors().get("oldPassword").size());
        err = context.getErrors().get("oldPassword").get(0);
        assertEquals("Wrong error", "Current password is incorrect.", err);

        // correct old password
        command.setOldPassword("Password1");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertFalse("Errors exist for oldPassword", context.getErrors().containsKey("oldPassword"));

        // password too short
        command.setPassword("short");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong error count for password", 1, context.getErrors().get("password").size());
        err = context.getErrors().get("password").get(0);
        assertEquals("Wrong error", "New password must be at least 6 characters long.", err);

        // password2 doesn't match
        command.setPassword("Password2");
        command.setPassword2("Password2-nomatch");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong error count for password2", 1, context.getErrors().get("password2").size());
        err = context.getErrors().get("password2").get(0);
        assertEquals("Wrong error", "New passwords do not match.", err);

        // ok
        command.setPassword2("Password2");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Errors occurred", 0, context.getErrors().size());
    }

}
