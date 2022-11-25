package org.randomcoder.dao;

import org.randomcoder.db.Role;
import org.randomcoder.db.User;

import java.util.List;

public interface RoleDao {

    List<Role> listByDescription();

    Role findByName(String roleName);

}
