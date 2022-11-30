package org.randomcoder.website.jaxrs.features;

import jakarta.annotation.Priority;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Locale;

public class SecurityFeature implements DynamicFeature {

    private static final URI LOGIN_URI = URI.create("/login");
    private static final String REDIRECT_COOKIE = "login-redirect";
    private static final String MIME_APP_PREFIX = "application/";
    private static final String TEXT_XML_PREFIX = "text/xml";

    @Override
    public void configure(ResourceInfo resource, FeatureContext context) {
        // DenyAll on method takes precedence over class
        if (resource.getResourceMethod().getAnnotation(DenyAll.class) != null) {
            context.register(new SecurityValidationRequestFilter());
            return;
        }

        // RolesAllowed on method takes precedence over class
        var rolesAllowed = resource.getResourceMethod().getAnnotation(RolesAllowed.class);
        if (rolesAllowed != null) {
            context.register(new SecurityValidationRequestFilter(rolesAllowed.value(), isApiMethod(resource)));
            return;
        }

        // PermitAll on method takes precedence over class
        if (resource.getResourceMethod().getAnnotation(PermitAll.class) != null) {
            return;
        }

        // RolesAllowed on the class takes precedence over PermitAll
        var classRolesAllowed = resource.getResourceClass().getAnnotation(RolesAllowed.class);
        if (classRolesAllowed != null) {
            context.register(new SecurityValidationRequestFilter(classRolesAllowed.value(), isApiMethod(resource)));
        }
    }

    private boolean isApiMethod(ResourceInfo resource) {
        // get the content types this method returns
        var produces = resource.getResourceMethod().getAnnotation(Produces.class);
        if (produces == null) {
            // unknown, assume non-API
            return false;
        }

        for (String value : produces.value()) {
            var normalized = value.trim().toLowerCase(Locale.US);
            if (normalized.startsWith(MIME_APP_PREFIX)) {
                return true;
            }
            if (normalized.startsWith(TEXT_XML_PREFIX)) {
                return true;
            }
        }

        return false;
    }

    @Priority(Priorities.AUTHORIZATION)
    private static class SecurityValidationRequestFilter implements ContainerRequestFilter {

        final boolean denyAll;
        final String[] rolesAllowed;
        final boolean apiMethod;

        SecurityValidationRequestFilter() {
            this.denyAll = true;
            this.rolesAllowed = new String[]{};
            this.apiMethod = false;
        }

        SecurityValidationRequestFilter(String[] rolesAllowed, boolean apiMethod) {
            this.denyAll = false;
            this.rolesAllowed = rolesAllowed;
            this.apiMethod = apiMethod;
        }

        @Override
        public void filter(ContainerRequestContext context) throws IOException {
            if (denyAll || rolesAllowed.length == 0) {
                // no roles will allow access, so no point in authenticating
                throw new ForbiddenException(); // 403
            }

            if (!isAuthenticated(context)) {
                // API clients should know better, make them re-authenticate
                if (apiMethod) {
                    throw new NotAuthorizedException("Unauthorized"); // 401
                }

                // Browser handling: we need to redirect them to a login page, (i.e. /login).
                // However, if the original request was a GET, we need to save the originally requested
                // URL so that we can redirect the user back to it after successful login.

                // If the request was *not* a GET, we need to clear the saved URL so that the login response
                // will redirect back to the original page.

                var cookieBuilder = new NewCookie
                        .Builder(REDIRECT_COOKIE)
                        .httpOnly(true);

                if (isGet(context)) {
                    cookieBuilder.value(context.getUriInfo().getRequestUri().toString());
                } else {
                    cookieBuilder.value("");
                    cookieBuilder.expiry(new Date(0L));
                }

                var response = Response.status(Response.Status.FOUND)
                        .location(LOGIN_URI)
                        .cookie(cookieBuilder.build())
                        .build();

                context.abortWith(response);
                return;
            }

            for (String role : rolesAllowed) {
                if (context.getSecurityContext().isUserInRole(role)) {
                    return;
                }
            }

            // user is logged in, but lacking required roles
            throw new ForbiddenException();
        }

        static boolean isGet(ContainerRequestContext context) {
            return context.getMethod().equalsIgnoreCase(HttpMethod.GET);
        }

        static boolean isAuthenticated(ContainerRequestContext context) {
            return context.getSecurityContext().getUserPrincipal() != null;
        }

    }

}
