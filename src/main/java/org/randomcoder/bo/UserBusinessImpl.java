package org.randomcoder.bo;

import jakarta.inject.Inject;
import org.hibernate.Hibernate;
import org.randomcoder.dao.RoleDao;
import org.randomcoder.dao.UserDao;
import org.randomcoder.db.Role;
import org.randomcoder.db.User;
import org.randomcoder.db.UserRepository;
import org.randomcoder.io.Consumer;
import org.randomcoder.io.Producer;
import org.randomcoder.user.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business implementation for user management.
 */
@Component("userBusiness")
public class UserBusinessImpl implements UserBusiness {
    private UserRepository userRepository;

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

    /**
     * Sets the user repository to use.
     *
     * @param userRepository user repository
     */
    @Inject
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void changePassword(String userName, String password) {
        userDao.changePassword(userName, User.hashPassword(password));
    }

    @Override
    @Transactional("transactionManager")
    public void createUser(Producer<User> producer) {
        User user = new User();
        producer.produce(user);
        userRepository.save(user);
    }

    @Override
    @Transactional("transactionManager")
    public void updateUser(Producer<User> producer, Long userId) {
        User user = loadUser(userId);
        producer.produce(user);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userDao.deleteById(userId);
    }

    @Override
    @Transactional(value = "transactionManager", readOnly = true)
    public void loadUserForEditing(Consumer<User> consumer, Long userId) {
        User user = loadUser(userId);
        consumer.consume(user);
    }

    private User loadUser(Long userId) {
        User user = userRepository.getReferenceById(userId);
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
        return userDao.findByName(name, true, true);
    }

    @Override
    public User findUserByNameEnabled(String name) {
        return userDao.findByName(name, false, true);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        var result = userDao.listByName(pageable.getOffset(), pageable.getPageSize(), true);
        return new PageImpl<>(result.getContent(), pageable, result.getTotalSize());
    }

    @Override
    public void auditUsernamePasswordLogin(String userName) {
        userDao.updateLoginTime(userName);
    }

}
