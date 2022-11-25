package org.randomcoder.security.spring;

import jakarta.inject.Inject;
import org.randomcoder.dao.UserDao;
import org.randomcoder.db.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * {@link UserDetailsService} implementation which loads users from a database.
 */
@Component("randomcoderUserDetailsService")
public class RandomcoderUserDetailsService implements UserDetailsService {

    private UserDao userDao;

    @Inject
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByName(username, false);
        if (user == null || user.getPassword() == null) {
            throw new UsernameNotFoundException(username);
        }
        return new RandomcoderUserDetails(user);
    }

}