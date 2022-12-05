package org.randomcoder.website.dao;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.randomcoder.website.func.UncheckedConsumer;
import org.randomcoder.website.data.Page;
import org.randomcoder.website.data.Tag;
import org.randomcoder.website.data.TagStatistics;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.randomcoder.website.dao.DaoUtils.withReadonlyConnection;
import static org.randomcoder.website.dao.DaoUtils.withTransaction;

@Singleton
public class TagDaoImpl implements TagDao {

    private DataSource dataSource;

    @Inject
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String INSERT = "INSERT INTO tags (\"name\", display_name) VALUES (?, ?) RETURNING tag_id";
    private static final String UPDATE = "UPDATE tags SET \"name\" = ?, display_name = ? WHERE tag_id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM tags WHERE tag_id = ?";

    private static final String SELECT_ALL = "SELECT tag_id, \"name\", display_name FROM tags";
    private static final String FIND_BY_ID = SELECT_ALL + " WHERE tag_id = ?";
    private static final String FIND_BY_NAME = SELECT_ALL + " WHERE \"name\" = ?";
    private static final String LIST_ALL = SELECT_ALL + " ORDER BY display_name";

    private static final String TAG_STATISTICS = """
            SELECT
                t.tag_id tag_id,
                t.name \"name\",
                t.display_name display_name,
                count(atl.tag_id) article_count
            FROM tags t
            LEFT JOIN article_tag_link atl ON t.tag_id = atl.tag_id
            GROUP BY t.tag_id, t.name, t.display_name
            ORDER BY t.display_name""";

    private static final String TAG_STATISTICS_PAGED = TAG_STATISTICS + " OFFSET ? LIMIT ?";
    private static final String COUNT_TAGS = "SELECT COUNT(1) FROM tags";

    private static final String MOST_ARTICLES = """
            SELECT MAX(c) article_count FROM (
                SELECT article_id, COUNT(1) c
                FROM article_tag_link
                GROUP BY article_id) x""";

    private static final String COL_TAG_ID = "tag_id";
    private static final String COL_TAG_NAME = "name";
    private static final String COL_DISPLAY_NAME = "display_name";
    private static final String COL_ARTICLE_COUNT = "article_count";

    @Override
    public Long save(Tag tag) {
        return withTransaction(dataSource, con -> (tag.getId() == null) ? createTag(con, tag) : updateTag(con, tag));
    }

    @Override
    public void deleteById(long tagId) {
        withTransaction(dataSource, con -> {
            try (PreparedStatement ps = con.prepareStatement(DELETE_BY_ID)) {
                ps.setLong(1, tagId);
                ps.executeUpdate();
            }
        });
    }

    @Override
    public Tag findById(long tagId) {
        return withReadonlyConnection(dataSource, con -> {
            return loadTag(con, FIND_BY_ID, ps -> {
                ps.setLong(1, tagId);
            });
        });
    }

    @Override
    public Tag findByName(String tagName) {
        return withReadonlyConnection(dataSource, con -> {
            return loadTag(con, FIND_BY_NAME, ps -> {
                ps.setString(1, tagName);
            });
        });
    }

    @Override
    public List<Tag> listAll() {
        return withReadonlyConnection(dataSource, con -> {
            List<Tag> tags = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(LIST_ALL)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        tags.add(populateTag(rs));
                    }
                }
            }
            return tags;
        });
    }

    @Override
    public List<TagStatistics> listAllTagStatistics() {
        return withReadonlyConnection(dataSource, con -> {
            List<TagStatistics> stats = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(TAG_STATISTICS)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        stats.add(populateTagStatistics(rs));
                    }
                }
            }
            return stats;
        });
    }

    @Override
    public Page<TagStatistics> listAllTagStatistics(long offset, long length) {
        return withReadonlyConnection(dataSource, con -> {
            long count;
            try (PreparedStatement ps = con.prepareStatement(COUNT_TAGS)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException("Failed to list tags");
                    }
                    count = rs.getLong(1);
                }
            }
            List<TagStatistics> stats = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(TAG_STATISTICS_PAGED)) {
                ps.setLong(1, offset);
                ps.setLong(2, length);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        stats.add(populateTagStatistics(rs));
                    }
                }
            }
            return new Page<>(stats, offset, count, length);
        });
    }

    @Override
    public int maxArticleCount() {
        return withReadonlyConnection(dataSource, con -> {
            try (PreparedStatement ps = con.prepareStatement(MOST_ARTICLES)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(COL_ARTICLE_COUNT);
                    }
                }
                return 0;
            }
        });
    }

    private Long createTag(Connection con, Tag tag) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(INSERT)) {
            ps.setString(1, tag.getName());
            ps.setString(2, tag.getDisplayName());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Unable to save tag");
                }
                tag.setId(rs.getLong(1));
            }
        }
        return tag.getId();
    }

    private Long updateTag(Connection con, Tag tag) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(UPDATE)) {
            ps.setString(1, tag.getName());
            ps.setString(2, tag.getDisplayName());
            ps.setLong(3, tag.getId());
            int count = ps.executeUpdate();
            if (count != 1) {
                throw new DataAccessException("Unable to save tag");
            }
        }
        return tag.getId();
    }

    private Tag loadTag(Connection con, String sql, UncheckedConsumer<PreparedStatement> callback) throws Exception {

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            callback.invoke(ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return populateTag(rs);
            }
        }
    }

    private Tag populateTag(ResultSet rs) throws SQLException {
        Tag tag = new Tag();
        tag.setId(rs.getLong(COL_TAG_ID));
        tag.setName(rs.getString(COL_TAG_NAME));
        tag.setDisplayName(rs.getString(COL_DISPLAY_NAME));
        return tag;
    }

    private TagStatistics populateTagStatistics(ResultSet rs) throws SQLException {
        TagStatistics stats = new TagStatistics();

        Tag tag = new Tag();
        tag.setId(rs.getLong(COL_TAG_ID));
        tag.setName(rs.getString(COL_TAG_NAME));
        tag.setDisplayName(rs.getString(COL_DISPLAY_NAME));
        stats.setTag(tag);

        stats.setArticleCount(rs.getInt(COL_ARTICLE_COUNT));
        return stats;
    }

}
