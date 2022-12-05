package org.randomcoder.website.validation;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.website.bo.UserBusiness;
import org.randomcoder.website.command.AccountCreateCommand;
import org.randomcoder.website.data.Role;
import org.randomcoder.website.data.User;

import java.util.ArrayList;
import java.util.Locale;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AccountCreateValidatorTest {

    private IMocksControl control;
    private UserBusiness ub;
    private AccountCreateValidator validator;

    @Before
    public void setUp() {
        control = createControl();
        ub = control.createMock(UserBusiness.class);
        validator = new AccountCreateValidator();
        validator.minimumPasswordLength = 6;
        validator.minimumUsernameLength = 6;
        validator.userBusiness = ub;
    }

    @After
    public void tearDown() {
        validator = null;
        ub = null;
        control = null;
    }

    @Test
    public void testValidate() {
        ValidatorContext context;

        // setup
        AccountCreateCommand command = new AccountCreateCommand();

        User user = new User();
        user.setUserName("existing-user");
        user.setEmailAddress("existing@example.com");
        user.setPassword(User.hashPassword("Password1"));
        user.setRoles(new ArrayList<Role>());
        user.setEnabled(true);

        expect(ub.findUserByName("existing-user")).andStubReturn(user);
        expect(ub.findUserByName("new-user")).andStubReturn(null);
        control.replay();

        // empty form
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong number of errors occurred", 4, context.getErrors().keySet().size());
        assertEquals("Wrong error count for userName", 1, context.getErrors().get("userName").size());
        assertEquals("Wrong error count for emailAddress", 1, context.getErrors().get("emailAddress").size());
        assertEquals("Wrong error count for password", 1, context.getErrors().get("password").size());
        assertEquals("Wrong error count for password2", 1, context.getErrors().get("password2").size());

        // username too short
        command.setUserName("short");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong error count for userName", 1, context.getErrors().get("userName").size());

        // username exists
        command.setUserName("existing-user");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong error count for userName", 1, context.getErrors().get("userName").size());

        // username valid
        command.setUserName("new-user");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertFalse("Errors found for userName", context.getErrors().containsKey("userName"));

        // password too short (and no match with password2)
        command.setPassword("short");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong error count for password", 1, context.getErrors().get("password").size());
        assertEquals("Wrong error count for password2", 1, context.getErrors().get("password2").size());

        // passwords valid
        command.setPassword("testPassword");
        command.setPassword2("testPassword");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertFalse("Errors found for password", context.getErrors().containsKey("password"));
        assertFalse("Errors found for password2", context.getErrors().containsKey("password2"));

        // invalid email address
        command.setEmailAddress("bogus");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong error count for emailAddress", 1, context.getErrors().get("emailAddress").size());

        // valid email address
        command.setEmailAddress("test@example.com");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertFalse("Errors found for emailAddress", context.getErrors().containsKey("emailAddress"));

        // invalid website
        command.setWebsite("bogus");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertEquals("Wrong error count for website", 1, context.getErrors().get("website").size());

        // valid website
        command.setWebsite("http://www.exmaple.com/");
        context = new ValidatorContext(Locale.US);
        validator.validate(context, command);
        assertFalse("Errors found for website", context.getErrors().containsKey("website"));

        assertEquals("Wrong number of errors occurred", 0, context.getErrors().size());

        control.verify();
    }

}