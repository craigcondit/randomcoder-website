package org.randomcoder.mvc.editor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyEditorSupport;

/**
 * Enum property editor support.
 */
@SuppressWarnings("rawtypes")
public class EnumPropertyEditor
        extends PropertyEditorSupport {
    private static final Logger logger =
            LoggerFactory.getLogger(EnumPropertyEditor.class);

    private final Class<? extends Enum> enumType;

    /**
     * Creates a new property editor.
     *
     * @param enumType enum type
     */
    public EnumPropertyEditor(Class<? extends Enum> enumType) {
        this.enumType = enumType;
    }

    /**
     * Gets the value of the current Enum's name as a {@code String}.
     *
     * @return String enum string value
     */
    @Override
    public String getAsText() {
        logger.debug("getAsText()");

        Object value = getValue();
        if (value == null) {
            return "";
        }

        return ((Enum) value).name();
    }

    /**
     * Populates the editor with the Enum object with the given name.
     *
     * @param string string value of name property
     * @throws IllegalArgumentException if object could not be loaded
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setAsText(String string)
            throws IllegalArgumentException {
        logger.debug("setAsText(" + string + ")");

        if (string == null || string.trim().length() == 0) {
            setValue(null);
            return;
        }

        setValue(Enum.valueOf(enumType, string));
    }

}
