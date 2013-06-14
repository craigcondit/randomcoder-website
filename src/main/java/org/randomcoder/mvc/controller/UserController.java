package org.randomcoder.mvc.controller;

import java.security.Principal;
import java.util.*;

import javax.inject.Inject;

import org.randomcoder.bo.UserBusiness;
import org.randomcoder.db.User;
import org.randomcoder.mvc.command.*;
import org.randomcoder.mvc.validator.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for user management.
 */
@Controller("userController")
public class UserController
{
	private UserBusiness userBusiness;
	private ChangePasswordValidator changePasswordValidator;
	private UserProfileValidator userProfileValidator;
	private AccountCreateValidator accountCreateValidator;

	private int defaultPageSize = 25;
	private int maximumPageSize = 100;

	/**
	 * Sets the UserBusiness implementation to use.
	 * 
	 * @param userBusiness
	 *            UserBusiness implementation
	 */
	@Inject
	public void setUserBusiness(UserBusiness userBusiness)
	{
		this.userBusiness = userBusiness;
	}

	/**
	 * Sets the default number of items to display per page (defaults to 25).
	 * 
	 * @param defaultPageSize
	 *            default number of items per page
	 */
	@Value("${user.pagesize.default}")
	public void setDefaultPageSize(int defaultPageSize)
	{
		this.defaultPageSize = defaultPageSize;
	}

	/**
	 * Sets the maximum number of items to allow per page (defaults to 100).
	 * 
	 * @param maximumPageSize
	 *            maximum number of items per page
	 */
	@Value("${user.pagesize.max}")
	public void setMaximumPageSize(int maximumPageSize)
	{
		this.maximumPageSize = maximumPageSize;
	}

	/**
	 * Sets the change password validator to use.
	 * 
	 * @param changePasswordValidator
	 *            change password validator
	 */
	@Inject
	public void setChangePasswordValidator(ChangePasswordValidator changePasswordValidator)
	{
		this.changePasswordValidator = changePasswordValidator;
	}

	/**
	 * Sets the user profile validator to use.
	 * 
	 * @param userProfileValidator
	 *            user profile validator
	 */
	@Inject
	public void setUserProfileValidator(UserProfileValidator userProfileValidator)
	{
		this.userProfileValidator = userProfileValidator;
	}

	/**
	 * Sets the account create validator to use.
	 * 
	 * @param accountCreateValidator
	 *            account create validator
	 */
	@Inject
	public void setAccountCreateValidator(AccountCreateValidator accountCreateValidator)
	{
		this.accountCreateValidator = accountCreateValidator;
	}

	/**
	 * Sets up data binding.
	 * 
	 * @param binder
	 *            data binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder)
	{
		Object target = binder.getTarget();
		if (target instanceof UserProfileCommand)
		{
			binder.setValidator(userProfileValidator);
		}
		else if (target instanceof AccountCreateCommand)
		{
			binder.setValidator(accountCreateValidator);
		}
	}

	/**
	 * Lists users.
	 * 
	 * @param command
	 *            user list command
	 * @param model
	 *            MVC model
	 * @return user list view
	 */
	@RequestMapping("/user")
	public String listUsers(UserListCommand command, Model model)
	{
		// set range
		int start = Math.max(0, command.getStart());
		command.setStart(start);

		int limit = command.getLimit();
		if (limit <= 0)
		{
			limit = defaultPageSize;
		}
		if (limit > maximumPageSize)
		{
			limit = maximumPageSize;
		}
		command.setLimit(limit);

		List<User> users = userBusiness.listUsersInRange(start, limit);
		int count = userBusiness.countUsers();

		// populate model
		model.addAttribute("users", users);
		model.addAttribute("pageCount", count);
		model.addAttribute("pageStart", start);
		model.addAttribute("pageLimit", limit);

		return "user-list";
	}

