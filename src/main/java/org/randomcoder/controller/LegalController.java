package org.randomcoder.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Legal documents controller.
 */
@Controller("legalController")
public class LegalController
{
	/**
	 * Displays the about page.
	 * 
	 * @return about view
	 */
	@RequestMapping(value = "/legal/about", method = RequestMethod.GET)
	public String about()
	{
		return "legal-about";
	}

	/**
	 * Displays the license page.
	 * 
	 * @return license view
	 */
	@RequestMapping(value = "/legal/license", method = RequestMethod.GET)
	public String license()
	{
		return "legal-license";
	}
}