package org.randomcoder.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Login controller.
 */
@Controller("loginController")
public class LoginController
{
	/**
	 * Handle login.
	 * 
	 * @return login view
	 */
	@RequestMapping(value = "/login")
	public String login()
	{
		return "login";
	}

	/**
	 * Handle login error.
	 * 
	 * @return login error view
	 */
	@RequestMapping(value = "/login-error")
	public String loginError()
	{
		return "login-error";
	}
}