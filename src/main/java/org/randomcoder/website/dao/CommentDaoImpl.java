package org.randomcoder.website.dao;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.randomcoder.website.data.ModerationStatus;
import org.randomcoder.website.data.ContentType;
import org.randomcoder.website.data.Article;
import org.randomcoder.website.data.Comment;
import org.randomcoder.website.data.Page;
import org.randomcoder.website.data.User;
import org.randomcoder.website.func.UncheckedConsumer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static org.randomcoder.website.dao.DaoUtils.withReadonlyConnection;
import static org.randomcoder.website.dao.DaoUtils.withTransaction;

@Singleton
public class CommentDaoImpl implements CommentDao {

    private DataSource dataSource;

    @Inject
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String INSERT = """
            INSERT INTO comments (
                article_id, content_type, create_user_id, create_date,
                anonymous_user_name, anonymous_email_address, anonymous_website,
                title, "content", visible, moderation_status, referrer, ip_address, user_agent)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING comment_id""";

    private static final String UPDATE = """
            UPDATE comments SET
                article_id = ?,
                content_type = ?,
                create_user_id = ?,
                create_date = ?,
                anonymous_user_name = ?,
                anonymous_email_address = ?,
                anonymous_website = ?,
                title = ?,
                "content" = ?,
                visible = ?,
                moderation_status = ?,
                referrer = ?,
                ip_address = ?,
                user_agent = ?
            WHERE comment_id = ?""";

    private static final String SELECT_ALL = """
            SELECT
                c.comment_id comment_id,
                c.article_id article_id,
                a.permalink article_permalink, 
                c.content_type content_type, 
                c.create_user_id create_user_id,
                u.username create_username,
                u.email create_email,
                u.website create_website,
                c.create_date create_date,
                c.anonymous_user_name anonymous_user_name,
                c.anonymous_email_address anonymous_email_address,
                c.anonymous_website anonymous_website,
                c.title title,
                c.content "content",
                c.visible visible,
                c.moderation_status moderation_status,
                c.referrer referrer,
                c.ip_address ip_address,
                c.user_agent user_agent
            FROM comments c
            JOIN articles a ON c.article_id = a.article_id
            LEFT OUTER JOIN users u ON c.create_user_id = u.user_id""";

    private static final String FIND_BY_ID = SELECT_ALL + " WHERE c.comment_id = ?";

    private static final String LIST_FOR_MODERATION_PAGED = SELECT_ALL + " " + """
            WHERE c.moderation_status = 'PENDING'
            ORDER BY c.create_date
            OFFSET ? LIMIT ?""";

    private static final String COUNT_FOR_MODERATION = """
            SELECT count(1) FROM comments
            WHERE moderation_status = 'PENDING'""";

    private static final String DELETE = "DELETE FROM comments WHERE comment_id = ?";

    private static final String COL_COMMENT_ID = "comment_id";
    private static final String COL_ARTICLE_ID = "article_id";
    private static final String COL_ARTICLE_PERMALINK = "article_permalink";
    private static final String COL_CONTENT_TYPE = "content_type";
    private static final String COL_CREATE_USER_ID = "create_user_id";
    private static final String COL_CREATE_USERNAME = "create_username";
    private static final String COL_CREATE_EMAIL = "create_email";
    private static final String COL_CREATE_WEBSITE = "create_website";
    private static final String COL_CREATE_DATE = "create_date";
    private static final String COL_ANON_USER_NAME = "anonymous_user_name";
    private static final String COL_ANON_EMAIL_ADDR = "anonymous_email_address";
    private static final String COL_ANON_WEBSITE = "anonymous_website";
    private static final String COL_TITLE = "title";
    private static final String COL_CONTENT = "content";
    private static final String COL_VISIBLE = "visible";
    private static final String COL_MODERATION_STATUS = "moderation_status";
    private static final String COL_REFERRER = "referrer";
    private static final String COL_IP_ADDRESS = "ip_address";
    private static final String COL_USER_AGENT = "user_agent";

    @Override
    public Comment findById(long commentId) {
        return withReadonlyConnection(dataSource, con -> {
            try (PreparedStatement ps = con.prepareStatement(FIND_BY_ID)) {
                ps.setLong(1, commentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    return populateComment(rs);
                }
            }
        });
    }

    @Override
    public Long save(Comment comment) {
        return withTransaction(dataSource, con -> (comment.getId() == null)
                ? insertComment(con, comment)
                : updateComment(con, comment));
    }

    @Override
    public void deleteById(long commentId) {
        withTransaction(dataSource, con -> {
            try (PreparedStatement ps = con.prepareStatement(DELETE)) {
                ps.setLong(1, commentId);
                ps.executeUpdate();
            }
        });
    }

