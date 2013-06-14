package org.randomcoder.mvc.controller;

import java.security.Principal;
import java.util.List;

import javax.inject.Inject;

import org.randomcoder.bo.UserBusiness;
import org.randomcoder.db.User;
import org.randomcoder.mvc.command.*;
import org.randomcoder.mvc.validator.ChangePasswordValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for user management.
 */
@Controller("userController")
public class UserController
{
	private UserBusiness userBusiness;
	private ChangePasswordValidator changePasswordValidator;

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
