package org.randomcoder.website.bo;

import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.website.command.AccountCreateCommand;
import org.randomcoder.website.command.UserAddCommand;
import org.randomcoder.website.command.UserEditCommand;
import org.randomcoder.website.dao.RoleDao;
import org.randomcoder.website.dao.UserDao;
import org.randomcoder.website.data.Role;
import org.randomcoder.website.data.User;
import org.randomcoder.website.data.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class UserBusinessImplTest {

    private IMocksControl control;
    private UserBusinessImpl ub;
    private UserDao ud;
    private RoleDao rd;

    @Before
    public void setUp() throws Exception {
        control = createControl();
        ud = control.createMock(UserDao.class);
        rd = control.createMock(RoleDao.class);

        ub = new UserBusinessImpl();
        ub.userDao = ud;
        ub.roleDao = rd;
    }

    @After
    public void tearDown() {
        control = null;
        ub = null;
        ud = null;
        rd = null;
    }

    @Test
    public void testChangePassword() {
        ud.changePassword("test-change-password", User.hashPassword("test-new-password"));
        expectLastCall();
        control.replay();

        ub.changePassword("test-change-password", "test-new-password");
        control.verify();
    }

    @Test
    public void testCreateUser() {
        UserAddCommand cmd = new UserAddCommand();

        cmd.setUserName("test-create");
        cmd.setEmailAddress("test-create@example.com");
        cmd.setPassword("testCreate1");
        cmd.setPassword2("testCreate1");
        cmd.setEnabled(true);

        Role testRole = new Role();
        testRole.setId(1L);
        testRole.setName("test-role");
        testRole.setDescription("Test role");

        cmd.setRoles(List.of(testRole));

        Capture<User> created = newCapture();

        expect(ud.save(capture(created))).andReturn(1L);
        control.replay();

        ub.createUser(cmd);
        control.verify();

        assertEquals("test-create", created.getValue().getUserName());
    }

    @Test
    public void testCreateUserByPassword() {
        AccountCreateCommand cmd = new AccountCreateCommand();
        cmd.setUserName("test-create");
        cmd.setEmailAddress("test-create@example.com");
        cmd.setPassword("testCreate1");
        cmd.setPassword2("testCreate1");
        cmd.setWebsite("http://www.example.com/");

        Capture<User> created = newCapture();

        expect(ud.save(capture(created))).andReturn(1L);
        control.replay();

        ub.createUser(cmd);
        control.verify();

        assertEquals("test-create", created.getValue().getUserName());
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setUserName("test-update-user");
        user.setEnabled(true);
        user.setEmailAddress("test-update@example.com");
        user.setPassword(User.hashPassword("testPassword1"));
        user.setRoles(new ArrayList<Role>());
        user.setId(1L);

        UserEditCommand cmd = new UserEditCommand();
        cmd.load(user);
        cmd.setEmailAddress("test-update2@example.com");
        cmd.setPassword("testPassword2");
        cmd.setPassword2("testPassword2");

        Capture<User> saved = newCapture();

        expect(ud.findById(1L)).andReturn(user);
        expect(ud.save(capture(saved))).andReturn(1L);
        control.replay();

        ub.updateUser(cmd, 1L);
        control.verify();

        assertEquals("test-update2@example.com", saved.getValue().getEmailAddress());
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setId(1L);
        ud.deleteById(1L);
        control.replay();

        ub.deleteUser(1L);
        control.verify();
    }

    @Test
    public void testLoadUserForEditing() {
        User user = new User();
        user.setUserName("test-load-user");
        user.setEnabled(true);
        user.setEmailAddress("test-load@example.com");
        user.setPassword(User.hashPassword("testPassword1"));
        user.setRoles(new ArrayList<Role>());
        user.setId(1L);

        UserEditCommand cmd = new UserEditCommand();

        expect(ud.findById(1L)).andReturn(user);
        control.replay();

        ub.loadUserForEditing(cmd::load, 1L);
        control.verify();

        assertEquals("Wrong username", "test-load-user", cmd.getUserName());
    }

    @Test
    public void testLoadUserForEditingUserNotFound() {
        try {
            UserEditCommand cmd = new UserEditCommand();
            ub.loadUserForEditing(cmd, (long) -1);
            fail("UserNotFoundException expected");
        } catch (UserNotFoundException e) {
            // pass
        }
    }

    @Test
    public void testAuditUsernamePasswordLogin() {
        ud.updateLoginTime("test-audit-user");
        expectLastCall();
        control.replay();

        ub.auditUsernamePasswordLogin("test-audit-user");
        control.verify();
    }

    @Test
    public void testListRoles() {
        Role role = new Role();
        role.setId(1L);
        role.setName("test-role");
        role.setDescription("test-desc");

        List<Role> roles = List.of(role);

        expect(rd.listByDescription()).andReturn(roles);
        control.replay();

        var result = ub.listRoles();
        control.verify();

        assertSame(roles, result);

    }

    @Test
    public void testFindRoleByName() {
        Role role = new Role();
        role.setId(1L);
        role.setName("test-role");
        role.setDescription("test-desc");

        expect(rd.findByName("test-role")).andReturn(role);
        control.replay();

        var result = ub.findRoleByName("test-role");
        control.verify();
        assertSame(role, result);
    }

}