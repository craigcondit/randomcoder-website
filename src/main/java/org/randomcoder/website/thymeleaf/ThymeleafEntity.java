package org.randomcoder.website.thymeleaf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ThymeleafEntity {

    private final String view;
    private final Map<String, String> variables = new HashMap<>();

    public ThymeleafEntity(String view) {
        this.view = view;
    }

    public ThymeleafEntity withVariable(String key, String value) {
        variables.put(key, value);
        return this;
    }

    public String getView() {
        return view;
    }

    public Object getVariable(String name) {
        return variables.get(name);
    }

    public boolean containsVariable(String name) {
        return variables.containsKey(name);
    }

    public Set<String> getVariableNames() {
        return variables.keySet();
    }

}
