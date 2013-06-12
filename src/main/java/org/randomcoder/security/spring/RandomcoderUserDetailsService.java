package org.randomcoder.security.spring;

import javax.inject.Inject;

import org.apache.commons.logging.*;
import org.randomcoder.bo.UserBusiness;
import org.randomcoder.user.User;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Component;

/**
 * {@link UserDetailsService} implementation which loads users from a
 * database.
 */
@Component("randomcoderUserDetailsService")
public class RandomcoderUserDetailsService implements UserDetailsService
{
	private static final Log logger = LogFactory.getLog(RandomcoderUserDetailsService.class);

	private UserBusiness userBusiness;
	private boolean debug = false;

	/**
	 * Sets the UserBusiness implementation to use.
	 * 
	 * @param userBusiness
	 *            UserBusiness implementation.
	 */
	@Inject
	public void setUserBusiness(UserBusiness userBusiness)
	{
		this.userBusiness = userBusiness;
	}

	/**
	 * Turns debug logging of ppid and issuerhash on / off.
	 * 
	 * @param debug
	 *            true if debugging is to be enabled.
	 */
	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		User user = userBusiness.findUserByName(username);
		if (user == null || user.getPassword() == null)
		{
			throw new UsernameNotFoundException(username);
		}
		return new RandomcoderUserDetails(user);
	}
}