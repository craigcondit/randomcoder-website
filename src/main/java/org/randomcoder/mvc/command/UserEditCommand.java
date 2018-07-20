package org.randomcoder.mvc.command;

import java.util.List;

import org.randomcoder.db.Role;
import org.randomcoder.db.User;
import org.randomcoder.io.Consumer;

/**
 * Command class for editing users.
 */
public class UserEditCommand extends UserAddCommand implements Consumer<User> {
	private static final long serialVersionUID = 2923257330122456830L;

	private Long id;

	/**
	 * Gets the ID associated with this user.
	 * 
	 * @return id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the ID associated with this user.
	 * 
	 * @param id
	 *            id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void consume(User user) {
		setUserName(user.getUserName());
		setEmailAddress(user.getEmailAddress());
		setWebsite(user.getWebsite());
		setEnabled(user.isEnabled());

		List<Role> roleList = user.getRoles();
		Role[] roleArray = new Role[roleList.size()];
		roleList.toArray(roleArray);

		setRoles(roleArray);
	}
}
