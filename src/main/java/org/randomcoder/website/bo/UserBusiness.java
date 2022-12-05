package org.randomcoder.website.bo;

import org.randomcoder.website.data.Page;
import org.randomcoder.website.data.Role;
import org.randomcoder.website.data.User;

import java.util.List;
import java.util.function.Consumer;

public interface UserBusiness {

    User findUserByName(String name);

    void createUser(Consumer<User> visitor);

    void updateUser(Consumer<User> visitor, Long userId);

    void deleteUser(Long userId);

    void changePassword(String userName, String password);

    void auditUsernamePasswordLogin(String userName);

    void loadUserForEditing(Consumer<User> consumer, Long userId);

    UserAuthentication validateAuthToken(String securityToken);

    String generateAuthToken(User user, boolean rememberMe);

    List<Role> listRoles();

    Role findRoleByName(String name);

    User findUserByNameEnabled(String name);

    Page<User> findAll(long offset, long length);


}