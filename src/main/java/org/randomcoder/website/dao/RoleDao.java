package org.randomcoder.website.dao;

import org.randomcoder.website.data.Role;

import java.util.List;

public interface RoleDao {

    List<Role> listByDescription();

    Role findByName(String roleName);

}
