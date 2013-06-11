package org.randomcoder.bo;

import java.util.*;

import javax.inject.Inject;

import org.hibernate.Hibernate;
import org.randomcoder.db.*;
import org.randomcoder.io.*;
import org.randomcoder.user.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business implementation for user management.
 * 
 * <pre>
 * Copyright (c) 2006-2007, Craig Condit. All rights reserved.
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
@Component("userBusiness")
public class UserBusinessImpl implements UserBusiness
{
	private UserDao userDao;
	private RoleDao roleDao;
	
	/**
	 * Sets the UserDao implementation to use.
	 * 
	 * @param userDao
	 *            UserDao implementation
	 */
	@Inject
	public void setUserDao(UserDao userDao)
	{
		this.userDao = userDao;
	}
	
	/**
	 * Sets the RoleDao implementation to use.
	 * 
	 * @param roleDao
	 *            RoleDao implementation
	 */
	@Inject
	public void setRoleDao(RoleDao roleDao)
	{
		this.roleDao = roleDao;
	}
	
	@Override
	@Transactional
	public void changePassword(String userName, String password)
	{
		User user = userDao.findByUserName(userName);
		
		if (user == null)
			throw new UserNotFoundException("Unknown user: " + userName);

		user.setPassword(User.hashPassword(password));
		
		userDao.update(user);
	}

	@Override
	@Transactional
	public void createUser(Producer<User> producer)
	{
		User user = new User();
		producer.produce(user);
		userDao.create(user);		
	}
	
	@Override
	@Transactional
	public void createAccount(Producer<User> producer)
	{
		User user = new User();
		producer.produce(user);
		userDao.create(user);		
	}
	
	@Override
	@Transactional
	public void updateUser(Producer<User> producer, Long userId)
	{
		User user = loadUser(userId);
		producer.produce(user);
		userDao.update(user);
	}

	@Override
	@Transactional
	public void deleteUser(Long userId)
	{
		User user = loadUser(userId);
		
		userDao.delete(user);		
	}

	@Override
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
		{
			throw new UserNotFoundException();
		}
		return user;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Role> listRoles()
	{
		return roleDao.listAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Role findRoleByName(String name)
	{		
		return roleDao.findByName(name);
	}
	
	@Override
	@Transactional(readOnly = true)
	public User findUserByName(String name)
	{
		User user = userDao.findByUserName(name);
		if (user != null)
		{
			Hibernate.initialize(user.getRoles());
		}
		return user;
	}

	@Override
	@Transactional(readOnly = true)
	public User findUserByNameEnabled(String name)
	{
		User user = userDao.findByUserNameEnabled(name);
		if (user != null)
		{
			Hibernate.initialize(user.getRoles());
		}
		return user;
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> listUsersInRange(int start, int limit)
	{
		List<User> users = userDao.listAllInRange(start, limit);
		for (User user : users)
		{
			Hibernate.initialize(user.getRoles());
		}
		return users;
	}

	@Override
	@Transactional(readOnly = true)
	public int countUsers()
	{
		return userDao.countAll();
	}

	@Override
	@Transactional
	public void auditUsernamePasswordLogin(String userName)
	{
		User user = userDao.findByUserName(userName);
		
		if (user == null)
			throw new UserNotFoundException("Unknown user: " + userName);
		
		user.setLastLoginDate(new Date());
		
		userDao.update(user);
	}
}