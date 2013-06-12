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
	 *          UserDao implementation
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
	 *          RoleDao implementation
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
	@Transactional(readOnly = true)
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