package org.randomcoder.dao;

import jakarta.inject.Inject;
import org.randomcoder.db.Role;
import org.randomcoder.db.User;
import org.randomcoder.user.UserNotFoundException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.randomcoder.dao.DaoUtils.withTransaction;

@Component("userDao")
public class UserDaoImpl implements UserDao {

    private DataSource dataSource;

    @Inject
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String CHANGE_PASSWORD = """
            UPDATE users SET password = ? WHERE username = ?""";

    private static final String DELETE_BY_ID = """
            DELETE from users where user_id = ?""";

    private static final String CREATE = """
            INSERT INTO users (
                username, password, email, enabled, website
            ) VALUES (?, ?, ?, ?, ?)
            RETURNING user_id""";

    private static final String UPDATE = """
            UPDATE USERS SET
                password = ?, email = ?, enabled = ?, website = ?
            WHERE user_id = ?""";

    private static final String SELECT_ALL = """
            SELECT 
                user_id, username, password, email, enabled, login_date, website
            FROM users""";

    private static final String FIND_BY_ID = SELECT_ALL + " WHERE user_id = ?";
    private static final String FIND_BY_NAME = SELECT_ALL + " WHERE username = ?";
    private static final String FIND_BY_NAME_ENABLED = FIND_BY_NAME + " AND enabled = true";

    private static final String FIND_ROLES_BY_USER_ID = """
            SELECT r.role_id role_id, r.name name, r.description description
            FROM ROLES r
            JOIN USER_ROLE_LINK l ON r.role_id = l.role_id
            WHERE l.user_id = ?
            ORDER BY r.name""";

    private static final String LIST_ROLES_FOR_USER_PAGED = """
            SELECT url.user_id user_id, r.role_id role_id, r.name name, r.description description
            FROM roles r
            JOIN user_role_link url ON r.role_id = url.role_id
            WHERE url.user_id IN (
                SELECT user_id FROM users ORDER BY username OFFSET ? LIMIT ?)
            ORDER BY r.description""";

    private static final String COUNT_ALL = """
            SELECT count(1) FROM users""";

    private static final String LIST_ALL_BY_NAME_PAGED = SELECT_ALL + " ORDER BY username OFFSET ? LIMIT ?";

    private static final String UPDATE_LOGIN_TIME = """
            UPDATE users SET login_date = now() WHERE username = ?""";

    private static final String DELETE_ROLE_LINK_BY_USER_ID = """
            DELETE FROM user_role_link WHERE user_id = ?""";

    private static final String INSERT_ROLE_LINK = """
            INSERT INTO user_role_link (user_id, role_id) VALUES (?,?)""";

    private static final String COL_USER_ID = "user_id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_EMAIL = "email";
    private static final String COL_ENABLED = "enabled";
    private static final String COL_LOGIN_DATE = "login_date";
    private static final String COL_WEBSITE = "website";

    private static final String COL_ROLE_ID = "role_id";
    private static final String COL_ROLE_NAME = "name";
    private static final String COL_ROLE_DESCRIPTION = "description";

    @Override
    public Long save(User user) {
        return withTransaction(dataSource, con -> (user.getId() == null) ? createUser(user, con) : updateUser(user, con));
    }

