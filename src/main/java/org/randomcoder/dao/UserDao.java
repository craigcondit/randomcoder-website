package org.randomcoder.dao;

import jakarta.inject.Inject;
import org.randomcoder.user.UserNotFoundException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.randomcoder.dao.DaoUtils.withConnection;
import static org.randomcoder.dao.DaoUtils.withTransaction;

public class UserDao {

    @Inject
    public DataSource dataSource;

    private static final String QUERY_CHANGE_PASSWORD = """
            UPDATE users SET password = ? WHERE username = ?""";

    public void changePassword(String userName, String passwordHash) {
        withConnection(dataSource, (con) -> {
            withTransaction(con, () -> {
                try (PreparedStatement ps = con.prepareStatement(QUERY_CHANGE_PASSWORD)) {
                    ps.setString(1, passwordHash);
                    ps.setString(2, userName);
                    int rows = ps.executeUpdate();
                    if (rows != 1) {
                        throw new UserNotFoundException("Unknown user: " + userName);
                    }
                }
            });
        });
    }
}
