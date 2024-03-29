package org.randomcoder.website.dao;

import com.codahale.metrics.MetricRegistry;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.randomcoder.website.data.Role;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.randomcoder.website.dao.DaoUtils.withReadonlyConnection;

@Singleton
public class RoleDaoImpl implements RoleDao {

    @Inject
    DataSource dataSource;

    @Inject
    MetricRegistry metrics;

    private static final String COL_ROLE_ID = "role_id";
    private static final String COL_ROLE_NAME = "name";
    private static final String COL_ROLE_DESCRIPTION = "description";

    private static final String LIST_ALL_BY_DESC = """
            SELECT role_id, "name", description
            FROM roles
            ORDER BY description""";

    private static final String FIND_BY_NAME = """
            SELECT role_id, "name", description
            FROM roles
            WHERE "name" = ?""";

    @Override
    public List<Role> listByDescription() {
        try (var ignored = metrics.timer("dao.role.list.by.description").time()) {
            return withReadonlyConnection(dataSource, con -> {
                List<Role> roles = new ArrayList<>();
                try (PreparedStatement ps = con.prepareStatement(LIST_ALL_BY_DESC)) {
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            roles.add(populateRole(rs));
                        }
                    }
                }
                return roles;
            });
        }
    }

    @Override
    public Role findByName(String roleName) {
        try (var ignored = metrics.timer("dao.role.find.by.name").time()) {
            return withReadonlyConnection(dataSource, con -> {
                try (PreparedStatement ps = con.prepareStatement(FIND_BY_NAME)) {
                    ps.setString(1, roleName);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            return null;
                        }
                        return populateRole(rs);
                    }
                }
            });
        }
    }

    private Role populateRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getLong(COL_ROLE_ID));
        role.setName(rs.getString(COL_ROLE_NAME));
        role.setDescription(rs.getString(COL_ROLE_DESCRIPTION));
        return role;
    }

}
