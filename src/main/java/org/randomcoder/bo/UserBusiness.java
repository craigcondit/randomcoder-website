package org.randomcoder.bo;

import org.randomcoder.dao.Page;
import org.randomcoder.db.Role;
import org.randomcoder.db.User;
import org.randomcoder.io.Consumer;
import org.randomcoder.io.Producer;

import java.util.List;

/**
 * Business interface for user management.
 */
public interface UserBusiness {
    /**
     * Change a user's password.
     *
     * @param userName user name
     * @param password new password
     */
    void changePassword(String userName, String password);

    /**
     * Create a new user.
     *
     * @param producer user producer
     */
    void createUser(Producer<User> producer);

    /**
     * Loads a user for editing.
     *
     * @param consumer consumer
     * @param userId   id of user to load
     */
    void loadUserForEditing(Consumer<User> consumer, Long userId);

    /**
     * Update an existing user.
     *
     * @param producer user producer
     * @param userId   user id
     */
    void updateUser(Producer<User> producer, Long userId);

    /**
     * Deletes a user.
     *
     * @param userId user id to delete
     */
    void deleteUser(Long userId);

    /**
     * Marks a user as having logged in as of a particular date and time.
     *
     * @param userName user name to update
     */
    void auditUsernamePasswordLogin(String userName);

    /**
     * Lists all roles, ordered by name.
     *
     * @return List of {@code Role} objects
     */
    List<Role> listRoles();

    /**
     * Finds a {@code Role} instance with the given name.
     *
     * @param name role name
     * @return {@code Role} instance, or null if not found
     */
    Role findRoleByName(String name);

    /**
     * Finds a {@code User} with the given user name.
     *
     * @param name user name
     * @return {@code User} instance, or null if not found
     */
    User findUserByName(String name);

    /**
     * Finds an enabled {@code User} with the given user name.
     *
     * @param name user name
     * @return {@code User} instance, or null if not found or not enabled
     */
    User findUserByNameEnabled(String name);

    /**
     * Lists all {@code User} objects in range, ordered by user name.
     *
     * @param offset offset of first result
     * @param length pagen length
     * @return List of {@code User} objects
     */
    Page<User> findAll(long offset, long length);

}