    @Override
    public Page<User> listByName(long offset, long length, boolean includeRoles) {
        return withTransaction(dataSource, con -> {
            long count;
            List<User> users = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(COUNT_ALL)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException("Unable to retrieve users");
                    }
                    count = rs.getLong(1);
                }
            }
            try (PreparedStatement ps = con.prepareStatement(LIST_ALL_BY_NAME_PAGED)) {
                ps.setLong(1, offset);
                ps.setLong(2, length);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        users.add(populateUser(rs));
                    }
                }
            }
            if (includeRoles) {
                Map<Long, User> userMap = new HashMap<>();
                for (User user : users) {
                    user.setRoles(new ArrayList<>());
                    userMap.put(user.getId(), user);
                }
                try (PreparedStatement ps = con.prepareStatement(LIST_ROLES_FOR_USER_PAGED)) {
                    ps.setLong(1, offset);
                    ps.setLong(2, length);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            Role role = populateRole(rs);
                            Long userId = rs.getLong("user_id");
                            User user = userMap.get(userId);
                            if (user != null) {
                                user.getRoles().add(role);
                            }
                        }
                    }
                }
            }
            return new Page(users, offset, count, length);
        });
    }

    @Override
    public User findByName(String userName, boolean includeDisabled, boolean includeRoles) {
        return withTransaction(dataSource, con -> {
            User user;
            try (PreparedStatement ps = con.prepareStatement(includeDisabled ? FIND_BY_NAME : FIND_BY_NAME_ENABLED)) {
                ps.setString(1, userName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    user = populateUser(rs);
                }
            }
            if (includeRoles) {
                populateRoles(con, user);
            }
            return user;
        });
    }

    @Override
    public User findById(long userId, boolean includeRoles) {
        return withTransaction(dataSource, con -> {
            User user;
            try (PreparedStatement ps = con.prepareStatement(FIND_BY_ID)) {
                ps.setLong(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    user = populateUser(rs);
                }
            }
            if (includeRoles) {
                populateRoles(con, user);
            }
            return user;
        });
    }

    @Override
    public void deleteById(long userId) {
        withTransaction(dataSource, con -> {
            try (PreparedStatement ps = con.prepareStatement(DELETE_BY_ID)) {
                ps.setLong(1, userId);
                ps.executeUpdate();
            }
        });
    }

    @Override
    public void changePassword(String userName, String passwordHash) {
        withTransaction(dataSource, con -> {
            try (PreparedStatement ps = con.prepareStatement(CHANGE_PASSWORD)) {
                ps.setString(1, passwordHash);
                ps.setString(2, userName);
                int rows = ps.executeUpdate();
                if (rows != 1) {
                    throw new UserNotFoundException("Unknown user: " + userName);
                }
            }
        });
    }

    @Override
    public void updateLoginTime(String userName) {
        withTransaction(dataSource, con -> {
            try (PreparedStatement ps = con.prepareStatement(UPDATE_LOGIN_TIME)) {
                ps.executeUpdate();
            }
        });
    }

    private Long createUser(User user, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(CREATE)) {
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmailAddress());
            ps.setBoolean(4, user.isEnabled());
            ps.setString(5, user.getWebsite());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Failed to save user");
                }
                user.setId(rs.getLong(1));
            }
        }
        populateRoleLink(user, con);
        return user.getId();
    }

    private Long updateUser(User user, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(UPDATE)) {
            ps.setString(1, user.getPassword());
            ps.setString(2, user.getEmailAddress());
            ps.setBoolean(3, user.isEnabled());
            ps.setString(4, user.getWebsite());
            ps.setLong(5, user.getId());
            if (ps.executeUpdate() != 1) {
                throw new DataAccessException("User not found with ID: " + user.getId());
            }
        }
        deleteRoleLink(user, con);
        populateRoleLink(user, con);
        return user.getId();
    }

    private void deleteRoleLink(User user, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(DELETE_ROLE_LINK_BY_USER_ID)) {
            ps.setLong(1, user.getId());
            ps.executeUpdate();
        }
    }

    private void populateRoleLink(User user, Connection con) throws SQLException {
        if (user.getRoles().isEmpty()) {
            return;
        }
        try (PreparedStatement ps = con.prepareStatement(INSERT_ROLE_LINK)) {
            long userId = user.getId();
            for (Role role : user.getRoles()) {
                ps.setLong(1, userId);
                ps.setLong(2, role.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private User populateUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong(COL_USER_ID));
        user.setUserName(rs.getString(COL_USERNAME));
        user.setPassword(rs.getString(COL_PASSWORD));
        user.setEmailAddress(rs.getString(COL_EMAIL));
        user.setEnabled(rs.getBoolean(COL_ENABLED));
        user.setLastLoginDate(rs.getTimestamp(COL_LOGIN_DATE));
        user.setWebsite(rs.getString(COL_WEBSITE));
        return user;
    }

    private Role populateRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getLong(COL_ROLE_ID));
        role.setName(rs.getString(COL_ROLE_NAME));
        role.setDescription(rs.getString(COL_ROLE_DESCRIPTION));
        return role;
    }

    private void populateRoles(Connection con, User user) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(FIND_ROLES_BY_USER_ID)) {
            ps.setLong(1, user.getId());
            try (ResultSet rs = ps.executeQuery()) {
                List<Role> roles = new ArrayList<>();
                while (rs.next()) {
                    roles.add(populateRole(rs));
                }
                user.setRoles(roles);
            }
        }
    }

}
