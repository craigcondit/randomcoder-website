package com.randomcoder.test.mock.dao;

import java.util.*;

import com.randomcoder.user.*;

public class RoleDaoMock implements RoleDao
{
	private long primaryKey = 0;	
	private final List<Role> roles = new ArrayList<Role>();

	public Role findByName(String name)
	{
		for (Role role : roles)
		{
			if (role.getName().equals(name)) return role;
		}
		return null;
	}

	public List<Role> listAll()
	{
		List<Role> all = new ArrayList<Role>(roles);
		Collections.sort(all);
		return all;
	}

	public Role read(Long id)
	{
		for (Iterator<Role> it = roles.iterator(); it.hasNext();)
		{
			Role role = it.next();
			if (role.getId().equals(id)) return role;
		}
		return null;
	}
	
	public Long mockCreate(Role newInstance)
	{
		Role current = findByName(newInstance.getName());
		if (current != null) throw new IllegalArgumentException("UNIQUE violation: username = " + newInstance.getName());
				
		newInstance.setId(primaryKey++);
		roles.add(newInstance);
		
		return newInstance.getId();
	}
	
	public void mockDelete(Role persistentObject)
	{
		Long id = persistentObject.getId();
		
		for (Iterator<Role> it = roles.iterator(); it.hasNext();)
		{
			if (it.next().getId().equals(id))
			{
				it.remove();
			}
		}
	}

	public void mockUpdate(Role transientObject)
	{
		Long id = transientObject.getId();
		
		Role loaded = read(id);
		if (loaded != null) mockDelete(loaded);
		
		roles.add(transientObject);
	}
}
