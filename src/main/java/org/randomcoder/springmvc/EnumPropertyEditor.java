package org.randomcoder.springmvc;

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.*;

/**
 * Enum property editor support.
 * 
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
public class EnumPropertyEditor extends PropertyEditorSupport
{
	private static final Log logger = LogFactory.getLog(EnumPropertyEditor.class);

	private Class<? extends Enum> enumType;
	
	/**
	 * Creates a new property editor.
	 * 
	 * @param enumType
	 *            enum type
	 */
	public EnumPropertyEditor(Class<? extends Enum> enumType)
	{
		this.enumType = enumType;
	}
	
	/**
	 * Gets the value of the current Enum's name as a {@code String}.
	 * @return String enum string value
	 */
	@Override
	public String getAsText()
	{
		logger.debug("getAsText()");

		Object value = getValue();
		if (value == null)
			return "";
		
		return ((Enum) value).name();
	}

	/**
	 * Populates the editor with the Enum object with the given name.
	 * @param string string value of name property
	 * @throws IllegalArgumentException if object could not be loaded
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
