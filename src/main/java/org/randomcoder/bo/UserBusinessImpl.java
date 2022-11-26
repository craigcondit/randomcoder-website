package org.randomcoder.bo;

import jakarta.inject.Inject;
import org.randomcoder.dao.Page;
import org.randomcoder.dao.RoleDao;
import org.randomcoder.dao.UserDao;
import org.randomcoder.db.Role;
import org.randomcoder.db.User;
import org.randomcoder.io.Consumer;
import org.randomcoder.io.Producer;
import org.randomcoder.user.UserNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Business implementation for user management.
 */
@Component("userBusiness")
public class UserBusinessImpl implements UserBusiness {

    private RoleDao roleDao;
    private UserDao userDao;

    @Inject
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Inject
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void changePassword(String userName, String password) {
        userDao.changePassword(userName, User.hashPassword(password));
    }

    @Override
    public void createUser(Producer<User> producer) {
        User user = new User();
        producer.produce(user);
        userDao.save(user);
    }

    @Override
    public void updateUser(Producer<User> producer, Long userId) {
        User user = loadUser(userId);
        producer.produce(user);
        userDao.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userDao.deleteById(userId);
    }

    @Override
    public void loadUserForEditing(Consumer<User> consumer, Long userId) {
        User user = loadUser(userId);
        consumer.consume(user);
    }

    private User loadUser(Long userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    @Override
    public List<Role> listRoles() {
        return roleDao.listByDescription();
    }

    @Override
    public Role findRoleByName(String name) {
        return roleDao.findByName(name);
    }

    @Override
    public User findUserByName(String name) {
        return userDao.findByName(name, true);
    }

    @Override
    public User findUserByNameEnabled(String name) {
        return userDao.findByName(name, false);
    }

    @Override
    public Page<User> findAll(long offset, long length) {
        return userDao.listByName(offset, length);
    }

    @Override
    public void auditUsernamePasswordLogin(String userName) {
        userDao.updateLoginTime(userName);
    }

}