    @Override
    public Page<Comment> listForModeration(long offset, long length) {
        return withReadonlyConnection(dataSource, con -> {
            return loadCommentsPaged(
                    con, offset, length, COUNT_FOR_MODERATION, LIST_FOR_MODERATION_PAGED,
                    ps -> {
                    },
                    ps -> {
                        ps.setLong(1, offset);
                        ps.setLong(2, length);
                    });
        });
    }

    private Page<Comment> loadCommentsPaged(
            Connection con,
            long offset,
            long length,
            String countSql,
            String querySql,
            UncheckedConsumer<PreparedStatement> countCallback,
            UncheckedConsumer<PreparedStatement> queryCallback) throws Exception {
        long count;
        try (PreparedStatement ps = con.prepareStatement(countSql)) {
            countCallback.invoke(ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Unable to retrieve comments");
                }
                count = rs.getLong(1);
            }
        }
        var comments = loadComments(con, querySql, queryCallback);
        return new Page<>(comments, offset, count, length);
    }

    private List<Comment> loadComments(
            Connection con,
            String sql,
            UncheckedConsumer<PreparedStatement> callback) throws Exception {
        List<Comment> comments = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            callback.invoke(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comments.add(populateComment(rs));
                }
            }
        }
        return comments;
    }

    private Long insertComment(Connection con, Comment comment) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(INSERT)) {
            addSaveParams(ps, comment);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Unable to save comment");
                }
                comment.setId(rs.getLong(1));
            }
            return comment.getId();
        }
    }

    private Long updateComment(Connection con, Comment comment) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(UPDATE)) {
            addSaveParams(ps, comment);
            ps.setLong(15, comment.getId());
            int count = ps.executeUpdate();
            if (count != 1) {
                throw new DataAccessException("Unable to save comment");
            }
            return comment.getId();
        }
    }

    private void addSaveParams(PreparedStatement ps, Comment comment) throws SQLException {
        ps.setLong(1, comment.getArticle().getId());
        ps.setString(2, comment.getContentType().name());
        User createdBy = comment.getCreatedByUser();
        if (createdBy == null) {
            ps.setNull(3, Types.BIGINT);
        } else {
            ps.setLong(3, createdBy.getId());
        }
        ps.setTimestamp(4, new Timestamp(comment.getCreationDate().getTime()));
        ps.setString(5, comment.getAnonymousUserName());
        ps.setString(6, comment.getAnonymousEmailAddress());
        ps.setString(7, comment.getAnonymousWebsite());
        ps.setString(8, comment.getTitle());
        ps.setString(9, comment.getContent());
        ps.setBoolean(10, comment.isVisible());
        ps.setString(11, comment.getModerationStatus().name());
        ps.setString(12, comment.getReferrer());
        ps.setString(13, comment.getIpAddress());
        ps.setString(14, comment.getUserAgent());
    }

    private Comment populateComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong(COL_COMMENT_ID));
        comment.setContentType(ContentType.valueOf(rs.getString(COL_CONTENT_TYPE)));

        Article article = new Article();
        article.setId(rs.getLong(COL_ARTICLE_ID));
        article.setPermalink(rs.getString(COL_ARTICLE_PERMALINK));
        comment.setArticle(article);

        long createdById = rs.getLong(COL_CREATE_USER_ID);
        if (!rs.wasNull()) {
            User user = new User();
            user.setId(createdById);
            user.setUserName(COL_CREATE_USERNAME);
            user.setEmailAddress(COL_CREATE_EMAIL);
            user.setWebsite(COL_CREATE_WEBSITE);
            comment.setCreatedByUser(user);
        }
        comment.setCreationDate(rs.getTimestamp(COL_CREATE_DATE));
        comment.setAnonymousUserName(rs.getString(COL_ANON_USER_NAME));
        comment.setAnonymousEmailAddress(rs.getString(COL_ANON_EMAIL_ADDR));
        comment.setAnonymousWebsite(rs.getString(COL_ANON_WEBSITE));
        comment.setTitle(rs.getString(COL_TITLE));
        comment.setContent(rs.getString(COL_CONTENT));
        comment.setVisible(rs.getBoolean(COL_VISIBLE));
        comment.setModerationStatus(ModerationStatus.valueOf(rs.getString(COL_MODERATION_STATUS)));
        comment.setReferrer(rs.getString(COL_REFERRER));
        comment.setIpAddress(rs.getString(COL_IP_ADDRESS));
        comment.setUserAgent(rs.getString(COL_USER_AGENT));
        return comment;
    }

}
