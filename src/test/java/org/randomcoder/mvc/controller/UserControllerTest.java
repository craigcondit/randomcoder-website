package org.randomcoder.mvc.controller;

import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.bo.UserBusiness;
import org.randomcoder.db.Role;
import org.randomcoder.db.User;
import org.randomcoder.mvc.command.AccountCreateCommand;
import org.randomcoder.mvc.command.ChangePasswordCommand;
import org.randomcoder.mvc.command.UserAddCommand;
import org.randomcoder.mvc.command.UserEditCommand;
import org.randomcoder.mvc.command.UserProfileCommand;
import org.randomcoder.mvc.editor.RolePropertyEditor;
import org.randomcoder.mvc.validator.AccountCreateValidator;
import org.randomcoder.mvc.validator.ChangePasswordValidator;
import org.randomcoder.mvc.validator.UserAddValidator;
import org.randomcoder.mvc.validator.UserEditValidator;
import org.randomcoder.mvc.validator.UserProfileValidator;
import org.randomcoder.pagination.PagerInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.same;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class UserControllerTest {
  private IMocksControl control;
  private UserBusiness ub;
  private UserController c;
  private ChangePasswordValidator cpv;
  private AccountCreateValidator acv;
  private UserProfileValidator upv;
  private UserAddValidator uav;
  private UserEditValidator uev;
  private Principal p;
  private BindingResult br;
  private WebDataBinder wdb;
  private Model m;

  @Before public void setUp() {
    control = createControl();
    ub = control.createMock(UserBusiness.class);
    m = control.createMock(Model.class);
    cpv = control.createMock(ChangePasswordValidator.class);
    p = control.createMock(Principal.class);
    br = control.createMock(BindingResult.class);
    wdb = control.createMock(WebDataBinder.class);
    acv = control.createMock(AccountCreateValidator.class);
    upv = control.createMock(UserProfileValidator.class);
    uav = control.createMock(UserAddValidator.class);
    uev = control.createMock(UserEditValidator.class);
    c = new UserController();
    c.setUserBusiness(ub);
    c.setChangePasswordValidator(cpv);
    c.setMaximumPageSize(25);
    c.setAccountCreateValidator(acv);
    c.setUserProfileValidator(upv);
    c.setUserAddValidator(uav);
    c.setUserEditValidator(uev);
  }

  @After public void tearDown() {
    uav = null;
    uev = null;
    acv = null;
    upv = null;
    br = null;
    p = null;
    c = null;
    cpv = null;
    m = null;
    ub = null;
    control = null;
  }

  @Test public void testInitBinder() throws Exception {
    expect(wdb.getTarget()).andReturn(new Object());
    control.replay();

    c.initBinder(wdb);
    control.verify();
  }

  @Test public void testInitBinderUserProfile() throws Exception {
    expect(wdb.getTarget()).andReturn(new UserProfileCommand());
    wdb.setValidator(upv);
    control.replay();

    c.initBinder(wdb);
    control.verify();
  }

  @Test public void testInitBinderAccountCreate() throws Exception {
    expect(wdb.getTarget()).andReturn(new AccountCreateCommand());
    wdb.setValidator(acv);
    control.replay();

    c.initBinder(wdb);
    control.verify();
  }

  @Test public void testInitBinderUserAdd() throws Exception {
    expect(wdb.getTarget()).andReturn(new UserAddCommand());
    wdb.registerCustomEditor(same(Role.class), isA(RolePropertyEditor.class));
    wdb.setValidator(uav);
    control.replay();

    c.initBinder(wdb);
    control.verify();
  }

  @Test public void testInitBinderUserEdit() throws Exception {
    expect(wdb.getTarget()).andReturn(new UserEditCommand());
    wdb.registerCustomEditor(same(Role.class), isA(RolePropertyEditor.class));
    wdb.setValidator(uev);
    control.replay();

    c.initBinder(wdb);
    control.verify();
  }

  @Test public void testDeleteUser() throws Exception {
    ub.deleteUser(1L);
    control.replay();

    assertEquals("user-list-redirect", c.deleteUser(1L));
    control.verify();
  }

  @Test public void testListUsers() throws Exception {
    List<User> users = new ArrayList<>();
    Page<User> page = new PageImpl<>(users);

    Pageable pr = PageRequest.of(0, 20);
    Capture<Pageable> pc = newCapture();

    expect(ub.findAll(capture(pc))).andReturn(page);
    expect(m.addAttribute("users", page)).andReturn(m);
    expect(m.addAttribute(eq("pagerInfo"), isA(PagerInfo.class))).andReturn(m);
    control.replay();

    c.listUsers(m, pr, null);
    control.verify();

    assertEquals(Sort.by("userName"), pc.getValue().getSort());
    assertEquals(0, pc.getValue().getOffset());
    assertEquals(20, pc.getValue().getPageSize());
  }

  @Test public void testListUsersPageTooBig() throws Exception {
    List<User> users = new ArrayList<>();
    Page<User> page = new PageImpl<>(users);

    Pageable pr = PageRequest.of(0, 100);
    Capture<Pageable> pc = newCapture();

    expect(ub.findAll(capture(pc))).andReturn(page);
    expect(m.addAttribute("users", page)).andReturn(m);
    expect(m.addAttribute(eq("pagerInfo"), isA(PagerInfo.class))).andReturn(m);
    control.replay();

    c.listUsers(m, pr, null);
    control.verify();

    assertEquals(Sort.by("userName"), pc.getValue().getSort());
    assertEquals(0, pc.getValue().getOffset());
    assertEquals(25, pc.getValue().getPageSize());
  }

  @Test public void testChangePassword() {
    ChangePasswordCommand command = new ChangePasswordCommand();

    User user = new User();

    expect(p.getName()).andReturn("test");
    expect(ub.findUserByNameEnabled("test")).andReturn(user);
    control.replay();

    assertEquals("change-password", c.changePassword(command, p));
    assertSame(user, command.getUser());
    control.verify();
  }

  @Test public void testChangePasswordCancel() {
    assertEquals("user-profile-redirect", c.changePasswordCancel());
  }

  @Test public void testChangePasswordSubmit() {
    ChangePasswordCommand command = new ChangePasswordCommand();
    command.setPassword("password");
    User user = new User();

    expect(p.getName()).andReturn("test");
    expect(ub.findUserByNameEnabled("test")).andReturn(user);
    cpv.validate(same(command), isA(Errors.class));
    expect(br.hasErrors()).andReturn(false);
    ub.changePassword("test", "password");
    control.replay();

    assertEquals("user-profile-redirect",
        c.changePasswordSubmit(command, br, p));
    control.verify();
    assertSame(user, command.getUser());
  }

  @Test public void testChangePasswordSubmitErrors() {
    ChangePasswordCommand command = new ChangePasswordCommand();
    command.setPassword("password");
    User user = new User();

    expect(p.getName()).andReturn("test");
    expect(ub.findUserByNameEnabled("test")).andReturn(user);
    cpv.validate(same(command), isA(Errors.class));
    expect(br.hasErrors()).andReturn(true);
    control.replay();

    assertEquals("change-password", c.changePasswordSubmit(command, br, p));
    control.verify();
    assertSame(user, command.getUser());
  }

  @Test public void testUserProfile() {
    UserProfileCommand command = new UserProfileCommand();

    User user = new User();

    expect(p.getName()).andReturn("test");
    expect(ub.findUserByName("test")).andReturn(user);
    expect(m.addAttribute("user", user)).andReturn(m);
    control.replay();

    assertEquals("user-profile", c.userProfile(command, m, p));
    control.verify();
  }

  @Test public void testUserProfileCancel() {
    assertEquals("default", c.userProfileCancel());
  }

  @Test public void testUserProfileSubmit() {
    UserProfileCommand command = new UserProfileCommand();
    User user = new User();
    user.setId(1L);

    expect(p.getName()).andReturn("test");
    expect(ub.findUserByName("test")).andReturn(user);
    expect(br.hasErrors()).andReturn(false);
    ub.updateUser(command, 1L);
    control.replay();

    assertEquals("default", c.userProfileSubmit(command, br, m, p));
    control.verify();
  }

  @Test public void testUserProfileError() {
    UserProfileCommand command = new UserProfileCommand();
    User user = new User();
    user.setId(1L);

    expect(p.getName()).andReturn("test");
    expect(ub.findUserByName("test")).andReturn(user);
    expect(br.hasErrors()).andReturn(true);
    expect(m.addAttribute("user", user)).andReturn(m);
    control.replay();

    assertEquals("user-profile", c.userProfileSubmit(command, br, m, p));
    control.verify();
  }

  @Test public void testAccountCreate() {
    assertEquals("account-create", c.accountCreate(null));
  }

  @Test public void testAccountCreateCancel() {
    assertEquals("default", c.accountCreateCancel());
  }

  @Test public void testAccountCreateSubmit() {
    AccountCreateCommand command = new AccountCreateCommand();

    expect(br.hasErrors()).andReturn(false);
    ub.createAccount(command);
    control.replay();

    assertEquals("account-create-done", c.accountCreateSubmit(command, br));
    control.verify();
  }

  @Test public void testAccountCreateSubmitError() {
    AccountCreateCommand command = new AccountCreateCommand();

    expect(br.hasErrors()).andReturn(true);
    control.replay();

    assertEquals("account-create", c.accountCreateSubmit(command, br));
    control.verify();
  }

  @Test public void testAddUser() {
    UserAddCommand command = new UserAddCommand();
    List<Role> roles = new ArrayList<>();

    expect(ub.listRoles()).andReturn(roles);
    expect(m.addAttribute(eq("availableRoles"), same(roles))).andReturn(m);
    control.replay();

    assertEquals("user-add", c.addUser(command, m));
    control.verify();
    assertTrue(command.isEnabled());
  }

  @Test public void testAddUserCancel() {
    assertEquals("user-list-redirect", c.addUserCancel());
  }

  @Test public void testAddUserSubmit() {
    UserAddCommand command = new UserAddCommand();

    expect(br.hasErrors()).andReturn(false);
    ub.createUser(command);
    control.replay();

    assertEquals("user-list-redirect", c.addUserSubmit(command, br, m));
    control.verify();
  }

  @Test public void testAddUserSubmitError() {
    UserAddCommand command = new UserAddCommand();
    List<Role> roles = new ArrayList<>();

    expect(br.hasErrors()).andReturn(true);
    expect(ub.listRoles()).andReturn(roles);
    expect(m.addAttribute(eq("availableRoles"), same(roles))).andReturn(m);
    control.replay();

    assertEquals("user-add", c.addUserSubmit(command, br, m));
    control.verify();
  }

  @Test public void testEditUser() {
    UserEditCommand command = new UserEditCommand();
    command.setId(1L);
    List<Role> roles = new ArrayList<>();

    expect(ub.listRoles()).andReturn(roles);
    expect(m.addAttribute(eq("availableRoles"), same(roles))).andReturn(m);
    ub.loadUserForEditing(command, 1L);
    control.replay();

    assertEquals("user-edit", c.editUser(command, m));
    control.verify();
  }

  @Test public void testEditUserCancel() {
    assertEquals("user-list-redirect", c.editUserCancel());
  }

  @Test public void testEditUserSubmit() {
    UserEditCommand command = new UserEditCommand();
    command.setId(1L);

    expect(br.hasErrors()).andReturn(false);
    ub.updateUser(command, 1L);
    control.replay();

    assertEquals("user-list-redirect", c.editUserSubmit(command, br, m));
    control.verify();
  }

  @Test public void testEditUserSubmitError() {
    UserEditCommand command = new UserEditCommand();
    command.setId(1L);
    List<Role> roles = new ArrayList<>();

    expect(br.hasErrors()).andReturn(true);
    expect(ub.listRoles()).andReturn(roles);
    expect(m.addAttribute(eq("availableRoles"), same(roles))).andReturn(m);
    control.replay();

    assertEquals("user-edit", c.editUserSubmit(command, br, m));
    control.verify();
  }
}