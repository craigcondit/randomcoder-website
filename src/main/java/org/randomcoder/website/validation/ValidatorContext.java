package org.randomcoder.website.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ValidatorContext {

    private final ResourceBundle messages;
    private final Map<String, List<String>> errorMap = new HashMap<>();

    public ValidatorContext() {
        this(null);
    }

    public ValidatorContext(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }

        messages = ResourceBundle.getBundle(getClass().getPackageName() + ".ValidationMessages", locale);
    }

    public String resolveMessage(String message, Object... args) {
        var template = messages.getString(message);
        if (template == null) {
            return message;
        }
        return MessageFormat.format(template, args);
    }

    public void reject(String field, String template, Object... args) {
        String error = resolveMessage(template, args);
        List<String> errors = errorMap.get(field);
        if (errors == null) {
            errors = new ArrayList<>();
            errorMap.put(field, errors);
        }
        errors.add(error);
    }

    public boolean isValid() {
        return errorMap.isEmpty();
    }

    public Map<String, List<String>> getErrors() {
        return errorMap;
    }

}
