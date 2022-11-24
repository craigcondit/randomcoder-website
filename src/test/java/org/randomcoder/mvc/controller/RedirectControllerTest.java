package org.randomcoder.mvc.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.expect;

public class RedirectControllerTest {
    private RedirectController rc;
    private IMocksControl control;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Before
    public void setUp() {
        rc = new RedirectController();
        control = EasyMock.createControl();
        request = control.createMock(HttpServletRequest.class);
        response = control.createMock(HttpServletResponse.class);
    }

    @After
    public void tearDown() {
        rc = null;
        control = null;
        response = null;
    }

    @Test
    public void testHandle() throws Exception {
        expect(request.getRequestURL())
                .andStubReturn(new StringBuffer("http://localhost/test/"));
        response.sendRedirect("http://randomcoder.org/test/");
        control.replay();

        rc.redirect("http://randomcoder.org/test/", request, response);
        control.verify();
    }

}