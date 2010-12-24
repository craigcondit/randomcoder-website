package com.randomcoder.user;

import java.util.Date;

import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.CancellableFormController;

import com.randomcoder.cardspace.*;
import com.randomcoder.crypto.*;
import com.randomcoder.security.cardspace.CardSpaceCredentials;

/**
 * Controller class which handles adding user accounts.
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
public class AccountCreateController extends CancellableFormController
{
	private static final long TOKEN_EXPIRATION_TIME = 30 * 60 * 1000; // 30 minutes
	
	private UserBusiness userBusiness;
	private CertificateContext certificateContext;
	private EncryptionContext encryptionContext;

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
	 * Sets the certificate context used to lookup private keys.
	 * @param certificateContext certificate context
	 */
	@Required
	public void setCertificateContext(CertificateContext certificateContext)
	{
		this.certificateContext = certificateContext;
	}
	
	/**
	 * Sets the encryption context to use.
	 * @param encryptionContext encryption context
	 */
	@Required
	public void setEncryptionContext(EncryptionContext encryptionContext)
	{
		this.encryptionContext = encryptionContext;
	}
	
	/**
	 * Binds custom property editors.
	 * @param request HTTP request
	 * @param binder data binder
	 * @throws Exception if an error occurs 
	 */
	@Override
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception
	{
		super.initBinder(request, binder);
		binder.registerCustomEditor(CardSpaceCredentials.class, new CardSpaceCredentialsPropertyEditor(certificateContext));
		binder.registerCustomEditor(CardSpaceTokenSpec.class, new EncryptedObjectPropertyEditor(encryptionContext));
	}
	
	/**
	 * Creates a new account based on form submission.
	 */
	@Override
	public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
	throws Exception
	{
		AccountCreateCommand form = (AccountCreateCommand) command;
		
		if ("INFOCARD".equals(form.getFormType()))
		{
			CardSpaceCredentials credentials = form.getXmlToken();
			
			if (!form.isFormComplete())
			{				
				if (credentials != null)
				{
					// update form
					String ppid = credentials.getPrivatePersonalIdentifier();
					String issuerHash = CardSpaceUtils.calculateIssuerHash(credentials);
					Date now = new Date();
					Date expiration = new Date(now.getTime() + TOKEN_EXPIRATION_TIME);				
					
					CardSpaceTokenSpec spec = new CardSpaceTokenSpec(ppid, issuerHash, expiration);
					form.setCardSpaceTokenSpec(spec);
					form.setEmailAddress(credentials.getEmailAddress());
					form.setWebsite(credentials.getWebPage());
				}
				
				// redirect to form
				return showForm(request, response, errors);
			}
			
			userBusiness.createAccount(form, form.getCardSpaceTokenSpec());
		}
		else if ("PASS".equals(form.getFormType()))
		{
			// password auth, done
			userBusiness.createAccount(form);			
		}

		return new ModelAndView(getSuccessView());
	}
	
}
