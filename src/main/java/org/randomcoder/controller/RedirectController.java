package org.randomcoder.controller;

import java.io.IOException;
import java.net.*;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Controller used for encoding redirects.
 */
@Controller("redirectController")
public class RedirectController
{
	/**
	 * Redirects to the given URL.
	 * 
	 * @param url
	 *            url to redirect to
	 * @param response
	 *            HTTP servlet response (used for redirection)
	 * @throws IOException
	 *             if an error occurs
	 */
	@RequestMapping("/redirect")
	public void redirect(@RequestParam("url") String url, HttpServletResponse response)
			throws IOException
	{
		URL target = null;
		try
		{
			target = new URL(url);
		}
		catch (MalformedURLException e)
		{
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		response.sendRedirect(target.toExternalForm());
	}
}