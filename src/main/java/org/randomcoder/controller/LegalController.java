package org.randomcoder.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
	@RequestMapping("/legal/about")
	public String about()
	{
		return "legal-about";
	}

	/**
	 * Displays the license page.
	 * 
	 * @return license view
	 */
	@RequestMapping("/legal/license")
	public String license()
	{
		return "legal-license";
	}
}