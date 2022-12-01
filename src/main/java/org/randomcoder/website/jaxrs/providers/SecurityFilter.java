package org.randomcoder.website.jaxrs.providers;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.ext.Provider;
import org.randomcoder.website.bo.UserBusiness;
import org.randomcoder.website.data.User;
import org.randomcoder.website.data.UserSecurityContext;
import org.randomcoder.website.jaxrs.features.SecurityFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Inject
    public HttpServletRequest request;

    @Inject
    public UserBusiness userBusiness;

    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        var authCookie = context.getCookies().get(SecurityFeature.AUTH_COOKIE);
        if (authCookie == null) {
            // not logged in
            return;
        }

        var token = authCookie.getValue();
        User user = userBusiness.validateAuthToken(token);
        if (user == null) {
            // token didn't validate
            return;
        }

        // get prior secure setting
        boolean secure = context.getSecurityContext().isSecure();
        context.setSecurityContext(new UserSecurityContext(user, secure));
    }

}
