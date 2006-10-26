package com.randomcoder.user;

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.*;

import com.randomcoder.bean.Role;
import com.randomcoder.dao.RoleDao;

/**
 * Property editor for roles.
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
public class RolePropertyEditor extends PropertyEditorSupport
{
	private static final Log logger = LogFactory.getLog(RolePropertyEditor.class);
	
	private final RoleDao roleDao;
	
	/**
	 * Creates a new property editor for Role objects.
	 * @param roleDao RoleDao implementation to use
	 */
	public RolePropertyEditor(RoleDao roleDao)
	{
		this.roleDao = roleDao;
	}

	@Override
	public String getAsText()
	{
		Role role = (Role) getValue();
		
		String result = (role == null) ? "" : role.getName();
		
		if (logger.isDebugEnabled())
			logger.debug("getAsText: " + result);
		
		return result;
	}

	@Override
	public void setAsText(String string) throws IllegalArgumentException
	{
		if (logger.isDebugEnabled())
			logger.debug("setAsText: " + string);
		
		Role role = roleDao.findByName(string);
		if (role == null) throw new IllegalArgumentException("No such role: " + string);
		
		setValue(role);
	}

}
