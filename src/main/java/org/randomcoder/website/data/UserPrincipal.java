package org.randomcoder.website.data;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class UserPrincipal implements Principal {

    private final String name;
    private final Set<String> roles;

    public UserPrincipal(User user) {
        this.name = user.getUserName();
        var roleSet = new HashSet<String>();
        for (Role role : user.getRoles()) {
            roleSet.add(role.getName());
        }
        this.roles = Collections.unmodifiableSet(roleSet);
    }

    @Override
    public String getName() {
        return name;
    }

    public Set<String> getRoles() {
        return roles;
    }

}
