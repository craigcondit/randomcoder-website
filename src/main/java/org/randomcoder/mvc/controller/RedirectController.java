package org.randomcoder.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Controller used for encoding redirects.
 */
@Controller("redirectController") public class RedirectController {
  /**
   * Redirects to the given URL.
   *
   * @param url      url to redirect to
   * @param request  HTTP servlet request
   * @param response HTTP servlet response (used for redirection)
   * @throws IOException if an error occurs
   */
  @RequestMapping("/redirect") public void redirect(
      @RequestParam("url") String url, HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    URL target = null;
    try {
      target = new URL(new URL(request.getRequestURL().toString()), url);
    } catch (MalformedURLException e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    response.sendRedirect(target.toExternalForm());
  }
}