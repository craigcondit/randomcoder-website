package org.randomcoder.mvc.controller;

import javax.inject.Inject;

import org.randomcoder.bo.UserBusiness;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for user management.
 */
@Controller("userController")
public class UserController
{
	private UserBusiness userBusiness;

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
	 * Deletees the selected user.
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
