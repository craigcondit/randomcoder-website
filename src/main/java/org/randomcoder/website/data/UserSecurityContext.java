package org.randomcoder.website.data;

import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;

public class UserSecurityContext implements SecurityContext {

    private final UserPrincipal principal;
    private final boolean secure;

    public UserSecurityContext(User user, boolean secure) {
        this.principal = new UserPrincipal(user);
        this.secure = secure;
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        return principal.getRoles().contains(role);
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public String getAuthenticationScheme() {
        return FORM_AUTH;
    }

}
