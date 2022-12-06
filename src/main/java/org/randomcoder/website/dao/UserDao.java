package org.randomcoder.website.dao;

import org.randomcoder.website.data.Page;
import org.randomcoder.website.data.User;

public interface UserDao {

    Page<User> listByName(long offset, long length);

    User findByName(String userName, boolean includeDisabled);

    User findById(long userId);

    void deleteById(long userId);

    long save(User user);

    void updateLoginTime(String userName);

    void changePassword(String userName, String passwordHash);

}
