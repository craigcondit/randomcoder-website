package org.randomcoder.website.thymeleaf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ThymeleafEntity {

    private final String view;
    private final Map<String, Object> variables = new HashMap<>();

    public ThymeleafEntity(String view) {
        this.view = view;
    }

    public ThymeleafEntity withVariable(String key, Object value) {
        variables.put(key, value);
        return this;
    }

    public ThymeleafEntity withVariables(Map<String, ? extends Object> values) {
        variables.putAll(values);
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
