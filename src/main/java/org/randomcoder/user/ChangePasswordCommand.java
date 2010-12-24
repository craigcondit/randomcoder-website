package org.randomcoder.user;

import java.io.Serializable;


/**
 * Controller used to change a user's password.
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
public class ChangePasswordCommand implements Serializable
{
	private static final long serialVersionUID = -6349438305307982312L;
	
	private String oldPassword;
	private String password;
	private String password2;
	private User user;
	
	/**
	 * Gets the value of the old password.
	 * @return old password
	 */
	public String getOldPassword()
	{
		return oldPassword;
	}
	
	/**
	 * Sets the value of the old password.
	 * @param oldPassword old password
	 */
	public void setOldPassword(String oldPassword)
	{
		this.oldPassword = oldPassword;
	}
	
	/**
	 * Gets the new password.
	 * @return new password
	 */
	public String getPassword()
	{
		return password;
	}
	
	/**
	 * Sets the new password.
	 * @param password new password
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
		
	/**
	 * Gets the new password again for verification.
	 * @return new password
	 */
	public String getPassword2()
	{
		return password2;
	}
	
	/**
	 * Sets the new password again for verification.
	 * @param password2 new password
	 */
	public void setPassword2(String password2)
	{
		this.password2 = password2;
	}
	
	/**
	 * Gets the user associated with this request.
	 * @return user
	 */
	public User getUser()
	{
		return user;
	}
	
	/**
	 * Sets the user associated with this request.
	 * @param user user
	 */
	public void setUser(User user)
	{
		this.user = user;
	}
	
}
