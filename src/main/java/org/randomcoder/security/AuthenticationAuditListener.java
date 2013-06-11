package org.randomcoder.security;

import org.acegisecurity.Authentication;
import org.acegisecurity.event.authentication.AuthenticationSuccessEvent;
import org.randomcoder.bo.UserBusiness;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.*;

/**
 * ApplicationEvent listener which audits authentication events.  
 * 
 * <pre>
 * Copyright (c) 2007, Craig Condit. All rights reserved.
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
public class AuthenticationAuditListener implements ApplicationListener
{
	private UserBusiness userBusiness;
	
	/**
	 * Sets the UserBusiness implementation to use.
	 * @param userBusiness UserBusiness implementation
	 */
	@Required
	public void setUserBusiness(UserBusiness userBusiness)
	{
		this.userBusiness = userBusiness;
	}
	
	/**
	 * Handles an application event.
	 * @param event event to handle
	 */
	@Override
	public void onApplicationEvent(ApplicationEvent event)
	{
		if (event instanceof AuthenticationSuccessEvent)
		{
			processAuthenticationSuccessEvent((AuthenticationSuccessEvent) event);
		}		
	}
	
	private void processAuthenticationSuccessEvent(AuthenticationSuccessEvent event)
	{
		Authentication auth = event.getAuthentication();
		
		userBusiness.auditUsernamePasswordLogin(auth.getName());
	}	
}