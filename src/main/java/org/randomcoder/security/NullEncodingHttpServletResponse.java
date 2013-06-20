package org.randomcoder.security;

import javax.servlet.http.*;

/**
 * HTTP servlet response wrapper which does no encoding of URLs.
 */
public class NullEncodingHttpServletResponse extends HttpServletResponseWrapper
{
	/**
	 * Creates a new response.
	 * 
	 * @param response
	 *          original response
	 */
	public NullEncodingHttpServletResponse(HttpServletResponse response)
	{
		super(response);
	}

	@Override
	public String encodeRedirectUrl(String url)
	{
		return url;
	}

	@Override
	public String encodeRedirectURL(String url)
	{
		return url;
	}

	@Override
	public String encodeUrl(String url)
	{
		return url;
	}

	@Override
	public String encodeURL(String url)
	{
		return url;
	}
}