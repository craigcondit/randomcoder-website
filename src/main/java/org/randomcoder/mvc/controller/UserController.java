package org.randomcoder.mvc.controller;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

/**
 * Controller class for user management.
 */
@Controller("userController")
public class UserController {
    private UserBusiness userBusiness;
    private ChangePasswordValidator changePasswordValidator;
    private UserProfileValidator userProfileValidator;
    private AccountCreateValidator accountCreateValidator;
    private UserAddValidator userAddValidator;
    private UserEditValidator userEditValidator;

    private int maximumPageSize = 100;

    /**
     * Sets the UserBusiness implementation to use.
     *
     * @param userBusiness UserBusiness implementation
     */
    @Inject
    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    /**
     * Sets the maximum number of items to allow per page (defaults to 100).
     *
     * @param maximumPageSize maximum number of items per page
     */
    @Value("${user.pagesize.max}")
    public void setMaximumPageSize(
            int maximumPageSize) {
        this.maximumPageSize = maximumPageSize;
    }

    /**
     * Sets the change password validator to use.
     *
     * @param changePasswordValidator change password validator
     */
    @Inject
    public void setChangePasswordValidator(
            ChangePasswordValidator changePasswordValidator) {
        this.changePasswordValidator = changePasswordValidator;
    }

    /**
     * Sets the user profile validator to use.
     *
     * @param userProfileValidator user profile validator
     */
    @Inject
    public void setUserProfileValidator(
            UserProfileValidator userProfileValidator) {
        this.userProfileValidator = userProfileValidator;
    }

    /**
     * Sets the account create validator to use.
     *
     * @param accountCreateValidator account create validator
     */
    @Inject
    public void setAccountCreateValidator(
            AccountCreateValidator accountCreateValidator) {
        this.accountCreateValidator = accountCreateValidator;
    }

    /**
     * Sets the user add validator to use.
     *
     * @param userAddValidator user add validator
     */
    @Inject
    @Named("userAddValidator")
    public void setUserAddValidator(
            UserAddValidator userAddValidator) {
        this.userAddValidator = userAddValidator;
    }

    /**
     * Sets the user edit validator to use.
     *
     * @param userEditValidator user edit validator
     */
    @Inject
    @Named("userEditValidator")
    public void setUserEditValidator(
            UserEditValidator userEditValidator) {
        this.userEditValidator = userEditValidator;
    }

    /**
     * Sets up data binding.
     *
     * @param binder data binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        Object target = binder.getTarget();
        if (target instanceof UserProfileCommand) {
            binder.setValidator(userProfileValidator);
        } else if (target instanceof AccountCreateCommand) {
            binder.setValidator(accountCreateValidator);
        } else if (target instanceof UserEditCommand) {
            binder.registerCustomEditor(Role.class,
                    new RolePropertyEditor(userBusiness));
            binder.setValidator(userEditValidator);
        } else if (target instanceof UserAddCommand) {
            binder.registerCustomEditor(Role.class,
                    new RolePropertyEditor(userBusiness));
            binder.setValidator(userAddValidator);
        }
    }

    /**
     * Lists users.
     *
     * @param model    MVC model
     * @param pageable paging parameters
     * @param request  HTTP servlet request
     * @return user list view
     */
    @RequestMapping("/user")
    public String listUsers(Model model,
                            @PageableDefault(25) Pageable pageable, HttpServletRequest request) {
        int size = pageable.getPageSize();
        int page = pageable.getPageNumber();
        if (size > maximumPageSize) {
            size = maximumPageSize;
            page = 0;
        }

        pageable = PageRequest.of(page, size, Sort.by("userName"));

        Page<User> users = userBusiness.findAll(pageable);
        model.addAttribute("users", users);
        model.addAttribute("pagerInfo", new PagerInfo<>(users, request));

        return "user-list";
    }

    /**
     * Begins creating a new account.
     *
     * @param command account create command
     * @return account create view
     */
    @RequestMapping(value = "/account/create", method = RequestMethod.GET)
    public String accountCreate(
            @ModelAttribute("command") AccountCreateCommand command) {
        return "account-create";
    }

    /**
     * Cancels creation of a new account.
     *
     * @return default view
     */
    @RequestMapping(value = "/account/create", method = RequestMethod.POST, params = "cancel")
    public String accountCreateCancel() {
        return "default";
    }

    /**
     * Completes creation of a new account.
     *
     * @param command account create command
     * @param result  validation result
     * @return account create done view
     */
    @RequestMapping(value = "/account/create", method = RequestMethod.POST, params = "!cancel")
    public String accountCreateSubmit(
            @ModelAttribute("command") @Validated AccountCreateCommand command,
            BindingResult result) {
        if (result.hasErrors()) {
            return "account-create";
        }

        userBusiness.createAccount(command);

        return "account-create-done";
    }

    /**
     * Begins adding a new user.
     *
     * @param command user add command
     * @param model   MVC model
     * @return user add view
     */
    @RequestMapping(value = "/user/add", method = RequestMethod.GET)
    public String addUser(@ModelAttribute("command") UserAddCommand command,
                          Model model) {
        model.addAttribute("availableRoles", userBusiness.listRoles());
        command.setEnabled(true);
        return "user-add";
    }

