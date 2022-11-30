package org.randomcoder.website.jaxrs.providers;

import jakarta.annotation.Priority;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/*
 * Basic design:
 *
 * We need to secure endpoints, and maintain compatiblity with browsers as well as API clients.
 *
 * API clients will likely use an Authorization: header with a token; HTML clients will use a Cookie header.
 *
 * On initial request, we check for an Authorization header. If present, and valid, ew populate the SecurityContext and move on.
 * If header is missing or invalid, we check for an auth cookie. Same flow.
 *
 * If none are detected, we proceed as though the user is not yet logged in.
 *
 * When a 403 error is generated, we need to intercept this on output. This might mean injecting just before
 *
 *
 * - Annotate resources with @RolesAllowed / @PermitAll, etc.
 * - Register an authentication filter (i.e. this class)
 *   - We might eventually have some API clients, so we should handle them differently then browsers.

 *   - If POST to @Login endpoint, parse username / password and generate cookie, then proceed
 *   - If
 *   - If cookie exists, use it to verify authentication information
 *   - If
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Context
    public HttpServletRequest request;

    @Context
    public ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        logger.info("Request: {}", request);
    }

}
