package com.randomcoder.user.test;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.randomcoder.user.*;

public class UserDaoMock implements UserDao
{
	private final List<User> users = new ArrayList<User>();
	private long primaryKey = 0;
	
	public int countAll()
	{
		return users.size();
	}

	public User findByUserName(String name)
	{
		for (User user : users)
		{
			if (name.equals(user.getUserName())) return user;
		}
		return null;
	}

	public User findByUserNameEnabled(String name)
	{
		for (User user : users)
		{
			if (name.equals(user.getUserName()) && user.isEnabled()) return user;
		}
		return null;
	}

	public List<User> listAll()
	{
		List<User> allUsers = new ArrayList<User>(users);
		Collections.sort(allUsers, new UserNameComparator());
		return allUsers;
	}

	public List<User> listAllInRange(int start, int limit)
	{
		List<User> allUsers = listAll();
		
		// validate range
		if (start < 0 || start >= allUsers.size()) return new ArrayList<User>();
		if (limit < 1 || start + limit >= allUsers.size()) return new ArrayList<User>();

		return allUsers.subList(start, start + limit + 1);
	}

	public List<User> listEnabled()
	{
		List<User> enabled = new LinkedList<User>(listAll());
		
		for (Iterator<User> it = enabled.iterator(); it.hasNext();)
		{
			if (!it.next().isEnabled()) it.remove();
		}
		
		return enabled;
	}

	public Long create(User newInstance)
	{
		validateRequiredFields(newInstance);
		
		User current = findByUserName(newInstance.getUserName());
		if (current != null) throw new IllegalArgumentException("UNIQUE violation: username = " + newInstance.getUserName());
				
		newInstance.setId(primaryKey++);
		users.add(newInstance);
		
		return newInstance.getId();
	}

	public void delete(User persistentObject)
	{
		Long id = persistentObject.getId();
		
		for (Iterator<User> it = users.iterator(); it.hasNext();)
		{
			if (it.next().getId().equals(id))
			{
				it.remove();
				return;
			}
		}
		throw new IllegalArgumentException("NOT FOUND: id = " + id);
	}

	public User read(Long id)
	{
		for (Iterator<User> it = users.iterator(); it.hasNext();)
		{
			User user = it.next();
			if (user.getId().equals(id)) return user;
		}
		return null;
	}

	public void update(User transientObject)
	{
		Long id = transientObject.getId();
		
		User loaded = read(id);
		if (loaded == null)
			throw new IllegalArgumentException("NOT FOUND: id = " + id);
		
		validateRequiredFields(transientObject);
		
		delete(loaded);
		
		users.add(transientObject);
	}
	
	private void validateRequiredFields(User user)
	{
		if (StringUtils.trimToNull(user.getUserName()) == null)
			throw new IllegalArgumentException("userName required");
		
		if (StringUtils.trimToNull(user.getEmailAddress()) == null)
			throw new IllegalArgumentException("emailAddress required");
		
		if (StringUtils.trimToNull(user.getPassword()) == null)
			throw new IllegalArgumentException("password required");
	}
	
	static class UserNameComparator implements Comparator<User>
	{
		public int compare(User u1, User u2)
		{
			String s1 = u1.getUserName();
			String s2 = u2.getUserName();
			
			// nulls come first
			s1 = (s1 == null) ? "A" : "B" + s1;
			s2 = (s2 == null) ? "A" : "B" + s2;
			
			return s1.compareTo(s2);
		}		
	}
}
