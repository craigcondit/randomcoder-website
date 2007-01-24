package com.randomcoder.user;

import java.util.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.randomcoder.io.*;
import com.randomcoder.security.cardspace.CardSpaceCredentials;

/**
 * Business implementation for user management.
 * 
 * <pre>
 * Copyright (c) 2006, 2007, Craig Condit. All rights reserved.
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
public class UserBusinessImpl implements UserBusiness
{
	private UserDao userDao;
	private CardSpaceTokenDao cardSpaceTokenDao;	
	
	/**
	 * Sets the UserDao implementation to use.
	 * @param userDao UserDao implementation
	 */
	@Required
	public void setUserDao(UserDao userDao)
	{
		this.userDao = userDao;
	}
	
	/**
	 * Sets the CardSpaceTokenDao implementation to use.
	 * @param cardSpaceTokenDao CardSpaceTokenDao implementation
	 */
	@Required
	public void setCardSpaceTokenDao(CardSpaceTokenDao cardSpaceTokenDao)
	{
		this.cardSpaceTokenDao = cardSpaceTokenDao;
	}

	@Transactional
	public void changePassword(String userName, String password)
	{
		User user = userDao.findByUserName(userName);
		
		if (user == null)
			throw new UserNotFoundException("Unknown user: " + userName);

		user.setPassword(User.hashPassword(password));
		
		userDao.update(user);
	}

	@Transactional
	public void createUser(Producer<User> producer)
	{
		User user = new User();
		producer.produce(user);
		userDao.create(user);		
	}
	
	@Transactional
	public void updateUser(Producer<User> producer, Long userId)
	{
		User user = loadUser(userId);
		producer.produce(user);
		userDao.update(user);
	}

	@Transactional
	public void deleteUser(Long userId)
	{
		User user = loadUser(userId);
		userDao.delete(user);
	}

	@Transactional(readOnly=true)
	public void loadUserForEditing(Consumer<User> consumer, Long userId)
	{
		User user = loadUser(userId);
		consumer.consume(user);
	}

	private User loadUser(Long userId)
	{
		User user = userDao.read(userId);
		if (user == null)
			throw new UserNotFoundException();
		return user;
	}

	@Transactional
	public void associateCardSpaceCredentials(Long userId, CardSpaceCredentials credentials)
	{
		User user = loadUser(userId);
		
		String ppid = credentials.getPrivatePersonalIdentifier();
		String issuerHash = calculateIssuerHash(credentials);
		
		if (cardSpaceTokenDao.findByPrivatePersonalIdentifier(ppid, issuerHash) != null)
			throw new CardSpaceTokenExistsException();
		
		CardSpaceToken token = new CardSpaceToken();
		token.setCreationDate(new Date());
		token.setUser(user);
		token.setPrivatePersonalIdentifier(ppid);
		token.setIssuerHash(issuerHash);
		token.setEmailAddress(credentials.getEmailAddress());				
		
		cardSpaceTokenDao.create(token);		
	}	
	
	private String calculateIssuerHash(CardSpaceCredentials credentials)
	{
		return DigestUtils.shaHex(credentials.getIssuerPublicKey()).toLowerCase(Locale.US);
	}
	
}
