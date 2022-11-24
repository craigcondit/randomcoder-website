package org.randomcoder.bo;

import jakarta.inject.Inject;
import org.hibernate.Hibernate;
import org.randomcoder.db.Role;
import org.randomcoder.db.RoleRepository;
import org.randomcoder.db.User;
import org.randomcoder.db.UserRepository;
import org.randomcoder.io.Consumer;
import org.randomcoder.io.Producer;
import org.randomcoder.user.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Business implementation for user management.
 */
@Component("userBusiness")
public class UserBusinessImpl
        implements UserBusiness {
    private RoleRepository roleRepository;
    private UserRepository userRepository;

    /**
     * Sets the user repository to use.
     *
     * @param userRepository user repository
     */
    @Inject
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Sets the role repository to use.
     *
     * @param roleRepository role repository
     */
    @Inject
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional("transactionManager")
    public void changePassword(String userName, String password) {
        User user = userRepository.findByUserName(userName);

        if (user == null) {
            throw new UserNotFoundException("Unknown user: " + userName);
        }

        user.setPassword(User.hashPassword(password));

        userRepository.save(user);
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
    public void createAccount(Producer<User> producer) {
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
    @Transactional("transactionManager")
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
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
    @Transactional(value = "transactionManager", readOnly = true)
    public List<Role> listRoles() {
        return roleRepository.findAll(Sort.by("description"));
    }

    @Override
    @Transactional(value = "transactionManager", readOnly = true)
    public Role findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    @Transactional(value = "transactionManager", readOnly = true)
    public User findUserByName(String name) {
        User user = userRepository.findByUserName(name);
        if (user != null) {
            Hibernate.initialize(user.getRoles());
        }
        return user;
    }

    @Override
    @Transactional(value = "transactionManager", readOnly = true)
    public User findUserByNameEnabled(String name) {
        User user = userRepository.findByUserNameEnabled(name);
        if (user != null) {
            Hibernate.initialize(user.getRoles());
        }
        return user;
    }

    @Override
    @Transactional(value = "transactionManager", readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        for (User user : users.getContent()) {
            Hibernate.initialize(user.getRoles());
        }
        return users;
    }

    @Override
    @Transactional("transactionManager")
    public void auditUsernamePasswordLogin(String userName) {
        User user = userRepository.findByUserName(userName);

        if (user == null) {
            throw new UserNotFoundException("Unknown user: " + userName);
        }

        user.setLastLoginDate(new Date());

        userRepository.save(user);
    }
}
