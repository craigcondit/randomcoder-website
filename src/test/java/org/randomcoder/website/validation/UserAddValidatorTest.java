package org.randomcoder.website.validation;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.website.bo.UserBusiness;
import org.randomcoder.website.command.UserAddCommand;
import org.randomcoder.website.data.Role;
import org.randomcoder.website.data.User;

import java.util.ArrayList;
import java.util.Locale;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class UserAddValidatorTest {
    private UserAddValidator validator;
    private IMocksControl control;
    private UserBusiness ub;

    @Before
    public void setUp() {
        control = createControl();
        ub = control.createMock(UserBusiness.class);
        validator = new UserAddValidator();
        validator.minimumUsernameLength = 6;
        validator.minimumPasswordLength = 6;
        validator.userBusiness = ub;
    }

    @After
    public void tearDown() {
        validator = null;
        control = null;
        ub = null;
    }

    @Test
    public void testValidate() {
        ValidatorContext context;
        String err;

        // setup
        UserAddCommand command = new UserAddCommand();

        User user = new User();
        user.setUserName("existing-user");
        user.setEmailAddress("existing@example.com");
        user.setPassword(User.hashPassword("Password1"));
        user.setRoles(new ArrayList<Role>());
        user.setEnabled(true);

        expect(ub.findUserByName("existing-user")).andStubReturn(user);
        expect(ub.findUserByName("new-user")).andStubReturn(null);
        control.replay();

        // no data supplied
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong number of errors occurred", 4, context.getErrors().size());
        assertEquals("Wrong error count for userName", 1, context.getErrors().get("userName").size());
        assertEquals("Wrong error count for emailAddress", 1, context.getErrors().get("emailAddress").size());
        assertEquals("Wrong error count for password", 1, context.getErrors().get("password").size());
        assertEquals("Wrong error count for password2", 1, context.getErrors().get("password2").size());

        // username too short
        command.setUserName("short");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong error count for userName", 1, context.getErrors().get("userName").size());
        err = context.getErrors().get("userName").get(0);
        assertEquals("Wrong error", "User name must be at least 6 characters long.", err);

        // username exists
        command.setUserName("existing-user");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong error count for userName", 1, context.getErrors().get("userName").size());
        err = context.getErrors().get("userName").get(0);
        assertEquals("Wrong error", "Another user with that user name already exists.", err);

        // correct username
        command.setUserName("new-user");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertFalse("Error exists for userName", context.getErrors().containsKey("userName"));

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
        assertFalse("Error exists for emailAddress", context.getErrors().containsKey("emailAddress"));

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
        assertFalse("Error exists for password", context.getErrors().containsKey("password"));

        // password 2 doesn't match
        command.setPassword2("Password2");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong error count for password2", 1, context.getErrors().get("password2").size());
        err = context.getErrors().get("password2").get(0);
        assertEquals("Wrong error", "Passwords do not match.", err);

        // password 2 specified, but not password 1
        command.setPassword(null);
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong error count for password", 1, context.getErrors().get("password").size());
        assertEquals("Wrong error count for password2", 1, context.getErrors().get("password2").size());
        err = context.getErrors().get("password").get(0);
        assertEquals("Wrong error code", "Password is required.", err);
        err = context.getErrors().get("password2").get(0);
        assertEquals("Wrong error code", "Passwords do not match.", err);

        // all data valid
        command.setPassword("Password2");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Errors occurred", 0, context.getErrors().size());

        control.verify();
    }

}