	/**
	 * Begins creating a new account.
	 * 
	 * @param command
	 *            account create command
	 * @return account create view
	 */
	@RequestMapping(value = "/account/create", method = RequestMethod.GET)
	public String accountCreate(@ModelAttribute("command") AccountCreateCommand command)
	{
		return "account-create";
	}

	/**
	 * Cancels creation of a new account.
	 * 
	 * @return default view
	 */
	@RequestMapping(value = "/account/create", method = RequestMethod.POST, params = "cancel")
	public String accountCreateCancel()
	{
		return "default";
	}

	/**
	 * Completes creation of a new account.
	 * 
	 * @param command
	 *            account create command
	 * @param result
	 *            validation result
	 * @return account create done view
	 */
	@RequestMapping(value = "/account/create", method = RequestMethod.POST, params = "!cancel")
	public String accountCreateSubmit(
			@ModelAttribute("command") @Validated AccountCreateCommand command,
			BindingResult result)
	{
		if (result.hasErrors())
		{
			return "account-create";
		}

		userBusiness.createAccount(command);

		return "account-create-done";
	}

	/**
	 * Begins editing a user's profile.
	 * 
	 * @param command
	 *            user profile command
	 * @param model
	 *            MVC model
	 * @param principal
	 *            current user
	 * @return user profile view
	 */
	@RequestMapping(value = "/user/profile", method = RequestMethod.GET)
	public String userProfile(
			@ModelAttribute("command") UserProfileCommand command,
			Model model, Principal principal)
	{
		Map<String, Object> data = new HashMap<String, Object>();

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
	public String userProfileCancel()
	{
		return "default";
	}

	/**
	 * Finishes editing a user's profile.
	 * 
	 * @param command
	 *            user profile command
	 * @param result
	 *            validation result
	 * @param model
	 *            MVC model
	 * @param principal
	 *            current user
	 * @return default view
	 */
	@RequestMapping(value = "/user/profile", method = RequestMethod.POST, params = "!cancel")
	public String userProfileSubmit(
			@ModelAttribute("command") @Validated UserProfileCommand command,
			BindingResult result,
			Model model,
			Principal principal)
	{
		User user = userBusiness.findUserByName(principal.getName());

		if (result.hasErrors())
		{
			model.addAttribute("user", user);
			return "user-profile";
		}

		userBusiness.updateUser(command, user.getId());

		return "default";
	}

	/**
	 * Begins changing a user's password.
	 * 
	 * @param command
	 *            change password command
	 * @param principal
	 *            current user
	 * @return change password view
	 */
	@RequestMapping(value = "/user/profile/change-password", method = RequestMethod.GET)
	public String changePassword(
			@ModelAttribute("command") ChangePasswordCommand command,
			Principal principal)
	{
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
	public String changePasswordCancel()
	{
		return "user-profile-redirect";
	}

	/**
	 * Finishes changing a user's password.
	 * 
	 * @param command
	 *            change password command
	 * @param result
	 *            validation result
	 * @param principal
	 *            current user
	 * @return redirect to user view
	 */
	@RequestMapping(value = "/user/profile/change-password", method = RequestMethod.POST, params = "!cancel")
	public String changePasswordSubmit(
			@ModelAttribute("command") ChangePasswordCommand command,
			BindingResult result,
			Principal principal)
	{
		String userName = principal.getName();

		User user = userBusiness.findUserByNameEnabled(userName);
		command.setUser(user);
		changePasswordValidator.validate(command, result);
		if (result.hasErrors())
		{
			return "change-password";
		}

		userBusiness.changePassword(userName, command.getPassword());

		return "user-profile-redirect";
	}

	/**
	 * Deletes the selected user.
	 * 
	 * @param id
	 *            user ID
	 * @return user list redirect
	 */
	@RequestMapping("/user/delete")
	public String deleteUser(@RequestParam("id") long id)
	{
		userBusiness.deleteUser(id);
		return "user-list-redirect";
	}
}
