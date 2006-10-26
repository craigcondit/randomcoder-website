package com.randomcoder.user;

import java.util.List;

import com.randomcoder.bean.*;
import com.randomcoder.io.Consumer;

/**
 * Command class for editing users.
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
public class UserEditCommand extends UserAddCommand implements Consumer<User>
{
	private static final long serialVersionUID = 2923257330122456830L;
	
	private Long id;
	
	/**
	 * Gets the ID associated with this user.
	 * @return id
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the ID associated with this user.
	 * @param id id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	public void consume(User user)
	{
		setUserName(user.getUserName());
		setEmailAddress(user.getEmailAddress());
		setEnabled(user.isEnabled());
		
		List<Role> roleList = user.getRoles();
		Role[] roleArray = new Role[roleList.size()];
		roleList.toArray(roleArray);
		
		setRoles(roleArray);
	}
}
