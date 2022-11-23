package org.randomcoder.thymeleaf;

import jakarta.ws.rs.core.SecurityContext;
import org.thymeleaf.context.IContext;

import java.util.HashMap;
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

    static Map<String, Object> securityAttributes(SecurityContext securityContext) {
        var map = new HashMap<String, Object>();

        var userPrincipal = securityContext.getUserPrincipal();
        if (userPrincipal == null) {
            map.put("username", null);
        } else {
            map.put("username", userPrincipal.getName());
        }
        return map;
    }

}
