package org.randomcoder.security;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.same;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.randomcoder.test.GenericProxy;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

@SuppressWarnings("javadoc")
public class DisableUrlSessionFilterTest {
	private IMocksControl control;
	private FilterChain fc;
	private DisableUrlSessionFilter filter;

	@Before
	public void setUp() throws Exception {
		control = createControl();
		fc = control.createMock(FilterChain.class);

		filter = new DisableUrlSessionFilter();
		filter.init(null);
	}

	@After
	public void tearDown() {
		filter.destroy();
		filter = null;
	}

	@Test
	public void testDoFilter() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.setRequestedSessionIdFromURL(true);
		request.setSession(new MockHttpSession());

		assertNotNull("Missing session", request.getSession(false));

		fc.doFilter(same(request), isA(NullEncodingHttpServletResponse.class));
		control.replay();

		filter.doFilter(request, response, fc);
		control.verify();

		assertNull("Session still active", request.getSession(false));
	}

	@Test
	public void testNonHttpServletRequest() throws Exception {
		// proxy the request object so that it no longer implements
		// HttpServletRequest
		ServletRequest request = (ServletRequest) GenericProxy.proxy(new MockHttpServletRequest(),
				ServletRequest.class);

		MockHttpServletResponse response = new MockHttpServletResponse();

		fc.doFilter(same(request), same(response));
		control.replay();

		filter.doFilter(request, response, fc);
		control.verify();
	}
}