    /**
     * Cancels adding a new user.
     *
     * @return redirect to user list view
     */
    @RequestMapping(value = "/user/add", method = RequestMethod.POST, params = "cancel")
    public String addUserCancel() {
        return "user-list-redirect";
    }

    /**
     * Completes adding a new user.
     *
     * @param command user add command
     * @param result  validation result
     * @param model   MVC model
     * @return redirect to user list view
     */
    @RequestMapping(value = "/user/add", method = RequestMethod.POST, params = "!cancel")
    public String addUserSubmit(
            @ModelAttribute("command") @Validated UserAddCommand command,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("availableRoles", userBusiness.listRoles());
            return "user-add";
        }

        userBusiness.createUser(command);

        return "user-list-redirect";
    }

    /**
     * Begins editing a new user.
     *
     * @param command user edit command
     * @param model   MVC model
     * @return account create view
     */
    @RequestMapping(value = "/user/edit", method = RequestMethod.GET)
    public String editUser(@ModelAttribute("command") UserEditCommand command,
                           Model model) {
        model.addAttribute("availableRoles", userBusiness.listRoles());
        userBusiness.loadUserForEditing(command, command.getId());
        return "user-edit";
    }

    /**
     * Cancels editing a user.
     *
     * @return redirect to user list view
     */
    @RequestMapping(value = "/user/edit", method = RequestMethod.POST, params = "cancel")
    public String editUserCancel() {
        return "user-list-redirect";
    }

    /**
     * Completes editing of a user.
     *
     * @param command user edit command
     * @param result  validation result
     * @param model   MVC model
     * @return redirect to user list view
     */
    @RequestMapping(value = "/user/edit", method = RequestMethod.POST, params = "!cancel")
    public String editUserSubmit(
            @ModelAttribute("command") @Validated UserEditCommand command,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("availableRoles", userBusiness.listRoles());
            return "user-edit";
        }

        userBusiness.updateUser(command, command.getId());

        return "user-list-redirect";
    }

    /**
     * Begins editing a user's profile.
     *
     * @param command   user profile command
     * @param model     MVC model
     * @param principal current user
     * @return user profile view
     */
    @RequestMapping(value = "/user/profile", method = RequestMethod.GET)
    public String userProfile(
            @ModelAttribute("command") UserProfileCommand command, Model model,
            Principal principal) {
        User user = userBusiness.findUserByName(principal.getName());
        command.setEmailAddress(user.getEmailAddress());
        command.setWebsite(user.getWebsite());
        model.addAttribute("user", user);

        return "user-profile";
    }

    /**
     * Cancels editing of a user's profile.
     *
     * @return default view
     */
    @RequestMapping(value = "/user/profile", method = RequestMethod.POST, params = "cancel")
    public String userProfileCancel() {
        return "default";
    }

    /**
     * Finishes editing a user's profile.
     *
     * @param command   user profile command
     * @param result    validation result
     * @param model     MVC model
     * @param principal current user
     * @return default view
     */
    @RequestMapping(value = "/user/profile", method = RequestMethod.POST, params = "!cancel")
    public String userProfileSubmit(
            @ModelAttribute("command") @Validated UserProfileCommand command,
            BindingResult result, Model model, Principal principal) {
        User user = userBusiness.findUserByName(principal.getName());

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "user-profile";
        }

        userBusiness.updateUser(command, user.getId());

        return "default";
    }

    /**
     * Begins changing a user's password.
     *
     * @param command   change password command
     * @param principal current user
     * @return change password view
     */
    @RequestMapping(value = "/user/profile/change-password", method = RequestMethod.GET)
    public String changePassword(
            @ModelAttribute("command") ChangePasswordCommand command,
            Principal principal) {
        User user = userBusiness.findUserByNameEnabled(principal.getName());
        command.setUser(user);
        return "change-password";
    }

    /**
     * Cancels editing a user's password.
     *
     * @return redirect to user profile view
     */
    @RequestMapping(value = "/user/profile/change-password", method = RequestMethod.POST, params = "cancel")
    public String changePasswordCancel() {
        return "user-profile-redirect";
    }

    /**
     * Finishes changing a user's password.
     *
     * @param command   change password command
     * @param result    validation result
     * @param principal current user
     * @return redirect to user view
     */
    @RequestMapping(value = "/user/profile/change-password", method = RequestMethod.POST, params = "!cancel")
    public String changePasswordSubmit(
            @ModelAttribute("command") ChangePasswordCommand command,
            BindingResult result, Principal principal) {
        String userName = principal.getName();

        User user = userBusiness.findUserByNameEnabled(userName);
        command.setUser(user);
        changePasswordValidator.validate(command, result);
        if (result.hasErrors()) {
            return "change-password";
        }

        userBusiness.changePassword(userName, command.getPassword());

        return "user-profile-redirect";
    }

    /**
     * Deletes the selected user.
     *
     * @param id user ID
     * @return user list redirect
     */
    @RequestMapping("/user/delete")
    public String deleteUser(
            @RequestParam("id") long id) {
        userBusiness.deleteUser(id);
        return "user-list-redirect";
    }
}
