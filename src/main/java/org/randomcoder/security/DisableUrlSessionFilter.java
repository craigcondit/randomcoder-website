package org.randomcoder.security;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Servlet filter which disables URL-encoded session identifiers.
 */
public class DisableUrlSessionFilter implements Filter
{
	/**
	 * Filters requests to disable URL-based session identifiers.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		// skip non-http requests
		if (!(request instanceof HttpServletRequest))
		{
			chain.doFilter(request, response);
			return;
		}

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		// clear session if session id in URL
		if (httpRequest.isRequestedSessionIdFromURL())
		{
			HttpSession session = httpRequest.getSession();
			if (session != null)
				session.invalidate();
		}

		// wrap response to remove URL encoding and continue
		chain.doFilter(request, new NullEncodingHttpServletResponse(httpResponse));
	}

	/**
	 * Unused.
	 */
	@Override
	public void init(FilterConfig config) throws ServletException
	{}

	/**
	 * Unused.
	 */
	@Override
	public void destroy()
	{}

}
