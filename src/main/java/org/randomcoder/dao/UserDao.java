package org.randomcoder.dao;

import org.randomcoder.db.User;

public interface UserDao {

    Page<User> listByName(long offset, long length);

    User findByName(String userName, boolean includeDisabled);

    User findById(long userId);

    void deleteById(long userId);

    Long save(User user);

    void updateLoginTime(String userName);

    void changePassword(String userName, String passwordHash);

}
