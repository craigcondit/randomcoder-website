package org.randomcoder.security.spring;

import jakarta.inject.Inject;
import org.randomcoder.bo.UserBusiness;
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
    private UserBusiness userBusiness;

    /**
     * Sets the UserBusiness implementation to use.
     *
     * @param userBusiness UserBusiness implementation.
     */
    @Inject
    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userBusiness.findUserByName(username);
        if (user == null || user.getPassword() == null) {
            throw new UsernameNotFoundException(username);
        }
        return new RandomcoderUserDetails(user);
    }
}