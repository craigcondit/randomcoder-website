package org.randomcoder.mvc;

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.*;

/**
 * Enum property editor support.
 */
@SuppressWarnings("rawtypes")
public class EnumPropertyEditor extends PropertyEditorSupport
{
	private static final Log logger = LogFactory.getLog(EnumPropertyEditor.class);

	private Class<? extends Enum> enumType;

	/**
	 * Creates a new property editor.
	 * 
	 * @param enumType
	 *          enum type
	 */
	public EnumPropertyEditor(Class<? extends Enum> enumType)
	{
		this.enumType = enumType;
	}

	/**
	 * Gets the value of the current Enum's name as a {@code String}.
	 * 
	 * @return String enum string value
	 */
	@Override
	public String getAsText()
	{
		logger.debug("getAsText()");

		Object value = getValue();
		if (value == null)
		{
			return "";
		}
		
		return ((Enum) value).name();
	}

	/**
	 * Populates the editor with the Enum object with the given name.
	 * 
	 * @param string
	 *          string value of name property
	 * @throws IllegalArgumentException
	 *           if object could not be loaded
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setAsText(String string) throws IllegalArgumentException
	{
		logger.debug("setAsText(" + string + ")");

		if (string == null || string.trim().length() == 0)
		{
			setValue(null);
			return;
		}

		setValue(Enum.valueOf(enumType, string));
	}

}
