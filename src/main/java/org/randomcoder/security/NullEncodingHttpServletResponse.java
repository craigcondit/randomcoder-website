package org.randomcoder.security;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * HTTP servlet response wrapper which does no encoding of URLs.
 */
public class NullEncodingHttpServletResponse
    extends HttpServletResponseWrapper {
  /**
   * Creates a new response.
   *
   * @param response original response
   */
  public NullEncodingHttpServletResponse(HttpServletResponse response) {
    super(response);
  }

  @SuppressWarnings("deprecation")
  @Override public String encodeRedirectUrl(String url) {
    return url;
  }

  @Override public String encodeRedirectURL(String url) {
    return url;
  }

  @SuppressWarnings("deprecation")
  @Override public String encodeUrl(String url) {
    return url;
  }

  @Override public String encodeURL(String url) {
    return url;
  }
}
