package org.randomcoder.website.thymeleaf;

import jakarta.ws.rs.core.SecurityContext;
import org.randomcoder.website.data.UserPrincipal;
import org.thymeleaf.context.IContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ThymeleafContext implements IContext {

    private final Map<String, Object> variables;

    public ThymeleafContext(ThymeleafEntity entity, SecurityContext securityContext) {
        variables = new HashMap<>();
        for (String name : entity.getVariableNames()) {
            variables.put(name, entity.getVariable(name));
        }
        if (securityContext != null) {
            variables.put("security", securityAttributes(securityContext));
        }
    }

    static Map<String, Object> securityAttributes(SecurityContext securityContext) {
        var map = new HashMap<String, Object>();
        map.put("username", null);
        map.put("roles", new HashSet<>());

        var principal = securityContext.getUserPrincipal();
        if (principal != null) {
            map.put("username", principal.getName());
            if (principal instanceof UserPrincipal up) {
                map.put("roles", up.getRoles());
            }
        }
        return map;
    }

    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }

    @Override
    public boolean containsVariable(String name) {
        return variables.containsKey(name);
    }

    @Override
    public Set<String> getVariableNames() {
        return variables.keySet();
    }

    @Override
    public Object getVariable(String name) {
        return variables.get(name);
    }

}
